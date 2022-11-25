package com.example.meowee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static FirebaseAuth firebaseAuth;
    public static FirebaseUser firebaseUser;
    public static FirebaseDatabase firebaseDatabase;
    public static User currentUser;
    public static DatabaseReference currentUserDatabaseRef;

    private TextView hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hello = findViewById(R.id.hello);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Intent signInSignUpIntent = new Intent(MainActivity.this, SignInSignUpActivity.class);
            startActivity(signInSignUpIntent);
        } else {
            initCurrentUser();
            if (currentUser != null) {
                Toast.makeText(MainActivity.this, "Chào mừng! " + currentUser.getFullName(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initCurrentUser() {
        currentUserDatabaseRef = firebaseDatabase
                .getReference("Users")
                .child(firebaseUser.getUid());
        currentUserDatabaseRef.addValueEventListener(onCurrentUserDataChanged);
    }

    private final ValueEventListener onCurrentUserDataChanged = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "Detected a data change of current user on the database.");
            currentUser = snapshot.getValue(User.class);
            updateUI();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d(TAG, error.toString());
        }
    };

    private void updateUI() {
        hello.setText(String.format("Hi, %s", currentUser.getFullName()));
    }
}