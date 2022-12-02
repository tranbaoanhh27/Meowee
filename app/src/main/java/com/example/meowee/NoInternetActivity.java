package com.example.meowee;

import static com.example.meowee.Tools.showToast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.button.MaterialButton;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        MaterialButton buttonCheckAgain = findViewById(R.id.buttonTryConnectAgain);
        buttonCheckAgain.setOnClickListener(v -> checkConnection());
    }

    private void checkConnection() {
        if (Tools.isOnline())
            NoInternetActivity.this.finish();
        else
            showToast(this, R.string.still_no_internet);
    }
}