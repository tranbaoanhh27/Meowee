package com.example.meowee;

import static com.example.meowee.MainActivity.firebaseAuth;
import static com.example.meowee.MainActivity.firebaseUser;
import static com.example.meowee.Tools.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private final String TAG = "SOS!SignUpActivity";

    private TextInputEditText emailInput, passwordInput, nameInput, phoneNumberInput;
    private ProgressBar progressBarSignUp;

    private String email, password, fullName, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView goToSignIn = findViewById(R.id.textviewSignUpGoToSignIn);
        emailInput = findViewById(R.id.edittextSignUpEmail);
        passwordInput = findViewById(R.id.edittextSignUpPassword);
        nameInput = findViewById(R.id.edittextSignUpFullname);
        phoneNumberInput = findViewById(R.id.edittextSignUpPhone);
        MaterialButton submitButton = findViewById(R.id.buttonSignUpSubmit);
        progressBarSignUp = findViewById(R.id.progressbar_signup);

        goToSignIn.setOnClickListener(v -> startSignInActivity());
        submitButton.setOnClickListener(v -> handleSignUpAccount());
    }

    private boolean checkInputRequirements() {

        if (!Tools.emailPatternValidate(email)) {
            showToast(this, R.string.invalid_email);
            return false;
        }

        if (!Tools.checkValidName(fullName)) {
            showToast(this, R.string.invalid_name);
            return false;
        }

        if (!Tools.checkValidPhoneNumber(phoneNumber)) {
            showToast(this, R.string.invalid_phone_num);
            return false;
        }

        if (!Tools.checkValidPassword(password)) {
            showToast(this, R.string.invalid_password);
            return false;
        }

        return true;
    }

    private void handleSignUpAccount() {
        email = Objects.requireNonNull(emailInput.getText()).toString();
        password = Objects.requireNonNull(passwordInput.getText()).toString();
        fullName = Objects.requireNonNull(nameInput.getText()).toString();
        phoneNumber = Objects.requireNonNull(phoneNumberInput.getText()).toString();

        if (checkInputRequirements()) {
            progressBarSignUp.setVisibility(ProgressBar.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, onCreateAccountCompleteListener);
        }
    }

    private final OnCompleteListener<AuthResult> onCreateAccountCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null)
                    firebaseUser.sendEmailVerification()
                            .addOnCompleteListener(onSendVerificationEmailComplete);
            } else {
                progressBarSignUp.setVisibility(ProgressBar.GONE);
                showToast(SignUpActivity.this, R.string.failed_to_register);
            }
        }
    };

    private final OnCompleteListener<Void> onSendVerificationEmailComplete = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                createNewUserInDatabase(fullName, email, phoneNumber);
                progressBarSignUp.setVisibility(ProgressBar.GONE);
                showToast(SignUpActivity.this, R.string.register_successful);
                startSignInActivity();
            } else {
                progressBarSignUp.setVisibility(ProgressBar.GONE);
                showToast(SignUpActivity.this, R.string.send_verification_email_failed);
            }
        }
    };

    private void createNewUserInDatabase(String name, String email, String phoneNumber) {
        User newUser = new User(name, phoneNumber, email);
        newUser.saveToDatabase().addOnCompleteListener(task -> {
            if (task.isSuccessful())
                Log.d(TAG, "Successfully saved new user to database.");
            else
                Log.d(TAG, "Failed to save new user to database");
        });
    }

    private void startSignInActivity() {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}