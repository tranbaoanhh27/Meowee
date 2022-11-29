package com.example.meowee;

import static com.example.meowee.MainActivity.firebaseStorage;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Exclude;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;

public class Cat implements Serializable {

    @Exclude
    public static ArrayList<Cat> allCats = new ArrayList<Cat>();

    // Attributes
    private String name, color, description, imageURL;
    private int price, ageLevel;
    private boolean isMale;

    // Methods
    public Cat() {}

    public Cat(String name, String color, String description, String imageURL, int price, int ageLevel, boolean isMale) {
        this.name = name;
        this.color = color;
        this.description = description;
        this.imageURL = imageURL;
        this.price = price;
        this.ageLevel = ageLevel;
        this.isMale = isMale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAgeLevel() {
        return ageLevel;
    }

    public void setAgeLevel(int ageLevel) {
        this.ageLevel = ageLevel;
    }

    public boolean getIsMale() {
        return isMale;
    }

    public void setIsMale(boolean male) {
        isMale = male;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    public String toString() {
        return String.format("%s, %s, %d, %d, %s", name, color, ageLevel, price, String.valueOf(isMale));
    }

    @Exclude
    public boolean hasNameSimilarTo(String text) {
        return this.name.toLowerCase().contains(text.toLowerCase());
    }

    @NonNull
    @Exclude
    public Cat clone() {
        return new Cat(this.name, this.color, this.description, this.imageURL, this.price, this.ageLevel, this.isMale);
    }

    @Exclude
    public static Integer idOfCatWithName(String catName) {
        for (int i = 0; i < allCats.size(); i++)
            if (allCats.get(i).name.equals(catName))
                return i;
        return -1;
    }

    @Exclude
    public static Cat getCatById(String id) {
        // CatId format: "CatID__"
        int idNumber = Integer.parseInt(id.substring(5));
        return allCats.get(idNumber);
    }
}
