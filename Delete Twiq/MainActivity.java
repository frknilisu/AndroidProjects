package com.example.frkn.twiqq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.List;
import java.util.ListIterator;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "TfQT9u8z7dxkHJ5U6o0rqfTOv";
    private static final String TWITTER_SECRET = "HD0IL6oX3C2ibwCvJrUdOx4dYBoTyDACOR5yusqT5PfEF6tkDm";
    private static int statusCount = 0;

    TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());
        setContentView(R.layout.activity_main);

        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                //Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                Log.d("Login", "Success => " + result.data.getUserName());

                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();

                final StatusesService statusesService = twitterApiClient.getStatusesService();

                setStatusCount(statusesService, result.data.getUserId(), result.data.getUserName());
                searchTweet(statusesService, result.data.getUserId(), result.data.getUserName(), "Tweet");


                //deleteAllTweet(statusesService, result.data.getUserId(), result.data.getUserName());

            }

            @Override
            public void failure(TwitterException e) {

            }
        });

    }

    private void searchTweet(StatusesService statusesService, long userId, String userName, final String tweet) {
        statusesService.userTimeline(userId, userName, statusCount, null, null, false, false, false, false, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> res) {
                ListIterator<Tweet> it = res.data.listIterator();
                int count = 0;
                while (it.hasNext()) {
                    String currentTweet = it.next().text;
                    if (currentTweet.toLowerCase().contains(tweet.toLowerCase())) {
                        count++;
                        Toast.makeText(getApplicationContext(), count + ". Tweet : " + currentTweet, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }

    private void setStatusCount(StatusesService statusesService, long userId, String userName) {
        statusesService.userTimeline(userId, userName, 1, null, null, false, false, false, false, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> res) {
                ListIterator<Tweet> it = res.data.listIterator();
                if (it.hasNext()) {
                    statusCount = it.next().user.statusesCount;
                    //Log.d("Status Count", String.valueOf(statusCount));
                } else {
                    statusCount = 0;
                    Toast.makeText(getApplicationContext(), "There is no tweet in your account", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }

    private void deleteAllTweet(final StatusesService statusesService, long userId, String userName) {
        statusesService.userTimeline(userId, userName, statusCount, null, null, false, false, false, false, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> res) {
                ListIterator<Tweet> it = res.data.listIterator();
                while (it.hasNext()) {
                    statusesService.destroy(it.next().getId(), false, new Callback<Tweet>() {
                        @Override
                        public void success(Result<Tweet> result) {
                            //Log.d("Delete", "Tweet was deleted");
                        }

                        @Override
                        public void failure(TwitterException e) {

                        }
                    });
                }
                if (statusCount == 0)
                    Toast.makeText(getApplicationContext(), "No tweet in your account", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "All tweets(" + statusCount + ") were deleted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}
