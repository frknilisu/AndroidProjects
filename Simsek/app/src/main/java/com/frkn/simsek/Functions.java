package com.frkn.simsek;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by frkn on 07.01.2017.
 */

public class Functions {

    public static boolean existLastSetting = false;
    public static String cityid = "5041";
    public static String cityname = "ISTANBUL";
    public static String countryname = "TURKIYE";
    public static String url = "https://namazvakitleri.com.tr/" + "sehir/" + cityid + "/" + cityname + "/" + countryname;

    public static boolean city_flag = false;
    public static boolean country_flag = false;

    private static String fileName = "times.json";

    private static Context context;
    private static Calendar calendar;

    public static void saveData(String mJsonResponse) {
        try {
            Log.d("saveData", "saving..");
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(dir, fileName);
            System.out.println("Write to: " + file.getAbsolutePath());
            if (!file.exists())
                file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(mJsonResponse);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }
    }

    public static JSONObject getData() {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(dir, fileName);
            System.out.println("Read from: " + file.getAbsolutePath());
            //check whether file exists
            FileInputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new JSONObject(new String(buffer));
        } catch (IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCurrentTime() {
        calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        return timeFormat.format(calendar.getTime());
    }

    public static String getCurrentDate() {
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(calendar.getTime());
    }

    public static String getReverseCurrentDate() {
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(calendar.getTime());
    }

    public static String getPrevXdaysDate(int x) {
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -x);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String ret = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, x);
        return ret;
    }

    public static String getReversePrevXdaysDate(int x) {
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -x);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String ret = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, x);
        return ret;
    }

    public static String getNextXdaysDate(int x) {
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, x);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String ret = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -x);
        return ret;
    }

    public static String getReverseNextXdaysDate(int x) {
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, x);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String ret = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -x);
        return ret;
    }

    public static Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

}
