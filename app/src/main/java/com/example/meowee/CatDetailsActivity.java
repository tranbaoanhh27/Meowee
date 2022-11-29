package com.example.meowee;

import static com.example.meowee.MainActivity.currentUser;
import static com.example.meowee.MainActivity.currentUserDatabaseRef;
import static com.example.meowee.MainActivity.firebaseStorage;
import static com.example.meowee.MainActivity.fragmentFavorites;
import static com.example.meowee.Tools.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

public class CatDetailsActivity extends AppCompatActivity {
    
    private static final String TAG = "SOS!CatDetailsActivity";

    private Cat cat;
    private int quantity;

    // UI Elements
    private ImageView imageView;
    private TextView nameView, quantityView, totalPriceView, 
            ageView, colorView, sexView, moreInfoView;
    private Button buttonAddToCart;
    private ImageButton buttonMinus, buttonAdd, buttonAddToFavorite, buttonBack, buttonGoToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_details);

        cat = (Cat) getIntent().getSerializableExtra("cat");
        quantity = 1;

        findViews();
        if (cat != null) initViewValues();
        else Log.d(TAG, "extra cat from intent is null");
    }

    @SuppressLint("DefaultLocale")
    private void initViewValues() {
        nameView.setText(cat.getName());
        quantityView.setText(String.valueOf(quantity));
        totalPriceView.setText(String.format("%d đ", cat.getPrice() * quantity));
        ageView.setText(cat.getAgeLevel() == 1 ? "Mèo con (dưới 3 tháng tuổi)" : "Mèo trường thành (từ 3 tháng tuối)");
        colorView.setText(String.format("Mèo %s", cat.getColor()));
        sexView.setText(String.format("Mèo %s", cat.getIsMale() ? "đực" : "cái"));
        moreInfoView.setText(cat.getDescription());
        buttonAddToFavorite.setImageResource(
                currentUser.likeCatWithId(Cat.idOfCatWithName(cat.getName())) ?
                        R.drawable.favorite_button_selected : R.drawable.favorite_button_unselected
        );
        buttonGoToCart.setImageResource(currentUser.hasEmptyCart() ?
                R.drawable.cart_button_no_dot : R.drawable.cart_not_empty);
        
        buttonAdd.setOnClickListener(v -> handleQuantity(1));
        buttonMinus.setOnClickListener(v -> handleQuantity(-1));
        buttonAddToFavorite.setOnClickListener(v -> handleAddToFavorite());
        buttonAddToCart.setOnClickListener(v -> handleAddToCart());
        buttonBack.setOnClickListener(v -> this.finish());
        buttonGoToCart.setOnClickListener(v -> goToCart());

        StorageReference ref = firebaseStorage.getReferenceFromUrl(cat.getImageURL());
        ref.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(imageBitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Can't download image.");
            }
        });
    }

    private void goToCart() {
        startActivity(new Intent(CatDetailsActivity.this, MyCartActivity.class));
    }

    @SuppressLint("DefaultLocale")
    private void handleAddToCart() {
        Integer catId = Cat.idOfCatWithName(cat.getName());
        currentUser.increaseQuantity(String.format("CatID%d", catId), quantity);
        currentUserDatabaseRef.setValue(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    showToast(CatDetailsActivity.this, R.string.added_to_cart);
            }
        });
    }

    private void handleAddToFavorite() {
        Integer catID = Cat.idOfCatWithName(cat.getName());
        int toastMessage;
        if (currentUser.likeCatWithId(catID)) {
            buttonAddToFavorite.setImageResource(R.drawable.favorite_button_unselected);
            toastMessage = R.string.removed_from_favorites;
            currentUser.unlike(catID);
        } else {
            buttonAddToFavorite.setImageResource(R.drawable.favorite_button_selected);
            toastMessage = R.string.added_to_favorites;
            currentUser.like(catID);
        }
        fragmentFavorites.notifyAdapter();
        currentUserDatabaseRef.setValue(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    showToast(CatDetailsActivity.this, toastMessage);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void handleQuantity(int i) {
        quantity += i;
        if (quantity < 1) quantity = 1;
        quantityView.setText(String.valueOf(quantity));
        totalPriceView.setText(String.format("%d đ", cat.getPrice() * quantity));
    }

    private void findViews() {
        imageView = findViewById(R.id.imageview_cat_details);
        nameView = findViewById(R.id.textview_cat_details_name);
        quantityView = findViewById(R.id.textview_cat_details_quantity);
        totalPriceView = findViewById(R.id.textview_cat_details_total_price);
        ageView = findViewById(R.id.textview_cat_details_age);
        colorView = findViewById(R.id.textview_cat_details_color);
        sexView = findViewById(R.id.textview_cat_details_sex);
        moreInfoView = findViewById(R.id.textview_cat_details_more);
        buttonAddToCart = findViewById(R.id.button_cat_details_add_to_cart);
        buttonMinus = findViewById(R.id.button_cat_details_minus);
        buttonAdd = findViewById(R.id.button_cat_details_add);
        buttonAddToFavorite = findViewById(R.id.button_cat_details_favorite);
        buttonBack = findViewById(R.id.button_cat_details_back);
        buttonGoToCart = findViewById(R.id.button_cat_details_go_to_cart);
    }
}