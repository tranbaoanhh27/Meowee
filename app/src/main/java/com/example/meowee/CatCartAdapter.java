package com.example.meowee;

import static com.example.meowee.DatabaseHelper.downloadFile;
import static com.example.meowee.DatabaseHelper.syncCurrentUserQuantitiesInCart;
import static com.example.meowee.MainActivity.currentSyncedUser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class CatCartAdapter extends RecyclerView.Adapter<CatCartAdapter.ViewHolder> {

    private final String TAG = "SOS!CatCartAdapter";

    private ArrayList<Cat> cats;
    private final UserDataChangedListener userDataChangedListener;

    public CatCartAdapter(ArrayList<Cat> cats, Context context) {
        this.setCats(cats);
        userDataChangedListener = (UserDataChangedListener) context;
    }

    public void setCats(ArrayList<Cat> cats) {
        this.cats = new ArrayList<>();
        for (Cat cat : cats) {
            this.cats.add(cat.deepCopy());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_cat_itemview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cat cat = cats.get(position);
        updateHolderViews(cat, holder);
    }

    @SuppressLint("DefaultLocale")
    private void updateHolderViews(Cat cat, @NonNull CatCartAdapter.ViewHolder viewHolder) {
        try {
            if (cat != null) {
                viewHolder.nameView.setText(cat.getName());
                viewHolder.priceView.setText(cat.getDisplayablePrice());
                viewHolder.typesView.setText(cat.getDisplayableTypes());

                updateViewHolderQuantityTextView(cat, viewHolder);

                viewHolder.buttonDecreaseQuantity.setOnClickListener(
                        v -> handleDecreaseQuantity(cat.getStringId(), cat, viewHolder));

                viewHolder.buttonIncreaseQuantity.setOnClickListener(
                        v -> handleIncreaseQuantity(cat.getStringId(), cat, viewHolder));

                OnSuccessListener<byte[]> onDownloadImageSuccessListener = bytes -> {
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    viewHolder.imageView.setImageBitmap(imageBitmap);
                };

                downloadFile(cat.getImageURL(), onDownloadImageSuccessListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateViewHolderQuantityTextView(Cat cat, ViewHolder viewHolder) {
        try {
            viewHolder.quantityView.setText(
                    String.valueOf(currentSyncedUser.getQuantityOf(Cat.idOfCatWithName(cat.getName()))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleIncreaseQuantity(String catId, Cat cat, ViewHolder viewHolder) {
        try {
            currentSyncedUser.increaseQuantity(catId, 1);
            syncCurrentUserQuantitiesInCart(task -> {
                if (task.isSuccessful())
                    Log.d(TAG, "Increased quantity");
            });
            updateViewHolderQuantityTextView(cat, viewHolder);
            userDataChangedListener.updateUserRelatedViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDecreaseQuantity(String catId, Cat cat, ViewHolder viewHolder) {
        try {
            currentSyncedUser.decreaseQuantity(catId, 1);
            syncCurrentUserQuantitiesInCart(task -> {
                if (task.isSuccessful())
                    Log.d(TAG, "Increased quantity");
            });
            updateViewHolderQuantityTextView(cat, viewHolder);
            userDataChangedListener.updateUserRelatedViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return cats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameView, priceView, typesView, quantityView;
        private Button buttonDecreaseQuantity, buttonIncreaseQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            findViewsByIds(itemView);
        }

        private void findViewsByIds(View view) {
            imageView = view.findViewById(R.id.imageview_cat_cart_itemview);
            nameView = view.findViewById(R.id.textview_catname_cart_itemview);
            priceView = view.findViewById(R.id.textview_catprice_cart_itemview);
            typesView = view.findViewById(R.id.textview_cattype_cart_itemview);
            quantityView = view.findViewById(R.id.textview_cat_cart_itemview_quantity);
            buttonDecreaseQuantity = view.findViewById(R.id.button_cat_cart_itemview_decrease_quantity);
            buttonIncreaseQuantity = view.findViewById(R.id.button_cat_cart_itemview_increase_quantity);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterByInCart() {
        this.cats.clear();
        for (Cat cat : Cat.allCats) {
            if (currentSyncedUser.addedToCartCatWithId(Cat.idOfCatWithName(cat.getName())))
                this.cats.add(cat.deepCopy());
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapter() {
        notifyDataSetChanged();
    }
}
