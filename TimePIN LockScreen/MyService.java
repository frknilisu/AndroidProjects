package com.example.frkn.background_service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by frkn on 1.10.2015.
 */
public class MyService extends Service {

    static final String TAG = "MyService";
    public MyReceiver receiver;
    private DevicePolicyManager dM = null;
    private ComponentName cN = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        dM = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        cN = new ComponentName(this, AdminReceiver.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        /*Bundle b = intent.getExtras();
        deviceManager = (DevicePolicyManager) b.getParcelable("DM");
        compName = (ComponentName) b.getParcelable("CN");*/

        if (dM == null || cN == null)
            System.out.println("Arkadaş bunlar yine null");
        else {
            receiver = new MyReceiver(dM, cN);
            registerReceiver(receiver, new IntentFilter(Intent.ACTION_TIME_TICK));
            Log.d(TAG, "Register Receiver");
        }
        return START_STICKY;

    }

    @Override
    public boolean stopService(Intent name) {
        unregisterReceiver(receiver);
        Log.d(TAG, "onStop");
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Log.d(TAG, "onDestroy");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
