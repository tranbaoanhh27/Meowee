package com.example.meowee;

import static com.example.meowee.MainActivity.currentSyncedUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class MyCartActivity extends AppCompatActivity implements UserDataChangedListener {

    private RecyclerView recyclerView;
    private CatCartAdapter adapter;
    private TextView catPriceView, deliverFeeView, totalPriceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

        ImageButton buttonBack = (ImageButton) findViewById(R.id.button_cart_back);
        buttonBack.setOnClickListener(v -> MyCartActivity.this.finish());

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_cart);
        adapter = new CatCartAdapter(Cat.allCats, this);
        adapter.filterByInCart();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        catPriceView = findViewById(R.id.textview_cart_cat_value);
        deliverFeeView = findViewById(R.id.textview_cart_deliver_fee);
        totalPriceView = findViewById(R.id.textview_cart_total);

        updateMoneyViews();
    }

    @SuppressLint("DefaultLocale")
    public void updateMoneyViews() {
        if (currentSyncedUser != null) {
            int catPrice = currentSyncedUser.getTotalCatPriceInCart();
            int deliverFee = catPrice > 0 ? 100000 : 0;

            catPriceView.setText(String.format("%, d đ", catPrice));
            deliverFeeView.setText(String.format("%, d đ", deliverFee));
            totalPriceView.setText(String.format("%, d đ", catPrice + deliverFee));
        }
    }

    public void updateRecyclerView() {
        try {
            adapter.filterByInCart();
            adapter.notifyAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUserRelatedViews() {
        updateRecyclerView();
        updateMoneyViews();
    }
}