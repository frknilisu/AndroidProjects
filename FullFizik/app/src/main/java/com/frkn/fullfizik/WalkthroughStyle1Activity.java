package com.frkn.fullfizik;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

public class WalkthroughStyle1Activity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager viewPager;
    private View indicator1;
    private View indicator2;
    private View indicator3;
    private View indicator4;
    private View btnStart;

    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walkthrough1_layout);

        indicator1 = findViewById(R.id.indicator1);
        indicator2 = findViewById(R.id.indicator2);
        indicator3 = findViewById(R.id.indicator3);
        indicator4 = findViewById(R.id.indicator4);
        btnStart = findViewById(R.id.btnGetStarted);

        btnStart.setEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewPager2);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new WizardPageChangeListener());
        viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.walkthrough1_viewpager_margin));
        viewPager.setOffscreenPageLimit(4);

        setup();

        updateIndicators(0);
    }

    private void setup() {
        Log.d("SplashScreen", "setup()");
        loadSettings();
        if (!downloadFlag) {
            reloadInception();
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    btnStart.setEnabled(true);
                }
            }, SPLASH_TIME_OUT);
        }
    }

    private void reloadInception() {
        Log.d("SplashScreen", "reloadInception()");
        download_inception_json();
    }

    public void download_inception_json() {
        Log.d("Functions", "download_inception_json()..");
        String URL = "https://www.dropbox.com/s/u6q6uno2ro50mv4/inception.json?dl=1";
        DownloaderAsync downloaderAsync = new DownloaderAsync();
        downloaderAsync.setContext(WalkthroughStyle1Activity.this);
        downloaderAsync.setListener(downloadListener);
        downloaderAsync.setProcessDialogFlag(false);
        downloaderAsync.setProcessMessage("Downloading inception json..");
        downloaderAsync.setParentFolderName(null);
        downloaderAsync.setFileName("inception");
        downloaderAsync.setFileExtension(".json");
        downloaderAsync.setFileLength(2607);
        downloaderAsync.execute(URL);
    }

    DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onTaskCompleted(String response) {
            Log.d("SplashScreen", "downloadListener: " + response);
            downloadFlag = true;
            saveDownloadFlag();
            btnStart.setEnabled(true);
        }

        @Override
        public void onTaskFailed(String response) {
            Log.d("SplashScreen", "onTaskFailed: " + response);
            downloadFlag = false;
            saveDownloadFlag();
        }
    };

    private void startMainActivity() {
        Intent i = new Intent(WalkthroughStyle1Activity.this, ActivityStyle13Activity.class);
        startActivity(i);
        finish();
    }

    public void updateIndicators(int position) {
        switch (position) {
            case 0:
                indicator1.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot));
                indicator2.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator3.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator4.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                break;
            case 1:
                indicator1.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator2.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot));
                indicator3.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator4.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                break;
            case 2:
                indicator1.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator2.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator3.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot));
                indicator4.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                break;
            case 3:
                indicator1.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator2.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator3.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator4.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGetStarted:
                Toast.makeText(this, "Button Get Started clicked!", Toast.LENGTH_SHORT).show();
                startMainActivity();
                break;
            default:
                break;
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private int WIZARD_PAGES_COUNT = 4;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new WalkthroughStyle1Fragment(position);
        }

        @Override
        public int getCount() {
            return WIZARD_PAGES_COUNT;
        }

    }

    private class WizardPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int position) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {
            updateIndicators(position);
        }
    }

    /*************************************************************************************
     * ************************************************************************************
     * Shared Preferences Functions
     * ************************************************************************************
     *************************************************************************************/
    private static final String PREFS_NAME = "MySharedPrefName";
    SharedPreferences settings;

    private boolean restoreFlag = false;
    private boolean downloadFlag = false;

    private void saveRestoreFlag() {
        SharedPreferences.Editor editor;
        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putBoolean("restoreIsOk", restoreFlag);
        editor.commit();
    }

    private void saveDownloadFlag() {
        SharedPreferences.Editor editor;
        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putBoolean("downloadIsOk", downloadFlag);
        editor.commit();
    }

    private void saveAccountType() {
        SharedPreferences.Editor editor;
        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Data.accountType);
        editor.putString("accountType", json);
        editor.commit();
    }

    private void loadSettings() {
        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = settings.getString("accountType", "");
        if(json != ""){
            Data.accountType = gson.fromJson(json, Data.Account.class);
        } else{
            Data.accountType = Data.Account.BASIC;
        }
        restoreFlag = settings.getBoolean("restoreIsOk", false);
        downloadFlag = settings.getBoolean("downloadIsOk", false);
    }
}
