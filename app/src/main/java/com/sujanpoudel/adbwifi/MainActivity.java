package com.sujanpoudel.adbwifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.quicksettings.TileService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


import static com.sujanpoudel.adbwifi.Utils.darkTheme;
import static com.sujanpoudel.adbwifi.Utils.getIpAndPort;
import static com.sujanpoudel.adbwifi.Utils.isAdbWifiEnabled;
import static com.sujanpoudel.adbwifi.Utils.startAdb;
import static com.sujanpoudel.adbwifi.Utils.stopAdb;

public class MainActivity extends AppCompatActivity {

    String runTextSuccess = "Run :";
    String runTextFailedNoSuperSu = "App needs rooted phone ro run";
    String runTextFailedNoSuperSuPermission = "App needs root permission ro run";
    String runTextFailedUnknownError = "Something Went Wrong";
    String IPTextSuccess = "adb connect ";


    Switch runSwitch;
    TextView runText, IPText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (darkTheme(getApplicationContext())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Utils.setupDarkStatusBar(this);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Utils.setupWhiteStatusBar(this);
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        runSwitch = findViewById(R.id.run_switch);
        runText = findViewById(R.id.run_text);
        IPText = findViewById(R.id.ip_text);
        runSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        new Handler().postDelayed(postDelayedRunnable, 300);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.about_me) {
            Intent i = new Intent(MainActivity.this, AboutMeActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SettingsActivity.SettingFragment.shouldRecreateMainActivity) {
            SettingsActivity.SettingFragment.shouldRecreateMainActivity = false;
            recreate();
        }
    }

    void onAdbWifiSuccess() {
        runText.setText(runTextSuccess);
        runSwitch.setChecked(true);
        runText.setTextColor(getResources().getColor(R.color.normalTextColor));
        IPText.setText(IPTextSuccess + getIpAndPort(getApplicationContext()));
        runText.setVisibility(View.VISIBLE);
        IPText.setVisibility(View.VISIBLE);
    }

    void onAdbWifiFailed(String error) {
        runText.setText(error);
        runText.setTextColor(Color.RED);
        runText.setVisibility(View.VISIBLE);
        IPText.setVisibility(View.GONE);
    }

    void onAdbWifiStop() {
        runText.setVisibility(View.GONE);
        IPText.setVisibility(View.GONE);
        runSwitch.setChecked(false);
    }

    Runnable postDelayedRunnable = new Runnable() {
        @Override
        public void run() {
            if (!eu.chainfire.libsuperuser.Shell.SU.available()) {
                runText.setText(runTextFailedNoSuperSu);
                return;
            }
            try {
                Runtime.getRuntime().exec("su");
                runText.setVisibility(View.GONE);
                runSwitch.setClickable(true);
                if (isAdbWifiEnabled()) {
                    onAdbWifiSuccess();
                } else {
                    onAdbWifiStop();
                }
            } catch (IOException e) {
                onAdbWifiFailed(runTextFailedNoSuperSuPermission);
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TileService.requestListeningState(getApplicationContext(), new ComponentName(getApplicationContext(), Tile.class));
            }
            IntentFilter intentFilter = new IntentFilter(Utils.BROADCAST_ACTION);

            registerReceiver(new UpdateBroadcastReceiver(), intentFilter);

        }
    };
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                try {
                    if (startAdb(getApplicationContext()) == 0)
                        onAdbWifiSuccess();

                } catch (Exception c) {
                    c.printStackTrace();
                    onAdbWifiFailed(runTextFailedUnknownError);
                }
            } else {
                try {
                    if (stopAdb() == 0)
                        onAdbWifiStop();
                } catch (Exception c) {
                    c.printStackTrace();
                    onAdbWifiFailed(runTextFailedUnknownError);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TileService.requestListeningState(getApplicationContext(), new ComponentName(getApplicationContext(), Tile.class));
            }
        }
    };

    public class UpdateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Received", Toast.LENGTH_SHORT);

            if (intent.getIntExtra(Utils.BROADCAST_INT_KEY, -1) == Tile.Status.Enabled)
                onAdbWifiSuccess();
            else if (intent.getIntExtra(Utils.BROADCAST_INT_KEY, -1) == Tile.Status.Disabled)
                onAdbWifiStop();
            System.out.println("Broadcast Received with int value:" + intent.getIntExtra(Utils.BROADCAST_INT_KEY, -1));
        }
    }

}
