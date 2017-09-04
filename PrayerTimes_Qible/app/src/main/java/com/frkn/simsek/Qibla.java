package com.frkn.simsek;

/**
 * Created by mdemirelcs on 1/15/17.
 */


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Qibla extends Activity implements SensorEventListener {
    Context mContext;
    private ImageView image, image1;

    private float currentDegree = 0f, currentDegree1 = 48f;

    private SensorManager mSensorManager;

    TextView tvHeading;
    TextView qiblaAngle;
    GPSTracker gps;
    double lat1 = 0, lat2 = 0, lon1 = 0, lon2 = 0;

    boolean canGetGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qibla);
        mContext = this;
        image = (ImageView) findViewById(R.id.imageViewCompass);
        image1 = (ImageView) findViewById(R.id.arrow);
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        qiblaAngle = (TextView) findViewById(R.id.qibla);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Qibla.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {

            gps = new GPSTracker(mContext, Qibla.this);

            // Check if GPS enabled
            if (!gps.canGetLocation()) {
                gps.showSettingsAlert();
            }


        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);


    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        TextView t = (TextView) findViewById(R.id.latlong);

        if (gps.canGetLocation()) {

            lat1 = Math.toRadians(gps.getLatitude());
            lon1 = Math.toRadians(gps.getLongitude());
            lat2 = Math.toRadians(21.3891);
            lon2 = Math.toRadians(39.8579);

            t.setText(String.valueOf(currentDegree1));

            float degree = Math.round(sensorEvent.values[0]);

            double deltalon = lon2 - lon1;

            double y = Math.sin(deltalon) * Math.cos(lat2);
            double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltalon);
            double q = Math.toDegrees(Math.atan2(y, x));

            int reduced = (int) q;

            qiblaAngle.setText(String.valueOf(reduced));

            tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(210);

            ra.setFillAfter(true);


            image.startAnimation(ra);

            currentDegree = -degree;


            if (degree <= (reduced + 2) && degree >= (reduced - 2)) {
                image1.setImageResource(R.mipmap.kaba);
                qiblaAngle.setText("KIBLE BULUNDU!");
            } else
                image1.setImageResource(R.mipmap.kible);
        } else {
            t.setText("Konumuzun bulunamadi. GPS'i aktif hale getirin!");
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.

                    gps = new GPSTracker(mContext, Qibla.this);

                    // Check if GPS enabled
                    if (gps.canGetLocation()) {

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        // \n is for new line
                        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(mContext, "You need to grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
