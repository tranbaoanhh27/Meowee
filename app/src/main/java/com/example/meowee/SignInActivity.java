package com.example.meowee;

import static com.example.meowee.MainActivity.currentUser;
import static com.example.meowee.MainActivity.currentUserDatabaseRef;
import static com.example.meowee.MainActivity.firebaseAuth;
import static com.example.meowee.MainActivity.firebaseDatabase;
import static com.example.meowee.MainActivity.firebaseUser;
import static com.example.meowee.Tools.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private TextView goToSignUp, forgotPassword;
    private TextInputEditText emailInput, passwordInput;
    private MaterialButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        goToSignUp = (TextView) findViewById(R.id.textviewSignInGoToSignUp);
        forgotPassword = (TextView) findViewById(R.id.textviewSignInForgetPassword);
        emailInput = (TextInputEditText) findViewById(R.id.edittextSignInEmail);
        passwordInput = (TextInputEditText) findViewById(R.id.edittextSignInPassword);
        submitButton = (MaterialButton) findViewById(R.id.buttonSignInSubmit);

        goToSignUp.setOnClickListener(v -> startSignUpActivity());
        submitButton.setOnClickListener(v -> handleSignInSubmit());
    }

    private void handleSignInSubmit() {
        String email = Objects.requireNonNull(emailInput.getText()).toString();
        String password = Objects.requireNonNull(passwordInput.getText()).toString();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(onSignInComplete);
    }

    private final OnCompleteListener<AuthResult> onSignInComplete = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
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