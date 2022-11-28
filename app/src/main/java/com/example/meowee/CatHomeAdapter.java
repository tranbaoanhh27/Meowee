package com.example.meowee;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CatHomeAdapter extends RecyclerView.Adapter<CatHomeAdapter.ViewHolder> {

    private ArrayList<Cat> cats;

    public CatHomeAdapter(ArrayList<Cat> cats) {
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
            holder.nameView.setText(cat.getName());
            holder.priceView.setText(String.format("%d đ", cat.getPrice()));
            holder.typesView.setText(String.format("Mèo %s, Mèo %s, Màu %s",
                    cat.getAgeLevel() == 1 ? "con" : "trưởng thành",
                    cat.isMale() ? "đực" : "cái",
                    cat.getColor()));
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
        private TextView nameView, priceView, typesView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageview_cat_home_itemview);
            nameView = (TextView) itemView.findViewById(R.id.textview_catname_home_itemview);
            priceView = (TextView) itemView.findViewById(R.id.textview_catprice_home_itemview);
            typesView = (TextView) itemView.findViewById(R.id.textview_cattype_home_itemview);
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
