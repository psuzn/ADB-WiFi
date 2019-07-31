package com.sujanpoudel.adbwifi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
        Bitmap photo = BitmapFactory.decodeResource(getResources(), R.drawable.me);
        System.out.println(photo.toString());
        AboutView view = AboutBuilder.with(this)
                .setPhoto(photo)
                .setName("Sujan Poudel")
                .setSubTitle("Game Developer | Android Developer | Web Developer")
                .setBrief("I am a android developer, fullstack web developer and also game developer available for freelancing as well as fulltime projects. ")
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
                .setActionsColumnsCount(4)
                .addGooglePlayStoreLink("5547061053835697166")
                .addGitHubLink("psuzn")
                .addTwitterLink("psuzn")
                .addLinkedInLink("psujan")
                .addEmailLink("psuzzn@gmail.com")
                .setActionsColumnsCount(2)
                .addAction(R.mipmap.star, "Rate", util.openPlayStoreAppPage(getPackageName()))
                .addAction(R.mipmap.feedback, "Feedback", util.sendEmail("psuzzn@gmail.com", "Feedback for Adb Wifi app", ""))
                .setVersionNameAsAppSubTitle()
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.background))
                .build();
        return view;
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

}
