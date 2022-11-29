package com.example.meowee;

import static com.example.meowee.MainActivity.currentUser;
import static com.example.meowee.MainActivity.firebaseStorage;

import android.annotation.SuppressLint;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CatCartAdapter extends RecyclerView.Adapter<CatCartAdapter.ViewHolder> {

    private final String TAG = "SOS!CatCartAdapter";

    private ArrayList<Cat> cats;

    public CatCartAdapter(ArrayList<Cat> cats) {
        this.setCats(cats);
    }

    public void setCats(ArrayList<Cat> cats) {
        this.cats = new ArrayList<Cat>();
        for (Cat cat : cats) {
            this.cats.add(cat.clone());
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
        if (cat != null) initHolderViews(cat, holder);
    }

    @SuppressLint("DefaultLocale")
    private void initHolderViews(Cat cat, @NonNull CatCartAdapter.ViewHolder viewHolder) {
        viewHolder.nameView.setText(cat.getName());
        viewHolder.priceView.setText(String.format("%d đ", cat.getPrice()));
        viewHolder.typesView.setText(String.format("Mèo %s, Mèo %s, Màu %s",
                cat.getAgeLevel() == 1 ? "con" : "trưởng thành",
                cat.getIsMale() ? "đực" : "cái",
                cat.getColor())
        );
        viewHolder.quantityView.setText(
                String.valueOf(currentUser.getQuantityOf(Cat.idOfCatWithName(cat.getName()))));
        viewHolder.buttonDecreaseQuantity.setOnClickListener(v -> handleDecreaseQuantity());
        viewHolder.buttonIncreaseQuantity.setOnClickListener(v -> handleIncreaseQuantity());

        StorageReference ref = firebaseStorage.getReferenceFromUrl(cat.getImageURL());
        ref.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                viewHolder.imageView.setImageBitmap(imageBitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Can't download image.");
            }
        });
    }

    private void handleIncreaseQuantity() {
    }

    private void handleDecreaseQuantity() {
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
            if (currentUser.addedToCartCatWithId(Cat.idOfCatWithName(cat.getName())))
                this.cats.add(cat.clone());
        }
        notifyDataSetChanged();
    }
}
