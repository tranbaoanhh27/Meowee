package com.example.meowee;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CatHomeAdapter extends RecyclerView.Adapter<CatHomeAdapter.ViewHolder> {
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
            Cat cat = Cat.allCats.get(position);
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
        return Cat.allCats.size();
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
}
