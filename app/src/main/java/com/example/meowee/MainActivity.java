package com.example.meowee;

import static com.example.meowee.Tools.showToast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static FirebaseAuth firebaseAuth;
    public static FirebaseUser firebaseUser;
    public static FirebaseDatabase firebaseDatabase;
    public static User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        Log.d(TAG, firebaseDatabase.getReference().toString());

        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Intent signInSignUpIntent = new Intent(MainActivity.this, SignInSignUpActivity.class);
            startActivity(signInSignUpIntent);
        } else {
            String userEmail = firebaseUser.getEmail();
            Toast.makeText(this, "Chào mừng! " + userEmail, Toast.LENGTH_LONG).show();
            // TODO: Show the home UI.
        }
    }
}