package com.example.meowee;

import static com.example.meowee.MainActivity.firebaseAuth;
import static com.example.meowee.MainActivity.firebaseUser;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class User {

    @Exclude
    private static final String TAG = "SOS!User";

    private String fullName, phoneNumber, address, email;
    private ArrayList<Integer> favoriteCatIds = new ArrayList<>();
    private Map<String, Integer> quantityByCatId = new HashMap<>();    // key: catId, value: quantity

    public User() {}

    public User(String fullName, String phoneNumber, String email) {
        this(fullName, phoneNumber, "", email);
    }

    public User(String fullName, String phoneNumber, String address, String email) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
    }

    public User(String fullName, String phoneNumber, String address, String email, ArrayList<Integer> favoriteCatIds) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.setFavoriteCatIds(favoriteCatIds);
    }

    public User(String fullName, String phoneNumber, String address, String email, ArrayList<Integer> favoriteCatIds, Map<String, Integer> quantityByCatId) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.setFavoriteCatIds(favoriteCatIds);
        this.setQuantityByCatId(quantityByCatId);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Integer> getFavoriteCatIds() {
        return favoriteCatIds;
    }

    public void setFavoriteCatIds(ArrayList<Integer> favoriteCatIds) {
        this.favoriteCatIds = new ArrayList<>();
        this.favoriteCatIds.addAll(favoriteCatIds);
    }

    public Map<String, Integer> getQuantityByCatId() {
        return quantityByCatId;
    }

    public void increaseQuantity(String catId, int delta) {
        Integer value = this.quantityByCatId.get(catId);
        if (value == null) value = 0;
        this.quantityByCatId.put(catId, value + delta);
    }

    public int decreaseQuantity(String catId, int delta) {
        Integer value = this.quantityByCatId.get(catId);
        if (value != null && value - delta >= 0) {
            if (value - delta > 0)
                this.quantityByCatId.put(catId, value - delta);
            else
                this.quantityByCatId.remove(catId);
            return value - delta;
        }
        return 0;
    }

    public void setQuantityByCatId(Map<String, Integer> quantityByCatId) {
        this.quantityByCatId = new HashMap<>();
        Set<String> catIdSet = quantityByCatId.keySet();
        for (String catId : catIdSet) {
            Integer quantity = quantityByCatId.get(catId);
            this.quantityByCatId.put(catId, quantity);
        }
    }

    @Exclude
    public Integer getQuantityOf(int catId) {
        @SuppressLint("DefaultLocale") Integer quantity = quantityByCatId.get(String.format("CatID%d", catId));
        return quantity != null ? quantity : 0;
    }

    @Exclude
    public Task<Void> saveToDatabase() {
        firebaseUser = firebaseAuth.getCurrentUser();
        return FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(firebaseUser.getUid())
                .setValue(this);
    }

    @Exclude
    public boolean likeCatWithId(Integer id) {
        return this.favoriteCatIds.contains(id);
    }

    @Exclude
    public boolean unlike(Integer id) {
        return this.favoriteCatIds.remove(id);
    }

    @Exclude
    public void like(Integer catId) {
        if (!this.favoriteCatIds.contains(catId))
            this.favoriteCatIds.add(catId);
    }

    @Exclude
    public boolean addedToCartCatWithId(Integer catId) {
        return getQuantityOf(catId) > 0;
    }

    @Exclude
    public boolean hasEmptyCart() {
        return quantityByCatId.isEmpty();
    }

    @Exclude
    public int getTotalCatPriceInCart() {
        int totalPrice = 0;
        Set<String> catIdSet = quantityByCatId.keySet();
        for (String catId : catIdSet) {
            Integer quantity = quantityByCatId.get(catId);
            if (quantity == null) quantity = 0;
            Cat cat = Cat.getCatById(catId);
            totalPrice += cat.getPrice() * quantity;
        }
        return totalPrice;
    }
}
