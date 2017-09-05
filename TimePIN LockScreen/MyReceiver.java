package com.example.frkn.background_service;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Date;

/**
 * Created by frkn on 1.10.2015.
 */
public class MyReceiver extends BroadcastReceiver {

    static final String TAG = "MyReceiver";
    private DevicePolicyManager deviceManager = null;
    private ComponentName compName = null;

    public MyReceiver() {

    }

    public MyReceiver(DevicePolicyManager dm, ComponentName cn) {
        Log.d(TAG, "Receiver Created");
        deviceManager = dm;
        compName = cn;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            Log.d(TAG, "Time is changed");

            if (deviceManager == null || compName == null)
                Log.d(TAG, "There are NULL");
            else {

                deviceManager.setPasswordQuality(compName, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
                deviceManager.setPasswordMinimumLength(compName, 4);
                String currentTime = DateFormat.format("kk:mm", new Date()).toString();
                String pass = currentTime.substring(0, 2) + currentTime.substring(3);
                deviceManager.resetPassword(pass, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                Log.d(TAG, "Set Password = " + pass);
            }
        }
    }

}
