package com.frkn.youtdownloader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView lastLink;
    String youtube_url;
    String yout_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastLink = (TextView) findViewById(R.id.textView);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else {
                // Handle other intents, such as being started from the home screen
            }
        }


    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            youtube_url = sharedText;
            System.out.println("Youtube Link: " + youtube_url);

            String[] s = sharedText.split("/");
            String videoId = s[s.length - 1];
            System.out.println("Video Id: " + videoId);

            yout_url = "https://yout.com/video/" + videoId;
            System.out.println("Yout Link: " + yout_url);
            lastLink.setText(yout_url);

            //open url with browser
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(yout_url));
            startActivity(i);

            System.out.println("startActivity");
        }
    }

    @Override
    protected void onPause() {
        Log.d("STATE", "onPause");
        int pid = android.os.Process.myPid();
        System.out.println(pid);
        android.os.Process.killProcess(pid);
        System.exit(0);
        //super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("STATE", "onResume");
        int pid = android.os.Process.myPid();
        System.out.println(pid);
        android.os.Process.killProcess(pid);
        System.exit(0);
        /*super.onResume();

        lastLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open url with browser
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(yout_url));
                startActivity(i);
            }
        });*/
    }

    @Override
    protected void onStop() {
        Log.d("STATE", "onStop");
        int pid = android.os.Process.myPid();
        System.out.println(pid);
        android.os.Process.killProcess(pid);
        System.exit(0);
        //super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("STATE", "onDestroy");
        int pid = android.os.Process.myPid();
        System.out.println(pid);
        android.os.Process.killProcess(pid);
        System.exit(0);
        //super.onDestroy();
    }

    @Override
    public void finish() {
        Log.d("STATE", "onFinish");
        super.finish();
    }
}
