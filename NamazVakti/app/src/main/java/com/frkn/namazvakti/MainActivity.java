package com.frkn.namazvakti;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String url = "http://www.diyanet.gov.tr/tr/PrayerTime/PrayerTimesList";
    Map<String, String> parameters;

    Map<String, Integer> countries_map;
    List<String> countries_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView txt = (TextView)findViewById(R.id.txt);
        parameters = new HashMap<String, String>();
        countries_map = new HashMap<String, Integer>();
        countries_list = new ArrayList<String>();

        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new postClass().execute(txt);
                new JsoupPost().execute();
            }

        });
        
    }

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

    private class JsoupPost extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
                org.jsoup.nodes.Document doc = null;
                try {

                    doc = Jsoup.connect(url)
                            //.data("Country", Integer.toString(2))
                            //.data("State", Integer.toString(552))
                            //.data("City", Integer.toString(9676))
                            //.data("period", "Haftalik")
                            .userAgent("Mozilla")
                            .post();

                    for(Element opt : doc.select("span").get(12).child(0).children()){
                        System.out.println(opt.text() + ": " + opt.attr("value").toString() + "\n--------------\n");
                        //countries_map.put(opt.text(), Integer.parseInt(opt.attr("value").toString()));
                        //countries_list.add(opt.text());
                    }

                    /*for(String s : countries_list)
                        System.out.println(s + ": " + countries_map.get(s));*/

                } catch (IOException e) {
                    e.printStackTrace();
                }

            return null;
        }

        private void getCountries() {
            org.jsoup.nodes.Document doc = null;
            try {

                doc = Jsoup.connect(url)
                    //.data("Country", Integer.toString(2))
                    //.data("State", Integer.toString(552))
                    //.data("City", Integer.toString(9676))
                    //.data("period", "Haftalik")
                    .userAgent("Mozilla")
                    .post();

                for(Element opt : doc.select("span").get(12).child(0).children()){
                    System.out.println(opt.text() + ": " + opt.attr("value").toString() + "\n--------------\n");
                    countries_map.put(opt.text(), Integer.parseInt(opt.attr("value").toString()));
                    countries_list.add(opt.text());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
