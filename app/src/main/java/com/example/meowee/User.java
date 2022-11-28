package com.example.meowee;

import static com.example.meowee.MainActivity.firebaseAuth;
import static com.example.meowee.MainActivity.firebaseUser;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

public class User {

    @Exclude
    private static final String TAG = "SOS!User";

    private String fullName, phoneNumber, address, email;

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

    @Exclude
    public Task<Void> saveToDatabase() {
        firebaseUser = firebaseAuth.getCurrentUser();
        return FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(firebaseUser.getUid())
                .setValue(this);
    }
}
