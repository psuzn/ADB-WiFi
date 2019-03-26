package com.sujanpoudel.adbwifi;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static android.content.Context.WIFI_SERVICE;

class Utils {
    public static  String BROADCAST_ACTION ="com.sujanpoudel.adbwifi.UPDATE_ACTION";
    public static  String BROADCAST_INT_KEY = "enable";

    private static final String TAG = "Uils adb wifis";
    private static String DEFAULT_PORT = "5555";

    static Boolean darkTheme(Context context){
        return PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(context.getApplicationContext())).
                getBoolean(context.getString(R.string.key_dark_theme), false);
    }
    static String getPort(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(context.getApplicationContext())).
                getString(context.getString(R.string.key_port), DEFAULT_PORT);
    }

    static String getIpAndPort(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress()) + ":" + getPort(context);
    }

    static int startAdb(Context context) throws Exception {
        List<String> commandStart = Arrays.asList("setprop service.adb.tcp.port " + getPort(context), "stop adbd", "start adbd", "exit");
        return doCommands(commandStart);
    }

    static int stopAdb() throws Exception {
        List<String> commandStop = Arrays.asList("setprop service.adb.tcp.port '' ", "stop adbd", "start adbd", "exit");
        return doCommands(commandStop);
    }

    private static int doCommands(List<String> cmds) throws Exception {
        Process process = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(process.getOutputStream());

        for (String tmpCmd : cmds) {
            os.writeBytes(tmpCmd + "\n");
        }
        os.writeBytes("exit\n");
        os.flush();
        os.close();

        process.waitFor();
        System.out.println(process.exitValue());
        return process.exitValue();
    }

    private static String getSystemProps(String propName) {
        Process process = null;
        BufferedReader bufferedReader = null;

        try {
            process = new ProcessBuilder().command("getprop", propName).redirectErrorStream(true).start();
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = bufferedReader.readLine();
            if (line == null) {
                line = ""; //prop not set
            }
            Log.i(TAG, "read System Property: " + propName + "=" + line);
            return line;
        } catch (Exception e) {
            Log.e(TAG, "Failed to read System Property " + propName, e);
            return "";
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    static boolean isAdbWifiEnabled() {
        String port = getSystemProps("service.adb.tcp.port");
        String AdbEnabled = getSystemProps("init.svc.adbd");
        return (!port.equals("")) & AdbEnabled.equals("running");
    }

}
