package com.sujanpoudel.adbwifi;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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

    static  void setupWhiteStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            activity.getWindow().setStatusBarColor(0x00000000);
            activity.getWindow().setNavigationBarColor(0x00000000);
        }
    }

    static void setupDarkStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
            activity.getWindow().setStatusBarColor(0x00000000);
            activity.getWindow().setNavigationBarColor(0x00000000);
        }
    }

    static  void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
