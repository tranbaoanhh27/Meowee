package com.example.meowee;

import static com.example.meowee.DatabaseHelper.downloadFile;
import static com.example.meowee.MainActivity.currentSyncedUser;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.ViewHolder> {

    private final String TAG = "SOS!CatAdapter";

    private ArrayList<Cat> cats;
    private Context context;

    public CatAdapter(Context context, ArrayList<Cat> cats) {
        this.context = context;
        this.setCats(cats);
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
            holder.priceView.setText(cat.getDisplayablePrice());
            holder.typesView.setText(cat.getDisplayableTypes());
            holder.layout.setOnClickListener(v -> startCatDetailsActivity(cat));

            OnSuccessListener<byte[]> onDownloadImageSuccessListener = bytes -> {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imageView.setImageBitmap(imageBitmap);
            };

            downloadFile(cat.getImageURL(), onDownloadImageSuccessListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCatDetailsActivity(Cat cat) {
        try {
            Intent intent = new Intent(context, CatDetailsActivity.class);
            intent.putExtra("catName", cat.getName());
            context.startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
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
            imageView = itemView.findViewById(R.id.imageview_cat_home_itemview);
            nameView = itemView.findViewById(R.id.textview_catname_home_itemview);
            priceView = itemView.findViewById(R.id.textview_catprice_home_itemview);
            typesView = itemView.findViewById(R.id.textview_cattype_home_itemview);
            layout = itemView.findViewById(R.id.layout_cat_home_itemview);
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
                    this.cats.add(cat.deepCopy());
            }
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterByOptions(ArrayList<String> selectedSex, ArrayList<Integer> selectedAge,
                                Integer minPrice, Integer maxPrice, ArrayList<String> selectedColor) {
        this.cats.clear();
        for (Cat cat : Cat.allCats) {
            String male = cat.getIsMale() ? "male" : "female";
            boolean matchColor = false;
            String[] colors = cat.getColor().replaceAll(" ", "").split(",");
            for (String color : colors) {
                if (selectedColor.contains(color)) {
                    matchColor = true;
                    break;
                }
            }
            if ((selectedSex.isEmpty() || selectedSex.contains(male))
                    && (selectedAge.isEmpty() || selectedAge.contains(cat.getAgeLevel()))
                    && cat.hasPriceInRange(minPrice, maxPrice)
                    && (selectedColor.isEmpty() || matchColor)) {
                this.cats.add(cat.deepCopy());
            }
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterByFavorite() {
        this.cats.clear();
        for (Cat cat : Cat.allCats) {
            if ((currentSyncedUser != null) && currentSyncedUser.likeCatWithId(Cat.idOfCatWithName(cat.getName())))
                this.cats.add(cat.deepCopy());
        }
        notifyDataSetChanged();
    }
}
