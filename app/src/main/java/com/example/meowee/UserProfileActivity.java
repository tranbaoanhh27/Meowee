package com.example.meowee;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView imgviewTakeCam;
    private EditText edtPhone, edtName, edtEmail, edtSex, edtBirthDay;
    private Button btnUpdateInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        imgviewTakeCam = findViewById(R.id.imgview_takecam);
        edtPhone = findViewById(R.id.edt_phone);
        edtEmail = findViewById(R.id.edt_email);
        edtSex = findViewById(R.id.edt_sex);
        edtBirthDay = findViewById(R.id.edt_birthday);

        imgviewTakeCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}