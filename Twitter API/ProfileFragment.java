package com.example.frkn.lasttry;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class ProfileFragment extends Fragment {

    TextView prof_name;
    SharedPreferences pref;
    Bitmap bitmap;
    ImageView prof_img, tweet, signout, post_tweet;
    EditText tweet_text;
    ProgressDialog progress;
    Dialog tDialog;
    String tweetText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        prof_name = (TextView) view.findViewById(R.id.prof_name);
        pref = getActivity().getPreferences(0);
        prof_img = (ImageView) view.findViewById(R.id.prof_image);
        tweet = (ImageView) view.findViewById(R.id.tweet);
        signout = (ImageView) view.findViewById(R.id.signout);

        signout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("ACCESS_TOKEN", "");
                edit.putString("ACCESS_TOKEN_SECRET", "");
                edit.putString("NAME", "");
                edit.putString("IMAGE_URL", "");
                edit.commit();
                Fragment login = new LoginFragment();
                FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, login);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        tweet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                tDialog = new Dialog(getActivity());
                tDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                tDialog.setContentView(R.layout.tweet_dialog);
                tweet_text = (EditText) tDialog.findViewById(R.id.tweet_text);
                post_tweet = (ImageView) tDialog.findViewById(R.id.post_tweet);

                post_tweet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new PostTweet().execute();
                    }
                });

                tDialog.show();
            }
        });

        new LoadProfile().execute();

        return view;
    }

    private class PostTweet extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Posting tweet ...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            tweetText = tweet_text.getText().toString();
            progress.show();

        }

        protected String doInBackground(String... args) {

            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(pref.getString("CONSUMER_KEY", ""));
            builder.setOAuthConsumerSecret(pref.getString("CONSUMER_SECRET", ""));


            AccessToken accessToken = new AccessToken(pref.getString("ACCESS_TOKEN", ""), pref.getString("ACCESS_TOKEN_SECRET", ""));
            Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);


            try {

                User user = twitter.verifyCredentials();
                String userHome = "Showing @" + user.getScreenName() + "'s home timeline." + "\n";
                twitter4j.Status response = user.getStatus();
                response = twitter.destroyStatus(user.getStatus().getId());

                //System.out.println("Status Id : " + user.getStatus().getId());
                //System.out.println("Response : " + response.toString());

                /*List<twitter4j.Status> statuses = twitter.getUserTimeline();
                System.out.println(userHome);
                for (twitter4j.Status status : statuses) {
                    String twiq = status.getText();
                    if (twiq.contains("robot twiq")) {
                        System.out.println("ROBOT --> " + twiq);
                        response = twitter.destroyStatus(status.getId());
                    }
                    //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                }*/
                return response.toString();
            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Failed to get timeline: " + e.getMessage());
            }


            return null;
        }

        protected void onPostExecute(String res) {
            if (res != null) {
                progress.dismiss();
                System.out.println("Successfully");
                Toast.makeText(getActivity(), "Sucessfully", Toast.LENGTH_SHORT).show();
                tDialog.dismiss();
            } else {
                progress.dismiss();
                System.out.println("Error while tweeting!");
                Toast.makeText(getActivity(), "Error while tweeting !", Toast.LENGTH_SHORT).show();
                tDialog.dismiss();
            }

        }
    }

    private class LoadProfile extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Loading Profile ...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();

        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(pref.getString("IMAGE_URL", "")).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            Bitmap image_circle = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

            BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            Canvas c = new Canvas(image_circle);
            c.drawCircle(image.getWidth() / 2, image.getHeight() / 2, image.getWidth() / 2, paint);
            prof_img.setImageBitmap(image_circle);
            prof_name.setText("Welcome " + pref.getString("NAME", ""));

            progress.hide();

        }
    }

}
