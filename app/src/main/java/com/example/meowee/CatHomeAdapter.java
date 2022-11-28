package com.example.meowee;

import static com.example.meowee.MainActivity.firebaseStorage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CatHomeAdapter extends RecyclerView.Adapter<CatHomeAdapter.ViewHolder> {

    private final String TAG = "SOS!CatHomeAdapter";

    private ArrayList<Cat> cats;
    private Context context;

    public CatHomeAdapter(Context context, ArrayList<Cat> cats) {
        this.context = context;
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
                .inflate(R.layout.home_cat_itemview, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Cat cat = cats.get(position);
            Log.d(TAG, cat.toString());
            holder.nameView.setText(cat.getName());
            holder.priceView.setText(String.format("%d đ", cat.getPrice()));
            holder.typesView.setText(
                    String.format("Mèo %s, Mèo %s, Màu %s",
                            cat.getAgeLevel() == 1 ? "con" : "trưởng thành",
                            cat.getIsMale() ? "đực" : "cái",
                            cat.getColor())
            );
            holder.layout.setOnClickListener(v -> startCatDetailsActivity(cat));
            StorageReference ref = firebaseStorage.getReferenceFromUrl(cat.getImageURL());
            ref.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.imageView.setImageBitmap(imageBitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Can't download image.");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCatDetailsActivity(Cat cat) {
        Intent intent = new Intent(context, CatDetailsActivity.class);
        intent.putExtra("cat", cat);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return cats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameView, priceView, typesView;
        private ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageview_cat_home_itemview);
            nameView = (TextView) itemView.findViewById(R.id.textview_catname_home_itemview);
            priceView = (TextView) itemView.findViewById(R.id.textview_catprice_home_itemview);
            typesView = (TextView) itemView.findViewById(R.id.textview_cattype_home_itemview);
            layout = (ConstraintLayout) itemView.findViewById(R.id.layout_cat_home_itemview);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterByName(String queryText) {
        this.cats.clear();
        if (TextUtils.isEmpty(queryText))
            this.setCats(Cat.allCats);
        else {
            for (Cat cat : Cat.allCats) {
                if (cat.hasNameSimilarTo(queryText))
                    this.cats.add(cat.clone());
            }
        }
        notifyDataSetChanged();
    }
}
