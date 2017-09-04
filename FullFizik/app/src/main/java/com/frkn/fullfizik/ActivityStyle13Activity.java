package com.frkn.fullfizik;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class ActivityStyle13Activity extends AppCompatActivity implements View.OnClickListener {

    private static final String[] pageTitle = {"Tests","Chapters","Specials"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity13_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_activity3);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager2);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new WizardPageChangeListener());
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);

        setup();
    }

    private void setup() {
        loadSettings();
        readInceptionJson();
        loadChapterList();
        loadTestList();
        loadSpecialList();
        saveAccountType();
        loadSettings();
    }

    private void readInceptionJson() {
        try {
            FileInputStream fin = new FileInputStream(new File(this.getFilesDir(), "inception.json"));
            Writer writer = new StringWriter();
            char[] buffer = new char[Data.BUFFER_SIZE];
            Reader reader = new BufferedReader(new InputStreamReader(fin, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }

            Data.inceptionJson = new JSONObject(writer.toString());
            JSONArray jsonArray = null;
            jsonArray = Data.inceptionJson.getJSONArray("chapters");
            Data.chaptersCount = jsonArray.length();

            jsonArray = Data.inceptionJson.getJSONArray("tests");
            Data.testsCount = jsonArray.length();

            jsonArray = Data.inceptionJson.getJSONArray("specials");
            Data.specialsCount = jsonArray.length();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean loadChapterList() {
        Log.d("setup", "loadChapterList()..");
        Data.chapterList.clear();
        try {
            JSONArray jArray = Data.inceptionJson.getJSONArray("chapters");
            for (int i = 0; i < jArray.length(); i++) {
                Data.chapterList.add(new ActivityStyle13Model(jArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean loadTestList() {
        Log.d("setup", "loadTestList()..");
        Data.testList.clear();
        try {
            JSONArray jArray = Data.inceptionJson.getJSONArray("tests");
            for (int i = 0; i < jArray.length(); i++) {
                Data.testList.add(new ActivityStyle13Model(jArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean loadSpecialList() {
        Log.d("setup", "loadSpecialList()..");
        Data.specialList.clear();
        try {
            JSONArray jArray = Data.inceptionJson.getJSONArray("specials");
            for (int i = 0; i < jArray.length(); i++) {
                Data.specialList.add(new ActivityStyle13Model(jArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    /*************************************************************************************
     * Shared Preferences Functions
     *************************************************************************************/
    private static final String PREFS_NAME = "MySharedPrefName";
    SharedPreferences settings;

    private void saveAccountType() {
        SharedPreferences.Editor editor;
        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Data.accountType);
        editor.putString("accountType", json);
        editor.commit();
    }

    private void loadSettings() {
        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = settings.getString("accountType", "");
        if(json != ""){
            Data.accountType = gson.fromJson(json, Data.Account.class);
        } else{
            Data.accountType = Data.Account.BASIC;
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private int WIZARD_PAGES_COUNT = pageTitle.length;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new ActivityStyle13Fragment(position, Data.getList(position));
        }

        @Override
        public int getCount() {
            return WIZARD_PAGES_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitle[position];
        }
    }

    private class WizardPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int position) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.loginsignup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Toast.makeText(this, "action edit clicked!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_search:
                Toast.makeText(this, "action search clicked!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                Toast.makeText(this, "action setting clicked!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }
}
