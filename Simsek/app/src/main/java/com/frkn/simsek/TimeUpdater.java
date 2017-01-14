package com.frkn.simsek;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Created by frkn on 07.01.2017.
 */

public class TimeUpdater {

    private ShowTimes showTimes;

    private Context context;
    private MainActivity mainActivity;
    private int flag;

    private String baseUrl = "https://namazvakitleri.com.tr/";
    private JSONObject parameters;

    public TimeUpdater(int _flag, MainActivity act, Context _context, String _cityid, String _cityname, String _countryname) {
        flag = _flag;
        mainActivity = act;
        context = _context;
        parameters = new JSONObject();
        try {
            parameters.put("cityid", _cityid);
            parameters.put("cityname", _cityname);
            parameters.put("countryname", _countryname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public TimeUpdater(int _flag, Context _context, String _cityid, String _cityname, String _countryname){
        flag = _flag;
        context = _context;
        parameters = new JSONObject();
        try {
            parameters.put("cityid", _cityid);
            parameters.put("cityname", _cityname);
            parameters.put("countryname", _countryname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        new asyncTask().execute(parameters);
    }

    private void getTimes(String cityid, String cityname, String countryname) {
        Log.d("ASYNC", "getTimes() starting..");
        org.jsoup.nodes.Document doc = null;
        try {
            //String fullUrl = baseUrl + "sehir/" + cityid + "/" + cityname + "/" + countryname;
            doc = Jsoup.connect(Functions.url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .timeout(10 * 1000)
                    .get();

            Element script = doc.select("script").get(6);
            int startIndex = script.toString().indexOf("var times") + 12;
            int endIndex = script.toString().indexOf("var date") - 9;
            JSONObject info = new JSONObject(script.toString().substring(startIndex, endIndex));
            Functions.saveData(context, info.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class asyncTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(JSONObject... params) {
            Log.d("asyncTask", "calling getTimes..");
            try {
                getTimes(params[0].getString("cityid"), params[0].getString("cityname"), params[0].getString("countryname"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("asyncTask", "times received..");
            if(flag == 1){
                if (showTimes != null && showTimes.handler != null) {
                    showTimes.stopTimer();
                    showTimes.updateUI();
                } else{
                    showTimes = new ShowTimes(mainActivity, context);
                    showTimes.updateUI();
                }
            }
        }
    }
}
