package com.example.meowee;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

public class Cat implements Serializable {

    @Exclude
    private final String TAG = "SOS!Cat";

    @Exclude
    public static ArrayList<Cat> allCats = new ArrayList<>();    // Realtime Synced with database

    // Attributes
    private String name, color, description, imageURL, downloadURL;
    private int price, ageLevel;
    private boolean isMale;

    // Methods
    public Cat() {}

    public Cat(String name, String color, String description, String imageURL, String downloadURL, int price, int ageLevel, boolean isMale) {
        this.name = name;
        this.color = color;
        this.description = description;
        this.imageURL = imageURL;
        this.price = price;
        this.ageLevel = ageLevel;
        this.isMale = isMale;
        this.downloadURL = downloadURL;
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
    public String getDownloadURL(){return downloadURL;}

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    public String toString() {
        return String.format("%s, %s, %d, %d, %s", name, color, ageLevel, price, isMale);
    }

    @Exclude
    public boolean hasNameSimilarTo(String text) {
        return this.name.toLowerCase().contains(text.toLowerCase());
    }

    @NonNull
    @Exclude
    public Cat deepCopy() {
        return new Cat(this.name, this.color, this.description, this.imageURL, this.downloadURL, this.price, this.ageLevel, this.isMale);
    }

    @Exclude
    public static Integer idOfCatWithName(String catName) {
        for (int i = 0; i < allCats.size(); i++)
            if (allCats.get(i).name.equals(catName))
                return i;
        return -1;
    }

    @SuppressLint("DefaultLocale")
    @Exclude
    public String getStringId() {
        int intID = idOfCatWithName(name);
        if (intID >= 0) return String.format("CatID%d", intID);
        else return "";
    }

    @Exclude
    public static Cat getCatById(String id) {
        // CatId format: "CatID__"
        int idNumber = Integer.parseInt(id.substring(5));
        return allCats.get(idNumber);
    }

    @Exclude
    public static Cat getCatByName(String catName) {
        for (Cat cat : allCats) {
            if (cat.name.equals(catName))
                return cat.deepCopy();
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    public String getDisplayablePrice() {
        try {
            return String.format("%, d đ", price);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return "N/A";
        }
    }

    public String getDisplayableTypes() {
        try {
            return String.format(
                    "Mèo %s, Mèo %s, Màu %s",
                    ageLevel == 1 ? "con" : "trưởng thành",
                    isMale ? "đực" : "cái",
                    color
            );
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return "N/A";
        }
    }

    @Exclude
    public boolean hasPriceInRange(Integer min, Integer max) {
        return (min == null || price >= min) && (max == null || price <= max);
    }
}
