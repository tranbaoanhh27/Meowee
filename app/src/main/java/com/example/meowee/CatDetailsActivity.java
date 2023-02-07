package com.example.meowee;

import static com.example.meowee.DatabaseHelper.downloadFile;
import static com.example.meowee.DatabaseHelper.syncCurrentUserFavoriteCats;
import static com.example.meowee.DatabaseHelper.syncCurrentUserQuantitiesInCart;
import static com.example.meowee.MainActivity.currentSyncedUser;
import static com.example.meowee.MainActivity.fragmentFavorites;
import static com.example.meowee.Tools.showToast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Objects;

public class CatDetailsActivity extends AppCompatActivity implements UserDataChangedListener {
    
    private static final String TAG = "SOS!CatDetailsActivity";

    private String catName;
    private int quantity;

    // Views
    private ImageView imageView;
    private TextView nameView, quantityView, totalPriceView, ageView, colorView, sexView, moreInfoView;
    private Button buttonAddToCart;
    private ImageButton buttonMinus, buttonAdd, buttonAddToFavorite, buttonBack, buttonGoToCart, buttonShare;
    ShareDialog shareDialog;

    private final UserDataChangedListener userDataChangedListener = fragmentFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_details);

        MainActivity.listenerForCatDetailsActivity = this;

        catName = getIntent().getStringExtra("catName");
        quantity = 1;
        shareDialog = new ShareDialog(this);
        findViews();
        updateViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateViews();
    }

    @SuppressLint("DefaultLocale")
    public void updateViews() {
        try {
            Cat cat = Cat.getCatByName(catName);

            if (cat != null) {
                nameView.setText(cat.getName());
                updateQuantityTextView();
                updateTotalPriceTextView();
                ageView.setText(cat.getAgeLevel() == 1 ? "Mèo con (dưới 3 tháng tuổi)" : "Mèo trưởng thành (từ 3 tháng tuổi)");
                colorView.setText(String.format("Mèo %s", cat.getColor()));
                sexView.setText(String.format("Mèo %s", cat.getIsMale() ? "đực" : "cái"));
                moreInfoView.setText(cat.getDescription());

                updateAddToFavoriteButton();

                OnSuccessListener<byte[]> onDownloadImageSuccessListener = bytes -> {
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(imageBitmap);
                };

                downloadFile(cat.getImageURL(), onDownloadImageSuccessListener);

                buttonShare.setOnClickListener(v -> {
                    String down = cat.getDownloadURL();
                    String name = cat.getName();
                    name = name.replaceAll("\\s+","");
                    String hashTag = "#"+name + "CatsMeowee";
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setShareHashtag(new ShareHashtag.Builder()
                                    .setHashtag(hashTag)
                                    .build())
                            .setContentUrl(Uri.parse(down))
                            .build();
                    shareDialog.show(content,  ShareDialog.Mode.WEB);
                });
            }

            updateButtonGoToCart();

            buttonAdd.setOnClickListener(v -> handleQuantity(1));
            buttonMinus.setOnClickListener(v -> handleQuantity(-1));
            buttonAddToFavorite.setOnClickListener(v -> handleAddToFavorite());
            buttonAddToCart.setOnClickListener(v -> handleAddToCart());
            buttonBack.setOnClickListener(v -> this.finish());
            buttonGoToCart.setOnClickListener(v -> goToCart());

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateTotalPriceTextView() {
        Cat cat = Cat.getCatByName(catName);
        totalPriceView.setText(String.format("%, d đ", Objects.requireNonNull(cat).getPrice() * quantity));
    }

    private void updateButtonGoToCart() {
        try {
            if(getResources().getBoolean(R.bool.isDarkMode)) {
                if (currentSyncedUser.hasEmptyCart()) {
                    buttonGoToCart.setImageResource(R.drawable.cart_button_no_dot_black);
                }
                else {
                    buttonGoToCart.setImageResource(R.drawable.cart_not_empty_black);
                }
            }
            else {
                if (currentSyncedUser.hasEmptyCart()) {
                    buttonGoToCart.setImageResource(R.drawable.cart_button_no_dot_white);
                }
                else {
                    buttonGoToCart.setImageResource(R.drawable.cart_not_empty_white);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private void updateQuantityTextView() {
        try {
            quantityView.setText(String.valueOf(quantity));
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private void updateAddToFavoriteButton() {
        try {
            if(getResources().getBoolean(R.bool.isDarkMode)) {
                if (currentSyncedUser.likeCatWithId(Cat.idOfCatWithName(catName))) {
                    buttonAddToFavorite.setImageResource(R.drawable.favorite_button_selected_black);
                }
                else {
                    buttonAddToFavorite.setImageResource(R.drawable.favorite_button_unselected_black);
                }
            }
            else {
                if (currentSyncedUser.likeCatWithId(Cat.idOfCatWithName(catName))) {
                    buttonAddToFavorite.setImageResource(R.drawable.favorite_button_selected_white);
                }
                else {
                    buttonAddToFavorite.setImageResource(R.drawable.favorite_button_unselected_white);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private void goToCart() {
        startActivity(new Intent(CatDetailsActivity.this, MyCartActivity.class));
    }

    @SuppressLint("DefaultLocale")
    private void handleAddToCart() {
        try {
            Integer catId = Cat.idOfCatWithName(catName);
            currentSyncedUser.increaseQuantity(String.format("CatID%d", catId), quantity);

            OnCompleteListener<Void> onCompleteListener = task -> {
                if (task.isSuccessful())
                    showToast(CatDetailsActivity.this, R.string.added_to_cart);
            };

            syncCurrentUserQuantitiesInCart(onCompleteListener);
            updateButtonGoToCart();

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    private void handleAddToFavorite() {
        try {
            Integer catID = Cat.idOfCatWithName(catName);
            int toastMessage;

            if (currentSyncedUser.likeCatWithId(catID)) {
                toastMessage = R.string.removed_from_favorites;
                currentSyncedUser.unlike(catID);
            } else {
                toastMessage = R.string.added_to_favorites;
                currentSyncedUser.like(catID);
            }

            OnCompleteListener<Void> onCompleteListener = task -> {
                if (task.isSuccessful())
                    showToast(CatDetailsActivity.this, toastMessage);
            };

            syncCurrentUserFavoriteCats(onCompleteListener);

            userDataChangedListener.updateUserRelatedViews();
            updateAddToFavoriteButton();

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    @SuppressLint("DefaultLocale")
    private void handleQuantity(int i) {
        quantity += i;
        if (quantity < 1) quantity = 1;
        updateQuantityTextView();
        updateTotalPriceTextView();
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
        buttonShare = findViewById(R.id.btn_share);
    }

    @Override
    public void updateUserRelatedViews() {
        updateButtonGoToCart();
        updateAddToFavoriteButton();
    }
}