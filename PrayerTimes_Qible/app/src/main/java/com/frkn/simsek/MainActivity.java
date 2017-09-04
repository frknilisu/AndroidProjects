package com.frkn.simsek;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    TimeUpdater timeUpdater;
    public static ShowTimes showTimes;

    String[] permissions = new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
    private static final String PREFS_NAME = "MySharedPrefName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showTimes = new ShowTimes(this, MainActivity.this);

        if (!checkPermissionWithSharedPref()) {
            takePermissions();
        } else {
            Log.d("Permission", "already have permissions");
            setup();
        }
    }

    private boolean checkPermissionWithSharedPref() {
        SharedPreferences settings;
        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean("Permissions", false);
    }

    private void setup() {

        if (loadLastSettings()) {   //  no settings(city, country vs.) change

            /*if (timeUpdater != null && timeUpdater.showTimes != null && timeUpdater.showTimes.handler != null) {
                Log.d("setup()", "timeUpdater's showTimes not null");
                timeUpdater.showTimes.stopTimer();
                timeUpdater.showTimes = null;
            }*/
            if(showTimes == null){
                Log.d("setup()", "showTime is null");
                showTimes = new ShowTimes(this, MainActivity.this);
                showTimes.updateUI();
            } else if(showTimes.handler == null){
                Log.d("setup()", "showTime not null, but timer is null");
                showTimes.updateUI();
            } else {
                Log.d("setup()", "showTime timer not null");
                showTimes.stopTimer();
                showTimes.updateUI();
            }

            Log.d("setup()", "timeUpdater(2) start");
            timeUpdater = new TimeUpdater(2, this, MainActivity.this);
            timeUpdater.run();
        } else {     //  time updater will run first time. No times data before this.
            Log.d("setup()", "timeUpdater(1) start");
            timeUpdater = new TimeUpdater(1, this, MainActivity.this);
            timeUpdater.run();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_sec:
                SelectCity fragment = new SelectCity();
                FragmentManager fm = getSupportFragmentManager();
                fragment.show(fm, "Select City");
                return true;
            case R.id.action_kible:
                startActivity(new Intent(MainActivity.this, Qibla.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop()");
        if (!isMyServiceRunning(BackgroundService.class))
            startBackgroundService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy()");
        if (!isMyServiceRunning(BackgroundService.class))
            startBackgroundService();
    }

    private void startBackgroundService() {
        Intent intent = new Intent(MainActivity.this, BackgroundService.class);
        startService(intent);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (Functions.country_flag && Functions.city_flag) {
            Log.d("onDismiss", "country and city are selected successfully");
            System.out.println(Functions.url);
            saveSettings();
            /*if (timeUpdater != null && timeUpdater.showTimes != null && timeUpdater.showTimes.handler != null) {
                Log.d("onDismiss", "timeUpdater's showTimes not null");
                timeUpdater.showTimes.stopTimer();
                timeUpdater.showTimes = null;
            }*/

            Log.d("setup()", "timeUpdater(1) start");
            timeUpdater = new TimeUpdater(1, this, MainActivity.this);
            timeUpdater.run();
        } else {
            Functions.country_flag = false;
            Functions.city_flag = false;
            Log.d("onDismiss", "Error: country and city selection is canceled");
            setup();
        }
    }

    /**********************************************************************************
     * TAKE PERMISSIONS
     *************************************************************************************/

    private void takePermissions() {
        Log.d("Permission", "takePermissions()");
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            } else {
                Log.d("Permission", "already have permissions");
                savePermissionTaken();
                setup();
            }
        } else {
            setup();
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        for (int i = 0; i < permissions.length; i++) {
            int result = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, permissions, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean flag = true;
        switch (requestCode) {
            case 101:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //not granted
                        Log.d("Permission", "No permission: " + permissions[i].toString());
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    //granted
                    Log.d("Permission", "all permissions are taken");
                    savePermissionTaken();
                    setup();
                } else {
                    Log.d("Permission", "some permission has no");
                    takePermissions();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /*********************
     * SharedPreferences Process
     **************************/

    private void savePermissionTaken() {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putBoolean("Permissions", true);
        editor.commit();
    }

    private void saveSettings() {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putBoolean("HaveLastSetting", true);
        editor.putString("CityId", Functions.cityid);
        editor.putString("CityName", Functions.cityname);
        editor.putString("CountryName", Functions.countryname);
        editor.putString("Url", Functions.url);
        editor.commit();
    }

    private boolean loadLastSettings() {
        SharedPreferences settings;
        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Functions.existLastSetting = settings.getBoolean("HaveLastSetting", false);
        Functions.cityid = settings.getString("CityId", "5041");
        Functions.cityname = settings.getString("CityName", "ISTANBUL");
        Functions.countryname = settings.getString("CountryName", "TURKIYE");
        Functions.url = settings.getString("Url", "https://namazvakitleri.com.tr/" + "sehir/" + Functions.cityid + "/" + Functions.cityname + "/" + Functions.countryname);
        Log.d("LastSettings", "HaveLastSetting: " + String.valueOf(Functions.existLastSetting));
        Log.d("LastSettings", "CityId: " + Functions.cityid);
        Log.d("LastSettings", "CityName: " + Functions.cityname);
        Log.d("LastSettings", "CountryName: " + Functions.countryname);
        Log.d("LastSettings", "Url: " + Functions.url);

        return Functions.existLastSetting;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
