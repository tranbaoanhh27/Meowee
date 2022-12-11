package com.example.meowee;

import static com.example.meowee.MainActivity.currentSyncedUser;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowee.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;


public class CatClassifyActivity extends AppCompatActivity implements UserDataChangedListener {
    private Button btnTakeCam, btnTakePic;
    private Button btnBuyNow;
    private ImageView imgBack, imgCart, imgCat;
    private TextView tvResult, tvNotif;

    String[] labels = {"Mèo Bombay", "Mèo Anh lông ngắn", "Mèo Miến Điện", "Mèo tam thể", "Mèo tam thể pha loãng", "Mèo Himalaya", "Mèo Munchkin", "Mèo Ragdoll", "Mèo Nga xanh", "Mèo Siberian"};


    static final int REQUEST_IMAGE_CAPTURE = 1;

    Bitmap ResizeToOriginal(Bitmap photo) {
        int originalWidth = imgCat.getWidth();
        int originalHeight = imgCat.getHeight();
        int Screenwidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = (Screenwidth * originalHeight) / originalWidth;
        return Bitmap.createScaledBitmap(photo, Screenwidth, height, true);
    }

    int getMax(float[] arr) {
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > arr[max]) {
                max = i;
            }
        }
        return max;
    }

    float[] Classify(Bitmap bitmap) {
        try {
            Model model = Model.newInstance(CatClassifyActivity.this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 299, 299, 3}, DataType.FLOAT32);
            bitmap = Bitmap.createScaledBitmap(bitmap, 299, 299, true);
//
            inputFeature0.loadBuffer(bitmapToByteBuffer(bitmap, 299, 299));

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] prob = outputFeature0.getFloatArray();


            // Releases model resources if no longer used.
            model.close();
            return prob;
        } catch (IOException e) {
            // TODO Handle the exception
            return null;
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
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bitmap photo = Objects.requireNonNull(data).getParcelableExtra("data");
                        Bitmap bitmap = ResizeToOriginal(photo);
                        imgCat.setImageBitmap(bitmap);
                        tvResult.setVisibility(View.VISIBLE);
                        tvNotif.setVisibility(View.VISIBLE);

                        float[] prob = Classify(bitmap);
                        int type = getMax(prob);

                        int max_prob = Math.round(prob[type] * 100);

                        if (max_prob > 50) {
                            tvResult.setText(labels[type]);
                            String prob_text = "Chúng tôi cho rằng khoảng " + max_prob + "% đây là một con";
                            tvNotif.setText(prob_text);
                            updateBuyNowButton();
                        } else {
                            tvResult.setText("Vui lòng đổi ảnh khác !");
                            tvNotif.setText("Chúng tôi không thể nhận diện ảnh này");
                        }

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
                        if (Build.VERSION.SDK_INT < 28) {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                        } else {
                            ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
                            bitmap = ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.RGBA_F16, true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        bitmap = ResizeToOriginal(bitmap);
                        imgCat.setImageBitmap(bitmap);
                        imgCat.setClipToOutline(true);
                        float[] prob = Classify(bitmap);
                        int type = getMax(prob);

                        int max_prob = Math.round(prob[type] * 100);

                        if (max_prob > 50) {
                            tvResult.setText(labels[type]);
                            String prob_text = "Chúng tôi cho rằng khoảng " + max_prob + "% đây là một con";
                            tvNotif.setText(prob_text);
                            updateBuyNowButton();
                        } else {
                            tvResult.setText("Vui lòng đổi ảnh khác !");
                            tvNotif.setText("Chúng tôi không thể nhận diện ảnh này");
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_classify);

        MainActivity.listenerForCatClassifyActivity = this;

        btnTakeCam = findViewById(R.id.btn_camera);
        btnTakePic = findViewById(R.id.btn_choose_img);
        btnBuyNow = findViewById(R.id.btn_buy);
        imgBack = findViewById(R.id.imgview_back);
        imgCart = findViewById(R.id.imgview_cart);
        imgCat = findViewById(R.id.imgview_cat);
        tvResult = findViewById(R.id.tv_res);
        tvNotif = findViewById(R.id.tv_notifi_res);

        tvResult.setText("");
        tvNotif.setText("");
        imgCat.setClipToOutline(true);
        updateCartButton();
        updateBuyNowButton();

        btnTakeCam.setOnClickListener(view -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            launchSomeActivity.launch(takePictureIntent);
        });

        btnTakePic.setOnClickListener(view -> mGetContent.launch("image/*"));

        btnBuyNow.setOnClickListener(view -> {
            Integer id = Cat.idOfCatWithName(tvResult.getText().toString());

            if (id != -1) {
                String id_str = "CatID" + id.toString();
                Cat cat = Cat.getCatById(id_str);

                Intent CatDetail = new Intent(CatClassifyActivity.this, CatDetailsActivity.class);
                CatDetail.putExtra("catName", cat.getName());
                startActivity(CatDetail);
            }
        });

        imgBack.setOnClickListener(view -> {
            CatClassifyActivity.this.finish();
        });

        imgCart.setOnClickListener(view -> {
            Intent Cart = new Intent(CatClassifyActivity.this, MyCartActivity.class);
            startActivity(Cart);
        });
    }

    @Override
    public void updateUserRelatedViews() {
        updateCartButton();
    }

    private void updateCartButton() {
        if (imgCart == null)
            imgCart = findViewById(R.id.imgview_cart);
        if (currentSyncedUser != null)
            imgCart.setImageResource(
                    currentSyncedUser.hasEmptyCart() ?
                            R.drawable.cart_button_no_dot
                            :
                            R.drawable.cart_not_empty
            );
    }

    private void updateBuyNowButton() {
        if (tvResult == null) tvResult = findViewById(R.id.tv_res);
        Log.d("fuck", tvResult.getText().toString());
        btnBuyNow.setVisibility(TextUtils.isEmpty(tvResult.getText().toString()) ? View.INVISIBLE : View.VISIBLE);
    }
}