package com.frkn.physbasic;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public final static String EXTRA_MESSAGE = "com.frkn.physbasic.MESSAGE";

    private List<Chapter> chapterList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChapterAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ChapterAdapter(chapterList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Chapter chapter = chapterList.get(position);
                //Toast.makeText(getApplicationContext(), chapter.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                if(chapter.isLock()){
                    BuyPro fragment = new BuyPro();
                    FragmentManager fm = getSupportFragmentManager();
                    fragment.show(fm, "Dialog Fragment");
                    System.out.println("this chapter is locked!\n" + "Please buy pro");
                    Toast.makeText(getApplicationContext(), "this chapter is locked!\nPlease buy pro", Toast.LENGTH_LONG);
                } else{
                    openChapter(chapter);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        takePermissions();
    }

    private void setup() {
        loadChapterList();
        mAdapter.notifyDataSetChanged();
    }

    private void loadChapterList() {
        Log.d("setup", "loadChapterList()..");
        //InputStream is = getResources().openRawResource(R.raw.chapters);
        try {
            File chapterFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/PhysBasic/chapters.json");
            InputStream is = new FileInputStream(chapterFile);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }

            JSONObject jObject = new JSONObject(writer.toString());
            JSONArray jArray = jObject.getJSONArray("chapters");
            for (int i = 0; i < jArray.length(); i++) {
                chapterList.add(new Chapter(jArray.getJSONObject(i)));
            }

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

    private void openChapter(Chapter chapter) {
        Intent intent = new Intent(MainActivity.this, ShowChapter.class);
        intent.putExtra(EXTRA_MESSAGE + "_chapterId", String.valueOf(chapter.getChapterId()));
        startActivity(intent);
    }

    /**********************************************************************************
     *
     *                              TAKE PERMISSIONS
     *
     *************************************************************************************/

    private void takePermissions() {
        Log.d("Permission", "takePermissions()..");
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            } else {
                Log.d("Permission", "already have permissions");
                setup();
            }
        } else {
            setup();
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        for (int i = 0; i < permissions.length; i++) {
            int result = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, permissions, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean flag = true;
        switch (requestCode) {
            case 101:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //not granted
                        Log.d("Permission", "No permission: " + permissions[i].toString());
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    //granted
                    Log.d("Permission", "all permissions are taken");
                    setup();
                } else {
                    Log.d("Permission", "some permission has no");
                    takePermissions();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
