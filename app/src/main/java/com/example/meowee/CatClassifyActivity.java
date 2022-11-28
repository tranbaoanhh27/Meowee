package com.example.meowee;



import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.meowee.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class CatClassifyActivity extends AppCompatActivity {
    private Button btnTakeCam, btnTakePic;
    private Button btnBuyNow;
    private ImageView imgBack, imgCart, imgCat;
    private TextView tvResult;

    String[] labels = {"Mèo Bombay", "Mèo Anh lông ngắn", "Mèo Miến Điện", "Mèo tam thể", "Mèo tam thể pha loãng", "Mèo Himalaya", "Mèo Munchkin", "Mèo Ragdoll", "Mèo Nga xanh", "Mèo Siberian"};


    static final int REQUEST_IMAGE_CAPTURE = 1;

    Bitmap ResizeToOriginal(Bitmap photo){
        Integer originalWidth = imgCat.getWidth();
        Integer originalHeight = imgCat.getHeight();
        Integer Screenwidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        Integer height = (Screenwidth*originalHeight)/originalWidth;
        Bitmap bitmap=Bitmap.createScaledBitmap(photo,Screenwidth,height, true);
        return bitmap;
    }
    int getMax(float[] arr){
        int max = 0;
        for(int i =0;i<arr.length;i++){
            if(arr[i] > arr[max]){
                max = i;
            }
        }
        return max;
    }
    int Classify(Bitmap bitmap){
        try {
            Model model = Model.newInstance(CatClassifyActivity.this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 299, 299, 3}, DataType.FLOAT32);
            bitmap  = Bitmap.createScaledBitmap(bitmap, 299, 299, true);
//
            inputFeature0.loadBuffer(bitmapToByteBuffer(bitmap, 299, 299));

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            Integer res = getMax(outputFeature0.getFloatArray()) ;


            // Releases model resources if no longer used.
            model.close();
            return res;
        } catch (IOException e) {
            // TODO Handle the exception
            return -1;
        }
    }

    public ByteBuffer bitmapToByteBuffer(Bitmap image, int width, int height) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * width * height * 3);
        byteBuffer.order(ByteOrder.nativeOrder());
        // get 1D array of width * height pixels in image
        int[] intValues = new int[width * height];
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
        int pixel = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int val = intValues[pixel++]; // RGB
                byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
            }
        }
        return byteBuffer;
    }

//    ---------------------------------------------------------------------------

    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                      Intent data = result.getData();
                        Bitmap photo = (Bitmap) data.getParcelableExtra("data");
                        Bitmap bitmap = ResizeToOriginal(photo);
                        imgCat.setImageBitmap(bitmap);

                        Integer type = Classify(bitmap);
                        tvResult.setText(labels[type]);

                    }
                }
            }
    );



    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {


//                    imgCat.setImageURI(uri);
                    Bitmap bitmap = null;
                    ContentResolver contentResolver = getContentResolver();
                    try {
                        if(Build.VERSION.SDK_INT < 28) {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                        } else {
                            ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
                            bitmap = ImageDecoder.decodeBitmap(source);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bitmap = ResizeToOriginal(bitmap);
                    imgCat.setImageBitmap(bitmap);
                    Integer type = Classify(bitmap);
                    tvResult.setText(labels[type]);



                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_classify);

        btnTakeCam = findViewById(R.id.btn_camera);
        btnTakePic = findViewById(R.id.btn_choose_img);
        btnBuyNow = findViewById(R.id.btn_buy);
        imgBack = findViewById(R.id.imgview_back);
        imgCart = findViewById(R.id.imgview_cart);
        imgCat = findViewById(R.id.imgview_cat);

        tvResult = findViewById(R.id.tv_res);

       btnTakeCam.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               launchSomeActivity.launch(takePictureIntent);

           }
       });

       btnTakePic.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               mGetContent.launch("image/*");
           }
       });


       btnBuyNow.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               System.out.println("Stop");
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