package com.frkn.pratikfizik;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;

    String[] permissions = new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public final static String EXTRA_MESSAGE = "com.frkn.pratikfizik.MESSAGE";

    private List<Chapter> chapterList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChapterAdapter mAdapter;

    private DownloadManager downloadManager;
    private long downloadReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                Toast.makeText(getApplicationContext(), chapter.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                if(isLocked(chapter.getChapterId())){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.add(YourFragment.newInstance(), null);
                    ft.commit();
                    System.out.println("you must login");
                    if (mFirebaseUser == null) {
                        // Not logged in, launch the Log In activity
                        loadLogInView();
                    } else {
                        mUserId = mFirebaseUser.getUid();

                        Log.d("UserId", mUserId);
                    }
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
        prepareChapterData();
        //startDownload();
        //moveFiles();
        mAdapter.notifyDataSetChanged();

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean isLocked(int id){
        if(id == 1)
            return false;
        if(id == 2)
            return true;
        return false;
    }

    private void openChapter(Chapter chapter) {
        Intent intent = new Intent(MainActivity.this, DisplayChapter.class);
        intent.putExtra(EXTRA_MESSAGE + "_chapterId", String.valueOf(chapter.getChapterId()));
        intent.putExtra(EXTRA_MESSAGE + "_title", chapter.getTitle());
        intent.putExtra(EXTRA_MESSAGE + "_definition", chapter.getDefinition());
        startActivity(intent);
    }

    private void prepareChapterData() {
        Log.d("Prepare", "prepareChapterData()");
        InputStream is = getResources().openRawResource(R.raw.chapters);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String jsonString = writer.toString();
        //Log.v("Text Data", jsonString);
        try {
            // Parse the data into jsonobject to get original data in form of json.
            JSONObject jObject = new JSONObject(
                    jsonString);
            JSONArray jArray = jObject.getJSONArray("chapters");
            for (int i = 0; i < jArray.length(); i++) {
                chapterList.add(new Chapter(jArray.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**********************************************************************************
     *
     *                              TAKE PERMISSIONS
     *
    *************************************************************************************/

    private void takePermissions() {
        Log.d("Permission", "takePermissions()");
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

    /**********************************************************************************
     *
     *************************************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mFirebaseAuth.signOut();
            loadLogInView();
        }

        return super.onOptionsItemSelected(item);
    }


    /**********************************************************************************
     *
     *                              Download and Move
     *
     *************************************************************************************/

    private void startDownload() {
        Log.d("Download", "startDownload()");
        for (int i = 0; i < chapterList.size(); i++) {
            downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(chapterList.get(i).getUrl());
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            //Restrict the types of networks over which this download may proceed.
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            //Set whether this download may proceed over a roaming connection.
            request.setAllowedOverRoaming(false);
            //Set the title of this download, to be displayed in notifications (if enabled).
            //request.setTitle(chapterList.get(i).getTitle());
            //Set a description of this download, to be displayed in notifications (if enabled)
            //request.setDescription("Android Data download using DownloadManager.");
            //Set the local destination for the downloaded file to a path within the application's external files directory
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setDestinationInExternalFilesDir(MainActivity.this, "/PratikFizik", "/Chapter" + chapterList.get(i).getChapterId() + ".pdf");

            //Enqueue a new download and same the referenceId
            downloadReference = downloadManager.enqueue(request);

            DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
            int ret = 0;
            while (ret != 1) {
                //set the query filter to our previously Enqueued download
                myDownloadQuery.setFilterById(downloadReference);

                //Query the download manager about downloads that have been requested.
                Cursor cursor = downloadManager.query(myDownloadQuery);
                try {
                    if (cursor.moveToFirst()) {
                        ret = checkStatus(cursor);
                        System.out.println(ret);
                    }
                } finally {
                    cursor.close();
                }
            }
            System.out.println(chapterList.get(i).getTitle() + " was downloaded");
        }
    }

    private int checkStatus(Cursor cursor) {

        //column for status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";

        switch (status) {
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }

                System.out.println(reasonText);
                return -1;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                System.out.println(reasonText);
                System.out.println(statusText);
                return 0;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                System.out.println(reasonText);
                System.out.println(statusText);
                return 0;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                System.out.println(reasonText);
                System.out.println(statusText);
                return 0;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "Filename:\n" + filename;
                System.out.println(reasonText);
                System.out.println(statusText);
                return 1;
        }

        return 0;

    }

    private void moveFiles() {
        Log.d("Move", "moveFiles()");
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        try {
            File dir = new File(downloadFolder.getAbsolutePath() + "/PratikFizik/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            Log.w("creating file error", e.toString());
        }
        for (int i = 0; i < chapterList.size(); i++) {
            File srcFile = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.frkn.pratikfizik/files/PratikFizik/Chapter" + chapterList.get(i).getChapterId() + ".pdf");
            System.out.println(srcFile.getAbsolutePath());
            System.out.println(String.valueOf(srcFile.exists()));

            File dstFile = new File(downloadFolder.getAbsolutePath() + "/PratikFizik/x" + chapterList.get(i).getChapterId() + "x.txt");
            //File dstFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Ch" + chapterList.get(i).getChapterId());
            if (!dstFile.exists()) {
                try {
                    dstFile.createNewFile();
                    moveFile(srcFile, dstFile);
                    srcFile.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        File fil = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.frkn.pratikfizik/files/PratikFizik/");
        fil.delete();

    }

    private boolean moveFile(File src, File dst) throws IOException {
        if (src.getAbsolutePath().toString().equals(dst.getAbsolutePath().toString())) {
            return true;
        } else {

        /* Encode a file and write the encoded output to a text file. */
            try {
                Crypter.encode(src.getAbsolutePath(), dst.getAbsolutePath(), true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*InputStream is = new FileInputStream(src);
            OutputStream os = new FileOutputStream(dst);
            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) > 0) {
                os.write(buff, 0, len);
            }
            is.close();
            os.close();*/
        }
        return true;
    }

    /**********************************************************************************
     *
     *************************************************************************************/

}
