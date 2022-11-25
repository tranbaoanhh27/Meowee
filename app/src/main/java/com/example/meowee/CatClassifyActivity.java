package com.example.meowee;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CatClassifyActivity extends AppCompatActivity {
    private Button btnTakeCam, btnTakePic;

    private Button btnBuyNow;
    private ImageView imgBack, imgCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cat_classify_activity);

        btnTakeCam = findViewById(R.id.btn_camera);
        btnTakePic = findViewById(R.id.btn_choose_img);
        btnBuyNow = findViewById(R.id.btn_buy);
        imgBack = findViewById(R.id.imgview_back);
        imgCart = findViewById(R.id.imgview_cart);

       btnTakeCam.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

           }
       });

       btnTakePic.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

           }
       });


       btnBuyNow.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

           }
       });

       imgBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

           }
       });
       imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }
}