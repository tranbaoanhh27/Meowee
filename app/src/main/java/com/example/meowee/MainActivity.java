package com.example.meowee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user signed in, if not then go to sign in/sign up activity
        boolean userSignedIn = false;
        if (!userSignedIn) {
            Intent signInSignUpIntent = new Intent(MainActivity.this, SignInSignUpActivity.class);
            startActivity(signInSignUpIntent);
        }
    }
}