package com.example.meowee;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

public class Cat implements Serializable {

    @Exclude
    public static ArrayList<Cat> allCats = new ArrayList<Cat>();

    // Attributes
    private String name, color, description;
    private int price, ageLevel;
    private boolean isMale;

    // Methods
    public Cat() {}

    public Cat(String name, String color, String description, int price, int ageLevel, boolean isMale) {
        this.name = name;
        this.color = color;
        this.description = description;
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

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    @Exclude
    public boolean hasNameSimilarTo(String text) {
        return this.name.toLowerCase().contains(text.toLowerCase());
    }

    @NonNull
    @Exclude
    public Cat clone() {
        return new Cat(this.name, this.color, this.description, this.price, this.ageLevel, this.isMale);
    }
}
