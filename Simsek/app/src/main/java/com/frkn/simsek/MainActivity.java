package com.frkn.simsek;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    TimeUpdater timeUpdater;
    ShowTimes showTimes;
    SelectCity fragment = new SelectCity();

    String[] permissions = new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FragmentManager fm = getSupportFragmentManager();
        Button but = (Button) findViewById(R.id.btn);

        // Capture button clicks
        but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                fragment.show(fm, "Dialog Fragment");
                //Intent intent = new Intent(MainActivity.this, SearchCity.class);
                //startActivity(intent);
            }
        });

        takePermissions();

    }

    private void setup(){
        timeUpdater = new TimeUpdater(1, this, MainActivity.this, Functions.cityid, Functions.cityname, Functions.countryname);
        timeUpdater.run();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy()");
        startBackgroundService();
    }

    private void startBackgroundService() {
        Intent intent = new Intent(MainActivity.this, BackgroundService.class);
        startService(intent);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        System.out.println(Functions.url);
        if(timeUpdater != null)
            timeUpdater.run();
    }

    /**********************************************************************************
     *
     *                              TAKE PERMISSIONS
     *
     *************************************************************************************/

    private void takePermissions() {
        Log.d("Permission", "takePermissions()");
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            } else {
                Log.d("Permission", "already have permissions");
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

}
