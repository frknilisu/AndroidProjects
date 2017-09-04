package com.frkn.simsek;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Created by frkn on 07.01.2017.
 */

public class TimeUpdater {

    public ShowTimes showTimes;
    private MainActivity activity;
    private Context context;

    private int flag;

    public TimeUpdater(int _flag, MainActivity _activity, Context _context) {
        this.flag = _flag;
        this.activity = _activity;
        this.context = _context;
    }

    public void run() {
        new asyncTask().execute();
    }

    private void getTimes() {
        Log.d("ASYNC", "getTimes() starting..");
        org.jsoup.nodes.Document doc = null;
        try {
            doc = Jsoup.connect(Functions.url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .timeout(10 * 1000)
                    .get();

            Element script = doc.select("script").get(6);
            int startIndex = script.toString().indexOf("var times") + 12;
            int endIndex = script.toString().indexOf("var date") - 9;
            JSONObject info = new JSONObject(script.toString().substring(startIndex, endIndex));
            Functions.saveData(info.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class asyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("asyncTask", "calling getTimes..");
            if(Functions.isOnline()) {
                getTimes();
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isOk) {
            super.onPostExecute(isOk);
            if(isOk) {
                Log.d("asyncTask", "times received..");

                if (flag == 1) {
                    if(MainActivity.showTimes == null){
                        MainActivity.showTimes = new ShowTimes(activity, context);
                        MainActivity.showTimes.updateUI();
                    } else if(MainActivity.showTimes.handler == null){
                        MainActivity.showTimes.updateUI();
                    } else {
                        MainActivity.showTimes.stopTimer();
                        MainActivity.showTimes.updateUI();
                    }
                    /*if (showTimes == null) {
                        showTimes = new ShowTimes(activity, context);
                        showTimes.updateUI();
                    } else if (showTimes.handler != null) {
                        showTimes.stopTimer();
                        showTimes.updateUI();
                    }*/
                }
            } else{
                Log.d("asyncTask", "times not received..");
                TextView tx = (TextView) activity.findViewById(R.id.tarihText);
                tx.setText("--/--/----");
            }
        }
    }
}
