package com.sujanpoudel.adbwifi;


import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import java.io.IOException;

import static com.sujanpoudel.adbwifi.Utils.getIpAndPort;
import static com.sujanpoudel.adbwifi.Utils.isAdbWifiEnabled;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Tile extends TileService {
    static class Status {
        static int Enabled = 1;
        static int Disabled = 2;
    }

    @Override
    public void onClick() {
        android.service.quicksettings.Tile tile = getQsTile();
        Intent broadCastIntent = new Intent("com.sujanpoudel.adbwifi.UPDATE_ACTION");

        if (tile.getState() == android.service.quicksettings.Tile.STATE_ACTIVE) {
            try {
                onTileDisable();
                Utils.stopAdb();
                broadCastIntent.putExtra(Utils.BROADCAST_INT_KEY, Status.Disabled);
            } catch (Exception e) {
                tile.setState(android.service.quicksettings.Tile.STATE_ACTIVE);
                Toast.makeText(getApplicationContext(), "ADB over Wifi could not be stopped", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            try {
                onTileEnable();
                Utils.startAdb(getApplicationContext());
                broadCastIntent.putExtra(Utils.BROADCAST_INT_KEY, Status.Enabled);
            } catch (Exception e) {
                tile.setState(android.service.quicksettings.Tile.STATE_INACTIVE);
                Toast.makeText(getApplicationContext(), "ADB over Wifi could not be started", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        tile.updateTile();
        sendBroadcast(broadCastIntent);
    }

    void onTileDisable() {
        android.service.quicksettings.Tile tile = getQsTile();
        tile.setContentDescription("ADB WIFI");
        tile.setState(android.service.quicksettings.Tile.STATE_INACTIVE);
        tile.setLabel("ADB WIFI");
        tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.adb_wifi_off));
        tile.updateTile(); // first update tile and perform operation for responsiveness
    }

    void onTileEnable() {
        android.service.quicksettings.Tile tile = getQsTile();
        tile.setState(android.service.quicksettings.Tile.STATE_ACTIVE);
        tile.setContentDescription(getIpAndPort(getApplicationContext()));
        tile.setLabel(getIpAndPort(getApplicationContext()));
        tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.adb_wifi_enabled));
        tile.updateTile(); // first update tile and perform operation for responsiveness
    }

    @Override
    public void onTileAdded() {
        android.service.quicksettings.Tile tile = getQsTile();
        if (eu.chainfire.libsuperuser.Shell.SU.available()) {
            try {
                Runtime.getRuntime().exec("su");
                if (isAdbWifiEnabled()) {
                    onTileEnable();
                } else {
                    onTileDisable();
                }
            } catch (IOException e) {
                tile.setState(android.service.quicksettings.Tile.STATE_UNAVAILABLE);
            }
        } else
            getQsTile().setState(android.service.quicksettings.Tile.STATE_UNAVAILABLE);
        getQsTile().updateTile();
    }


    @Override
    public void onStartListening() {
        if (getQsTile() != null)
            onTileAdded();
    }
}
