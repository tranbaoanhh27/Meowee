package com.example.meowee;

import static com.example.meowee.MainActivity.firebaseAuth;
import static com.example.meowee.Tools.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class PasswordResetActivity extends AppCompatActivity {

    // UI Elements
    private TextInputEditText inputEmail;
    private Button buttonSubmit;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        inputEmail = (TextInputEditText) findViewById(R.id.edittextResetPasswordEmail);
        buttonSubmit = (Button) findViewById(R.id.buttonResetPasswordSubmit);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_reset_password);

        buttonSubmit.setOnClickListener(v -> handleResetPassword());
    }

    private void handleResetPassword() {
        String email = Objects.requireNonNull(inputEmail.getText()).toString();
        if (TextUtils.isEmpty(email))
            showToast(this, R.string.enter_your_email);
        else if (!Tools.emailPatternValidate(email))
            showToast(PasswordResetActivity.this, R.string.email_seem_like_incorrect_please_check);
        else {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        String message = String.format(
                                "Chúng tôi đã gửi email đến địa chỉ %s, hãy kiểm tra email và đặt lại mật khẩu nhé!",
                                email
                        );
                        Toast.makeText(PasswordResetActivity.this, message, Toast.LENGTH_LONG).show();
                        PasswordResetActivity.this.finish();
                    }
                }
            });
        }
    }
}