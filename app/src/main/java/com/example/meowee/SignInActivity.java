package com.example.meowee;

import static com.example.meowee.MainActivity.firebaseAuth;
import static com.example.meowee.MainActivity.firebaseUser;
import static com.example.meowee.Tools.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SOS!SignInActivity";

    private TextView goToSignUp, forgotPassword;
    private TextInputEditText emailInput, passwordInput;
    private MaterialButton submitButton;
    private ProgressBar progressBarSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        goToSignUp = (TextView) findViewById(R.id.textviewSignInGoToSignUp);
        forgotPassword = (TextView) findViewById(R.id.textviewSignInForgetPassword);
        emailInput = (TextInputEditText) findViewById(R.id.edittextSignInEmail);
        passwordInput = (TextInputEditText) findViewById(R.id.edittextSignInPassword);
        submitButton = (MaterialButton) findViewById(R.id.buttonSignInSubmit);
        progressBarSignIn = (ProgressBar) findViewById(R.id.progressbar_login);

        goToSignUp.setOnClickListener(v -> startSignUpActivity());
        submitButton.setOnClickListener(v -> handleSignInSubmit());
        forgotPassword.setOnClickListener(v -> startResetPasswordActivity());
    }

    private void startResetPasswordActivity() {
        Intent intent = new Intent(SignInActivity.this, PasswordResetActivity.class);
        startActivity(intent);
    }

    private void handleSignInSubmit() {
        String email, password;
        email = Objects.requireNonNull(emailInput.getText()).toString();
        password = Objects.requireNonNull(passwordInput.getText()).toString();

        boolean validInputs = !email.equals("") && !password.equals("");

        if (!validInputs)
            showToast(this, R.string.dont_let_email_password_empty);
        else {
            progressBarSignIn.setVisibility(ProgressBar.VISIBLE);
            if (firebaseAuth != null)
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(onSignInComplete);
            else
                Log.d(TAG, "handleSignInSubmit: firebaseAuth is null");
        }
    }

    private final OnCompleteListener<AuthResult> onSignInComplete = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            progressBarSignIn.setVisibility(ProgressBar.GONE);
            if (task.isSuccessful()) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    if (!firebaseUser.isEmailVerified())
                        showToast(SignInActivity.this, R.string.you_havent_verify_email);
                    else {
                        showToast(SignInActivity.this, R.string.sign_in_sucess);
                        startMainActivity();
                    }
                }
            } else showToast(SignInActivity.this, R.string.sign_in_failed);
        }
    };

    private void startMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void startSignUpActivity() {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}