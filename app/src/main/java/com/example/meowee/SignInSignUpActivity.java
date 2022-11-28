package com.example.meowee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;

public class SignInSignUpActivity extends AppCompatActivity {

    private final String TAG = "SOS!SignInSignUpActivity";

    private MaterialButton buttonSignIn;
    private  MaterialButton buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_sign_up);

        buttonSignIn = (MaterialButton) findViewById(R.id.buttonSignIn);
        buttonSignUp = (MaterialButton) findViewById(R.id.buttonSignUp);

        buttonSignIn.setOnClickListener(v -> startSignInActivity());
        buttonSignUp.setOnClickListener(v -> startSignUpActivity());
    }

    private void startSignInActivity() {
        Intent intent = new Intent(SignInSignUpActivity.this, SignInActivity.class);
        startActivity(intent);
    }

    private void startSignUpActivity() {
        Intent intent = new Intent(SignInSignUpActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}