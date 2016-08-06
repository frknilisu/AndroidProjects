package com.example.frkn.basicfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String FIREBASE_URL = "https://basicdb-53847.firebaseio.com/";
    public static final String Email = "koruyucu5@hotmail.com";
    public static final String Password = "ehlenvesehlen4271";
    private static final String TAG = "EmailPassword";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);

        final EditText nameTxt = (EditText) findViewById(R.id.nameText);
        final EditText socialTxt = (EditText) findViewById(R.id.socialText);
        final EditText mobileTxt = (EditText) findViewById(R.id.mobileText);
        final Button save = (Button) findViewById(R.id.saveButton);

        //Firebase ref = new Firebase(FIREBASE_URL);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    Toast.makeText(getApplicationContext(), "auth_failed",
                            Toast.LENGTH_SHORT).show();
                }

                myRef = FirebaseDatabase.getInstance().getReference();

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = nameTxt.getText().toString();
                        String social = socialTxt.getText().toString();
                        Long mobile = Long.parseLong(mobileTxt.getText().toString());
                        writeData(name, social, mobile);
                    }
                });

                ChildEventListener childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                        Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Gson gson = new Gson();
                            final User user = gson.fromJson(child.getValue().toString(), User.class);
                            System.out.println("Name: " + user.name);
                            System.out.println("Email: " + user.email);
                            System.out.println("Number: " + user.number.toString());
                        }

                        // ...
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                        Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                        // A comment has changed, use the key to determine if we are displaying this
                        // comment and if so displayed the changed comment.
                        User comment = dataSnapshot.getValue(User.class);
                        String commentKey = dataSnapshot.getKey();

                        // ...
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                        // A comment has changed, use the key to determine if we are displaying this
                        // comment and if so remove it.
                        String commentKey = dataSnapshot.getKey();

                        // ...
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                        Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                        // A comment has changed position, use the key to determine if we are
                        // displaying this comment and if so move it.
                        User comment = dataSnapshot.getValue(User.class);
                        String commentKey = dataSnapshot.getKey();

                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                        Toast.makeText(getApplicationContext(), "Failed to load comments.",
                                Toast.LENGTH_SHORT).show();
                    }
                };
                myRef.addChildEventListener(childEventListener);

            }
        });
    }

    private void writeData(String name, String social, long number){
        // Write a message to the database
        String key = myRef.child("users").push().getKey();

        User newUser = new User(name, social, number);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/"+key, newUser.toMap());

        myRef.updateChildren(childUpdates);
    }


}
