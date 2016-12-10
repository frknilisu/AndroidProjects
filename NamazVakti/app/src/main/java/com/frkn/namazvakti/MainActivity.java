package com.frkn.namazvakti;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.ElementType;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static final String url = "http://www.diyanet.gov.tr/tr/PrayerTime/PrayerTimesList";
    List<Map.Entry<String, Integer>> countries_list;
    List<Map.Entry<String, Integer>> states_list;
    List<Map.Entry<String, Integer>> districts_list;
    List<Map.Entry<String, String>> parameters;

    Button btn1, btn2, btn3, btn4;
    EditText ulke, sehir, ilce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button)findViewById(R.id.button);
        btn2 = (Button)findViewById(R.id.button2);
        btn3 = (Button)findViewById(R.id.button3);
        btn4 = (Button)findViewById(R.id.button4);
        ulke = (EditText)findViewById(R.id.editText);
        sehir = (EditText)findViewById(R.id.editText2);
        ilce = (EditText)findViewById(R.id.editText3);

        countries_list = new ArrayList<>();
        states_list = new ArrayList<>();
        districts_list = new ArrayList<>();
        parameters = new ArrayList<>();
        //parameters.add(new AbstractMap.SimpleEntry<String, String>("Country", "33"));
        //parameters.add(new AbstractMap.SimpleEntry<String, String>("State", "626"));
        //parameters.add(new AbstractMap.SimpleEntry<String, String>("District", "8995"));

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JsoupPost(MainActivity.this).execute(parameters);
            }
        });
        
    }

    private void selectCountryAndGetStates() {
        if(countries_list.isEmpty()){
            System.out.println("countries_list Empty");
        } else{
            for(Map.Entry<String, Integer> entr : countries_list)
                System.out.println(entr.getKey() + ": " + entr.getValue());
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String C = ulke.getText().toString();
                    for(Map.Entry<String, Integer> entr : countries_list) {
                        if (C.equals(entr.getKey())) {
                            parameters.add(new AbstractMap.SimpleEntry<String, String>("Country", Integer.toString(entr.getValue())));
                            new JsoupPost(MainActivity.this).execute(parameters);
                            break;
                        }
                    }
                }
            });
        }
    }

    private void selectStateAndGetDistricts() {
        if(states_list.isEmpty()){
            System.out.println("states_list Empty");
        } else{
            for(Map.Entry<String, Integer> entr : states_list)
                System.out.println(entr.getKey() + ": " + entr.getValue());
            btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String S = sehir.getText().toString();
                    for(Map.Entry<String, Integer> entr : states_list) {
                        if (S.equals(entr.getKey())) {
                            parameters.add(new AbstractMap.SimpleEntry<String, String>("State", Integer.toString(entr.getValue())));
                            new JsoupPost(MainActivity.this).execute(parameters);
                            break;
                        }
                    }
                }
            });
        }
    }

    private void selectDistrictAndGetPrayerTimes() {
        if(districts_list.isEmpty()){
            System.out.println("districts_list Empty");
        } else{
            for(Map.Entry<String, Integer> entr : districts_list)
                System.out.println(entr.getKey() + ": " + entr.getValue());
            btn4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String D = ilce.getText().toString();
                    for(Map.Entry<String, Integer> entr : districts_list) {
                        if (D.equals(entr.getKey())) {
                            parameters.add(new AbstractMap.SimpleEntry<String, String>("District", Integer.toString(entr.getValue())));
                            new JsoupPost(MainActivity.this).execute(parameters);
                            break;
                        }
                    }
                }
            });
        }
    }

    private class JsoupPost extends AsyncTask<List<Map.Entry<String, String>>, Void, List<Map.Entry<String, Integer>>>{

        private MainActivity activity;

        public JsoupPost(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        protected List<Map.Entry<String, Integer>> doInBackground(List<Map.Entry<String, String>>... params) {
            if(params[0].size() == 0){
                System.out.println("getCountries() calling..");
                return getCountries();
            } else if(params[0].size() == 1){
                System.out.println(params[0].get(0).getKey() + ": " + params[0].get(0).getValue());
                System.out.println("getStates() calling..");
                return getStates(params[0].get(0).getValue().toString());
            } else if(params[0].size() == 2){
                System.out.println(params[0].get(0).getKey() + ": " + params[0].get(0).getValue());
                System.out.println(params[0].get(1).getKey() + ": " + params[0].get(1).getValue());
                System.out.println("getDistricts() calling..");
                return getDictricts(params[0].get(0).getValue().toString(), params[0].get(1).getValue().toString());
            } else if(params[0].size() == 3) {
                System.out.println(params[0].get(0).getKey() + ": " + params[0].get(0).getValue());
                System.out.println(params[0].get(1).getKey() + ": " + params[0].get(1).getValue());
                System.out.println(params[0].get(2).getKey() + ": " + params[0].get(2).getValue());
                System.out.println("getPrayerTimes() calling..");
                getPrayerTimes(params[0].get(0).getValue().toString(), params[0].get(1).getValue().toString(), params[0].get(2).getValue().toString());
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Map.Entry<String, Integer>> list) {
            super.onPostExecute(list);
            if(activity.parameters.size() == 0){
                activity.countries_list = list;
                activity.selectCountryAndGetStates();
            } else if(activity.parameters.size() == 1){
                activity.states_list = list;
                activity.selectStateAndGetDistricts();
            } else if(activity.parameters.size() == 2){
                activity.districts_list = list;
                activity.selectDistrictAndGetPrayerTimes();
            } else if(parameters.size() == 3){
                Log.d("fillList", "prayerTimes received");
            }

        }

        private List<Map.Entry<String, Integer>> getCountries() {
            org.jsoup.nodes.Document doc = null;
            List<Map.Entry<String, Integer>> pairList = new ArrayList<>();
            try {

                doc = Jsoup.connect(url)
                        //.data("Country", Integer.toString(2))
                        //.data("State", Integer.toString(552))
                        //.data("City", Integer.toString(9676))
                        //.data("period", "Haftalik")
                        .userAgent("Mozilla")
                        .post();


                for(Element opt : doc.select("span").get(12).child(0).children()){
                    //System.out.println(opt.text() + ": " + opt.attr("value").toString() + "\n--------------\n");
                    //countries_map.put(opt.text(), Integer.parseInt(opt.attr("value").toString()));
                    //countries_list.add(opt.text());
                    pairList.add(new AbstractMap.SimpleEntry<String, Integer>(opt.text(), Integer.parseInt(opt.attr("value").toString())));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return pairList;
        }

        private List<Map.Entry<String,Integer>> getStates(String country_value) {
            org.jsoup.nodes.Document doc = null;
            List<Map.Entry<String, Integer>> pairList = new ArrayList<>();
            try {

                doc = Jsoup.connect(url)
                        .data("Country", country_value)
                        //.data("State", Integer.toString(552))
                        //.data("City", Integer.toString(9676))
                        //.data("period", "Haftalik")
                        .userAgent("Mozilla")
                        .post();

                /*for(Element sp : doc.select("span"))
                    System.out.println(sp);*/

                for(Element opt : doc.select("span").get(14).child(0).children()){
                    if(opt.text() == "Seciniz" || opt.attr("value").toString() == "")
                        continue;
                    //System.out.println(opt.text() + ": " + opt.attr("value").toString());
                    //countries_map.put(opt.text(), Integer.parseInt(opt.attr("value").toString()));
                    //countries_list.add(opt.text());
                    pairList.add(new AbstractMap.SimpleEntry<String, Integer>(opt.text(), Integer.parseInt(opt.attr("value").toString())));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return pairList;
        }

        private List<Map.Entry<String,Integer>> getDictricts(String country_value, String state_value) {
            org.jsoup.nodes.Document doc = null;
            List<Map.Entry<String, Integer>> pairList = new ArrayList<>();
            try {

                doc = Jsoup.connect(url)
                        .data("Country", country_value)
                        .data("State", state_value)
                        //.data("City", Integer.toString(9676))
                        //.data("period", "Haftalik")
                        .userAgent("Mozilla")
                        .post();

                /*for(Element sp : doc.select("span"))
                    System.out.println(sp);*/

                for(Element opt : doc.select("span").get(16).child(0).children()){
                    if(opt.text() == "Seciniz" || opt.attr("value").toString() == "")
                        continue;
                    //System.out.println(opt.text() + ": " + opt.attr("value").toString());
                    //countries_map.put(opt.text(), Integer.parseInt(opt.attr("value").toString()));
                    //countries_list.add(opt.text());
                    pairList.add(new AbstractMap.SimpleEntry<String, Integer>(opt.text(), Integer.parseInt(opt.attr("value").toString())));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return pairList;
        }

        private void getPrayerTimes(String country_value, String state_value, String district_value) {
            org.jsoup.nodes.Document doc = null;
            List<Map.Entry<String, Integer>> pairList = new ArrayList<>();
            try {

                doc = Jsoup.connect(url)
                        .data("Country", country_value)
                        .data("State", state_value)
                        .data("City", district_value)
                        .data("period", "Haftalik")
                        .userAgent("Mozilla")
                        .post();


                //System.out.println(doc.select("tbody").first());
                Elements trs = doc.select("tbody").first().children();
                for(Element td : trs.select("td")){
                    System.out.println(td.text());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    private String makePostRequest() {

        try {
            HttpPost httpPost = new HttpPost("http://www.diyanet.gov.tr/tr/PrayerTime/PrayerTimesList"); // replace with
            HttpClient httpClient = new DefaultHttpClient();

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("Country", Integer.toString(2)));
            nameValuePair.add(new BasicNameValuePair("State", Integer.toString(552)));
            nameValuePair.add(new BasicNameValuePair("City", Integer.toString(9676)));
            nameValuePair.add(new BasicNameValuePair("period", "Haftalik"));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

            HttpResponse response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            // write response to log
            Log.d("Http Post Response:", response.toString() + " ==> " + status);

            if (status == 200) {
                System.out.println("HERE");
                HttpEntity entity = response.getEntity();
                InputStream veri = entity.getContent();
                //String data = EntityUtils.toString(entity);
                //Log.d("Entity Data", data);
                String veri_string = null;
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(veri, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    veri.close();
                    veri_string = sb.toString();
                } catch (Exception e) {
                    Log.e("Buffer Error", "Hata " + e.toString());
                }
                return veri_string;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public class postClass extends AsyncTask<TextView, Void, String>{

        TextView textView;

        @Override
        protected String doInBackground(TextView... params) {
            this.textView = params[0];
            String result = "fail";
            String url = "http://www.diyanet.gov.tr/tr/PrayerTime/PrayerTimesList";
            try {

                HttpPost httpPost = new HttpPost(url); // replace with
                HttpClient httpClient = new DefaultHttpClient();

                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                nameValuePair.add(new BasicNameValuePair("Country", Integer.toString(2)));
                nameValuePair.add(new BasicNameValuePair("State", Integer.toString(552)));
                nameValuePair.add(new BasicNameValuePair("City", Integer.toString(9676)));
                nameValuePair.add(new BasicNameValuePair("period", "Haftalik"));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

                HttpResponse response = httpClient.execute(httpPost);
                int status = response.getStatusLine().getStatusCode();
                // write response to log
                Log.d("Http Post Response:", response.toString() + " ==> " + status);

                if (status == 200) {
                    System.out.println("HERE");
                    HttpEntity entity = response.getEntity();
                    InputStream veri = entity.getContent();
                    //String data = EntityUtils.toString(entity);
                    //Log.d("Entity Data", data);
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(veri, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        veri.close();
                        result = sb.toString();
                    } catch (Exception e) {
                        Log.e("Buffer Error", "Hata " + e.toString());
                    }
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            textView.setText(s);
        }
    }


}
