package com.frkn.simsek;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by frkn on 07.01.2017.
 */

public class ShowTimes {

    private MainActivity mainActivity;
    private Context context;
    private String notificationMessage;

    private TextView[] arr_tv_map;
    private Calendar calendar;
    private LinearLayout[] arr_layout;
    public Handler handler;

    private JSONObject times;
    private JSONObject currentTimes;
    private JSONArray prayerTimes;
    private JSONObject next;

    private String suankiVakit = "";
    private String remaining = "";

    public ShowTimes(MainActivity _activity, Context _context) {
        mainActivity = _activity;
        context = _context;

        changeTextFonts();
        initialize();
    }

    public ShowTimes(Context _context) {
        context = _context;
    }

    public void updateUI() {
        updateData();
        updateTimesUI();
        updateLocationUI();
        startTimer();
    }

    private void changeTextFonts() {
        Typeface tf_for_vakitler = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Light.ttf");
        Typeface tf_for_sehirandtarih = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Medium.ttf");
        Typeface tf_for_kalanvakit = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Black.ttf");
        Typeface tf_for_currentVakandSure = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Medium.ttf");
        TextView[] arr_tv = {
                (TextView) mainActivity.findViewById(R.id.sabahSol),
                (TextView) mainActivity.findViewById(R.id.sabahSag),
                (TextView) mainActivity.findViewById(R.id.gunesSol),
                (TextView) mainActivity.findViewById(R.id.gunesSag),
                (TextView) mainActivity.findViewById(R.id.ogleSol),
                (TextView) mainActivity.findViewById(R.id.ogleSag),
                (TextView) mainActivity.findViewById(R.id.ikindiSol),
                (TextView) mainActivity.findViewById(R.id.ikindiSag),
                (TextView) mainActivity.findViewById(R.id.aksamSol),
                (TextView) mainActivity.findViewById(R.id.aksamSag),
                (TextView) mainActivity.findViewById(R.id.yatsiSol),
                (TextView) mainActivity.findViewById(R.id.yatsiSag),
                (TextView) mainActivity.findViewById(R.id.sehirText),
                (TextView) mainActivity.findViewById(R.id.nextVakit),
                (TextView) mainActivity.findViewById(R.id.currentVakit),
                (TextView) mainActivity.findViewById(R.id.nextVakit),
                (TextView) mainActivity.findViewById(R.id.tarihText)};
        for (int i = 0; i < 12; i++) {
            arr_tv[i].setTypeface(tf_for_vakitler);
        }
        arr_tv[12].setTypeface(tf_for_sehirandtarih);
        arr_tv[13].setTypeface(tf_for_kalanvakit);
        arr_tv[14].setTypeface(tf_for_currentVakandSure);
        arr_tv[15].setTypeface(tf_for_currentVakandSure);
        arr_tv[16].setTypeface(tf_for_sehirandtarih);

        arr_tv_map = arr_tv;
    }

    private void initialize() {
        calendar = Calendar.getInstance();

        arr_layout = new LinearLayout[6];
        arr_layout[0] = (LinearLayout) mainActivity.findViewById(R.id.sabahLay);
        arr_layout[1] = (LinearLayout) mainActivity.findViewById(R.id.gunesLay);
        arr_layout[2] = (LinearLayout) mainActivity.findViewById(R.id.ogleLay);
        arr_layout[3] = (LinearLayout) mainActivity.findViewById(R.id.ikindiLay);
        arr_layout[4] = (LinearLayout) mainActivity.findViewById(R.id.aksamLay);
        arr_layout[5] = (LinearLayout) mainActivity.findViewById(R.id.yatsiLay);
    }

    public void updateData() {
        try {
            times = Functions.getData();
            prayerTimes = times.getJSONArray("PrayerTimes");
            String curDate = Functions.getReverseCurrentDate();
            currentTimes = searchCurrentDate(curDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateTimesUI() {
        try {

            if (currentTimes == null) {
                Toast.makeText(context, "CurrentDate is not found in data: " + Functions.getCurrentDate(), Toast.LENGTH_LONG).show();
            } else {
                Log.d("Find Time", "Current Date Times found in data: " + currentTimes.getString("date"));
                arr_tv_map[1].setText(currentTimes.getString("imsak"));
                arr_tv_map[3].setText(currentTimes.getString("gunes"));
                arr_tv_map[5].setText(currentTimes.getString("ogle"));
                arr_tv_map[7].setText(currentTimes.getString("ikindi"));
                arr_tv_map[9].setText(currentTimes.getString("aksam"));
                arr_tv_map[11].setText(currentTimes.getString("yatsi"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        arr_tv_map[16].setText(Functions.getCurrentDate());
    }

    @Nullable
    private JSONObject searchCurrentDate(String curDate) {
        Log.d("ShowTimes", "searchCurrentDate()");
        try {
            JSONObject ret;
            for (int i = 0; i < prayerTimes.length(); i++) {
                ret = prayerTimes.getJSONObject(i);
                if (ret.getString("date").equals(curDate))
                    return ret;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateLocationUI() {
        arr_tv_map[12].setText(getLocationInfo());
    }

    private String getLocationInfo() {
        try {
            JSONObject cityInfo = times.getJSONObject("CityInfo");
            String cityname = cityInfo.getString("name");

            JSONObject countryInfo = times.getJSONObject("CountryInfo");
            String countryname = countryInfo.getString("name");

            return cityname + "/" + countryname;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Not resolved";
    }

    // Su anki namaz vaktinin yazi tipi bold yapan method
    private void makeBold(int id1, int id2) {
        Typeface tf_for_vakitler = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Light.ttf");
        Typeface tf_for_sehirandtarih = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Medium.ttf");

        arr_tv_map[id1].setTypeface(tf_for_sehirandtarih);
        arr_tv_map[id2].setTypeface(tf_for_sehirandtarih);

        for (int i = 0; i < 12; i++) {
            if (i != id1 && i != id2) {
                arr_tv_map[i].setTypeface(tf_for_vakitler);
            }
        }
    }

    // Su anki namaz vaktinin layer ini boyayan method
    private void paintLayer(int ind) {
        if (ind == 6) {
            arr_layout[ind - 1].setBackgroundResource(R.color.ayrim);
            for (int i = 0; i < 5; i++) {
                arr_layout[i].setBackgroundResource(0);
            }
        } else {
            arr_layout[ind].setBackgroundResource(R.color.ayrim);
            for (int i = 0; i < 6; i++) {
                if (i != ind) {
                    arr_layout[i].setBackgroundResource(0);
                }
            }
        }

        suankiVakitUpdate(ind);
        arr_tv_map[14].setText(suankiVakit);
    }

    private void suankiVakitUpdate(int ind) {
        // En ustteki TextView in guncellenmesi
        switch (ind) {
            case 0:
                suankiVakit = "İmsak'a kalan süre:";
                break;
            case 1:
                suankiVakit = "Güneş'e kalan süre:";
                break;
            case 2:
                suankiVakit = "Öğle'ye kalan süre:";
                break;
            case 3:
                suankiVakit = "İkindi'ye kalan süre:";
                break;
            case 4:
                suankiVakit = "Akşam'a kalan süre:";
                break;
            case 5:
                suankiVakit = "Yatsı'ya kalan süre:";
                break;
            case 6:
                suankiVakit = "İmsak'a kalan süre:";
                break;
        }
    }

    private void startTimer() {
        Log.d("Timer", "START");
        handler = new Handler();
        handler.postDelayed(runnable, 0);
    }

    protected void stopTimer() {
        Log.d("Timer", "STOP");
        handler.removeCallbacks(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (Functions.getCurrentTime().equals("00:00:00")) {
                updateUI();
            }
            try {
                if (currentTimes == null || !currentTimes.getString("date").equals(Functions.getReverseCurrentDate())) {
                    currentTimes = searchCurrentDate(Functions.getReverseCurrentDate());
                }
                if (currentTimes != null) {
                    int index = calcDiffInTime(currentTimes);
                    arr_tv_map[15].setText(remaining);
                    paintLayer(index);
                    if (index == 6)
                        makeBold(10, 11);
                    else
                        makeBold(2 * index, 2 * index + 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            handler.postDelayed(this, 1000);
        }
    };

    /*****************************
     * CALCULATIONS
     ******************************************/

    private int calcDiffInTime(JSONObject date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf_forCurrent = new SimpleDateFormat("HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String formattedDate = sdf_forCurrent.format(c.getTime());
        Date date1, date2;
        long[] diffAr = new long[6];
        try {
            date2 = sdf_forCurrent.parse(formattedDate);
            date1 = simpleDateFormat.parse(date.getString("imsak"));
            diffAr[0] = date2.getTime() - date1.getTime();
            date1 = simpleDateFormat.parse(date.getString("gunes"));
            diffAr[1] = date2.getTime() - date1.getTime();
            date1 = simpleDateFormat.parse(date.getString("ogle"));
            diffAr[2] = date2.getTime() - date1.getTime();
            date1 = simpleDateFormat.parse(date.getString("ikindi"));
            diffAr[3] = date2.getTime() - date1.getTime();
            date1 = simpleDateFormat.parse(date.getString("aksam"));
            diffAr[4] = date2.getTime() - date1.getTime();
            date1 = simpleDateFormat.parse(date.getString("yatsi"));
            diffAr[5] = date2.getTime() - date1.getTime();

            for (int i = 0; i <= 5; i++) {
                if (diffAr[i] < 0) {
                    remaining = calculations(diffAr[i]).toString();
                    return i;
                }
            }
            if (next == null)
                next = searchCurrentDate(Functions.getReverseNextXdaysDate(1));
            date1 = simpleDateFormat.parse(next.getString("imsak"));    //  bir sonraki günün imsak vaktini alıyor
            diffAr[0] = (date2.getTime() - simpleDateFormat.parse("24:00").getTime()) + (simpleDateFormat.parse("00:00").getTime() - date1.getTime());
            remaining = calculations(diffAr[0]).toString();
            return 6;

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String calculations(long diff) {
        int diffSeconds = (int) diff / 1000 % 60;
        int diffMinutes = (int) diff / (60 * 1000) % 60;
        int diffHours = (int) diff / (60 * 60 * 1000);
        int hh = Math.abs(diffHours);
        int mm = Math.abs(diffMinutes);
        int ss = Math.abs(diffSeconds);

        String hhS = String.valueOf(hh);
        String mmS = String.valueOf(mm);
        String ssS = String.valueOf(ss);

        if (hh < 10)
            hhS = "0" + hhS;
        if (mm < 10)
            mmS = "0" + mmS;
        if (ss < 10)
            ssS = "0" + ssS;

        String dateString = hhS + ":" + mmS + ":" + ssS;
        return dateString;
    }


    /***********************
     * BackgroundService functions
     *****************************/

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage() {
        try {
            if (currentTimes == null || !currentTimes.getString("date").equals(Functions.getReverseCurrentDate()))
                currentTimes = searchCurrentDate(Functions.getReverseCurrentDate());
            if (currentTimes != null) {
                int index = calcDiffInTime(currentTimes);
                suankiVakitUpdate(index);
                notificationMessage = suankiVakit + " " + remaining;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
