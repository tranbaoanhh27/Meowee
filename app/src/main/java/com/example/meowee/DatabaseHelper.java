package com.example.meowee;

import static com.example.meowee.MainActivity.currentSyncedUser;
import static com.example.meowee.MainActivity.firebaseUser;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DatabaseHelper {

    private static final String TAG = "SOS!DatabaseHelper";

    private static FirebaseDatabase database;
    private static FirebaseStorage storage;
    private static DatabaseReference currentUserRef, catsRef;

    public static void initCurrentUserDatabaseReference(ValueEventListener listener) {
        try {
            if (database == null) database = FirebaseDatabase.getInstance();
            currentUserRef = database.getReference("Users").child(firebaseUser.getUid());
            currentUserRef.addValueEventListener(listener);
        } catch (Exception exception) {
            Log.d(TAG, exception.toString());
        }
    }

    public static void initCatsDatabaseReference(ValueEventListener listener) {
        try {
            if (database == null) database = FirebaseDatabase.getInstance();
            catsRef = database.getReference("Cats");
            catsRef.addValueEventListener(listener);
        } catch (Exception exception) {
            Log.d(TAG, exception.toString());
        }
    }

    public static void downloadFile(String fileUrl, OnSuccessListener<byte[]> onSuccessListener) {
        try {
            if (storage == null) storage = FirebaseStorage.getInstance();
            StorageReference ref = storage.getReferenceFromUrl(fileUrl);
            ref.getBytes(Long.MAX_VALUE).addOnSuccessListener(onSuccessListener);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public static void syncCurrentUserFavoriteCats(OnCompleteListener<Void> onCompleteListener) {
        try {
            currentUserRef.child("favoriteCatIds")
                    .setValue(currentSyncedUser.getFavoriteCatIds())
                    .addOnCompleteListener(onCompleteListener);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
    }

    public static void syncCurrentUserQuantitiesInCart(OnCompleteListener<Void> onCompleteListener) {
        try {
            currentUserRef.child("quantityByCatId")
                    .setValue(currentSyncedUser.getQuantityByCatId())
                    .addOnCompleteListener(onCompleteListener);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
    }
}
