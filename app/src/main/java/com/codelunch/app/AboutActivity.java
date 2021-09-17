package com.codelunch.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Show back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set Version
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            String version = pInfo.versionName;
            ((TextView)findViewById(R.id.versionTextView)).setText('\t' + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickDiscordLink(View v) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.discordLink))));
        } catch (ActivityNotFoundException e) {}
    }

    public void onClickRedditLink(View v) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.redditLink))));
        } catch (ActivityNotFoundException e) {};
    }
}