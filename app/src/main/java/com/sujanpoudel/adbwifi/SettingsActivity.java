package com.sujanpoudel.adbwifi;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import static com.sujanpoudel.adbwifi.Utils.darkTheme;


public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (darkTheme(getApplicationContext())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Utils.setupDarkStatusBar(this);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Utils.setupWhiteStatusBar(this);
        }
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    public static class SettingFragment extends PreferenceFragmentCompat {
        static boolean shouldRecreateMainActivity = false;
        Preference.OnPreferenceChangeListener portPreferenceChangeListener
                = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                int intVal = Integer.parseInt(o.toString());
                if (intVal < 1023 | intVal > 65535) {
                    Toast.makeText(getContext(), o.toString() + " is not allowed value", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    preference.setSummary("TCP port is " + o.toString());
                    return true;
                }
            }
        };
        Preference.OnPreferenceChangeListener themePreferenceChangeListener
                = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().recreate();
                        shouldRecreateMainActivity = true;
                    }
                }, 10);
                return true;
            }
        };

        @Override
        public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preference);
            getPreferenceScreen().findPreference(getString(R.string.key_dark_theme)).setOnPreferenceChangeListener(themePreferenceChangeListener);
            bindPreferencesToSummary(findPreference(getString(R.string.key_port)));
        }

        private void bindPreferencesToSummary(Preference preference) {
            preference.setOnPreferenceChangeListener(portPreferenceChangeListener);
            portPreferenceChangeListener.onPreferenceChange(preference, Utils.getPort(getContext()));
        }
    }

}
