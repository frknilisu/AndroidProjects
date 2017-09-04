package com.frkn.simsek;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by frkn on 07.01.2017.
 */

public class BackgroundService extends Service {

    Handler handler;
    TimeUpdater timeUpdater;
    ShowTimes showTimes;


    @Override
    public void onCreate() {
        Log.d("BACKGROUND", "SERVICE CREATE");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("BACKGROUND", "SERVICE START");
    }

    @Override
    public void onDestroy() {
        Log.d("BACKGROUND", "SERVICE DESTROY");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BACKGROUND", "SERVICE COMMAND");
        handler = new Handler();
        showTimes = new ShowTimes(getApplicationContext());
        showTimes.updateData();
        //timeUpdater = new TimeUpdater(0, getApplicationContext(), Functions.cityid, Functions.cityname, Functions.countryname);
        //timeUpdater.run();
        handler.postDelayed(runnable, 0);
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void send_notification(String show) {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setLargeIcon(bitmap)
                        .setSmallIcon(R.drawable.ic_action_time)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentTitle(show)
                        .setOngoing(true)
                        .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(101, mBuilder.build());
    }

    private int getNotificationIcon() {
        return R.mipmap.ic_launcher;
    }


    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            showTimes.setNotificationMessage();
            send_notification(showTimes.getNotificationMessage());
            handler.postDelayed(this, 1000);
        }
    };


}
