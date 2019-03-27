package com.sujanpoudel.adbwifi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;


import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.util.IntentUtil;
import com.vansuita.materialabout.views.AboutView;

import static com.sujanpoudel.adbwifi.Utils.darkTheme;

public class AboutMeActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_about_me);
        setSupportActionBar((Toolbar) findViewById(R.id.action_bar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LinearLayout linearLayout = findViewById(R.id.parent_linearlayout);
        linearLayout.addView(getAboutView());

    }

    View getAboutView() {
        IntentUtil util = new IntentUtil(this);
        AboutView view = AboutBuilder.with(this)
                .setPhoto(R.drawable.me)
                .setName("Sujan Poudel")
                .setSubTitle("Game Developer | Android Developer | Web Developer")
                .setBrief("I am a android developer, fullstack web developer and also game developer available for freelancing as well as fulltime projects. ")
                .setAppIcon(R.mipmap.ic_launcher_round)
                .setAppName(R.string.app_name)
                .setActionsColumnsCount(4)
                .addGooglePlayStoreLink("8002078663318221363")
                .addGitHubLink("psuzn")
                .addTwitterLink("psuzn")
                .addLinkedInLink("psujan")
                .addEmailLink("psuzzn@gmail.com")
                .setActionsColumnsCount(3)
                .addAction(R.mipmap.star, "Rate this app", util.openPlayStoreAppPage(getPackageName()))
                .addAction(R.mipmap.feedback, "Give Feedback", util.sendEmail("psuzzn@gmail.com", "Feedback for Adb Wifi app", ""))
                .addShareAction(R.string.app_name)
                .setVersionNameAsAppSubTitle()
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .setBackgroundColor(getColor(R.color.background))
                .build();
        return view;
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

}
