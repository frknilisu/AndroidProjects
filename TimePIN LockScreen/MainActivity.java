package com.example.frkn.background_service;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Date;

public class MainActivity extends Activity {

    static final String TAG = "LockScreenActivity";
    static final int ACTIVATION_REQUEST = 47; // identifies our request id
    static final String SWITCH = "locked";
    static public DevicePolicyManager deviceManager = null;
    static public ComponentName compName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        deviceManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, AdminReceiver.class);

        if (!deviceManager.isAdminActive(compName)) {
            Log.d(TAG, "Administration Enabled");
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Explain something");
            startActivityForResult(intent, ACTIVATION_REQUEST);
        }

        final Switch lock = (Switch) findViewById(R.id.timePin);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (pref.getBoolean(SWITCH, false)) {
            lock.setChecked(true);
        }

        lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    pref.edit().putBoolean(SWITCH, true).apply();

                    deviceManager.setPasswordQuality(compName, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
                    deviceManager.setPasswordMinimumLength(compName, 4);
                    String currentTime = DateFormat.format("kk:mm", new Date()).toString();
                    String pass = currentTime.substring(0, 2) + currentTime.substring(3);
                    deviceManager.resetPassword(pass, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);

                    Log.d(TAG, "Set Lock Password = " + pass);

                    Intent intent = new Intent(MainActivity.this, MyService.class);
                    startService(intent);

                } else {

                    pref.edit().putBoolean(SWITCH, false).apply();

                    deviceManager.setPasswordQuality(compName, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
                    deviceManager.setPasswordMinimumLength(compName, 0);
                    String pass = "";
                    deviceManager.resetPassword("", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                    Log.d(TAG, "Reset Password");

                    //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

                    /*PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                            | PowerManager.ACQUIRE_CAUSES_WAKEUP
                            | PowerManager.ON_AFTER_RELEASE, "INFO");

                    wl.acquire();*/
                    KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                    KeyguardManager.KeyguardLock kl = km.newKeyguardLock("name");
                    kl.disableKeyguard();

                    Log.d(TAG, "Keyboard Disable");

                    Intent intent = new Intent(MainActivity.this, MyService.class);
                    stopService(intent);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
