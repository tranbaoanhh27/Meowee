package com.example.meowee;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WTF!MainActivity";

    public static FirebaseAuth firebaseAuth;
    public static FirebaseUser firebaseUser;
    public static FirebaseDatabase firebaseDatabase;
    public static User currentUser;
    public static DatabaseReference currentUserDatabaseRef, allCatsDatabaseRef;

    // UI Elements
    private ImageButton buttonHome, buttonMap, buttonCamera, buttonFavorite, buttonProfile;
    private Fragment fragmentMap, fragmentFavorites, fragmentProfile;
    private ProductListFragment fragmentHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentHome = new ProductListFragment();
        fragmentMap = new MapFragment();
        fragmentFavorites = new FavoritesFragment();
        fragmentProfile = new ProfileFragment();

        buttonHome = findViewById(R.id.bottomNavBarButtonHome);
        buttonMap = findViewById(R.id.bottomNavBarButtonMap);
        buttonCamera = findViewById(R.id.bottomNavBarButtonCamera);
        buttonFavorite = findViewById(R.id.bottomNavBarButtonFavorite);
        buttonProfile = findViewById(R.id.bottomNavBarButtonProfile);

        buttonHome.setOnClickListener(onBottomNavBarButtonClicked);
        buttonMap.setOnClickListener(onBottomNavBarButtonClicked);
        buttonFavorite.setOnClickListener(onBottomNavBarButtonClicked);
        buttonProfile.setOnClickListener(onBottomNavBarButtonClicked);

        setUpDefaultFragment();

    }

    private void setUpDefaultFragment() {
        resetSelectedBottomNavbarButton();
        buttonHome.setImageResource(R.drawable.home_selected);
        switchFragment(R.id.fragmentcontainerMainActivity, fragmentHome);
    }

    private final View.OnClickListener onBottomNavBarButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            resetSelectedBottomNavbarButton();
            int viewId = view.getId();
            if (viewId == R.id.bottomNavBarButtonHome) {
                Log.d(TAG, "Home button clicked!");
                buttonHome.setImageResource(R.drawable.home_selected);
                switchFragment(R.id.fragmentcontainerMainActivity, fragmentHome);
            } else if (viewId == R.id.bottomNavBarButtonMap) {
                Log.d(TAG, "Map button clicked!");
                buttonMap.setImageResource(R.drawable.map_selected);
                switchFragment(R.id.fragmentcontainerMainActivity, fragmentMap);
            } else if (viewId == R.id.bottomNavBarButtonFavorite) {
                Log.d(TAG, "Favorite button clicked!");
                buttonFavorite.setImageResource(R.drawable.favorite_selected);
                switchFragment(R.id.fragmentcontainerMainActivity, fragmentFavorites);
            } else if (viewId == R.id.bottomNavBarButtonProfile) {
                Log.d(TAG, "Profile button clicked!");
                buttonProfile.setImageResource(R.drawable.profile_selected);
                switchFragment(R.id.fragmentcontainerMainActivity, fragmentProfile);
            } else {
                Log.d(TAG, "Don't know which button clicked!");
            }
        }
    };

    private void resetSelectedBottomNavbarButton() {
        buttonHome.setImageResource(R.drawable.home_unselected);
        buttonMap.setImageResource(R.drawable.map_unselected);
        buttonFavorite.setImageResource(R.drawable.favorite_unselected);
        buttonProfile.setImageResource(R.drawable.profile_unselected);
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
            initDatabaseRef();
            if (currentUser != null) {
                Toast.makeText(MainActivity.this, "Chào mừng! " + currentUser.getFullName(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initDatabaseRef() {
        currentUserDatabaseRef = firebaseDatabase
                .getReference("Users")
                .child(firebaseUser.getUid());
        currentUserDatabaseRef.addValueEventListener(onCurrentUserDataChanged);
        allCatsDatabaseRef = firebaseDatabase
                .getReference("Cats");
        allCatsDatabaseRef.addValueEventListener(onCatsDataChanged);
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

    private final ValueEventListener onCatsDataChanged = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "All cats database changed.");
            Cat.allCats = new ArrayList<>();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                Cat.allCats.add(dataSnapshot.getValue(Cat.class));
            }
            fragmentHome.notifyAdapter();
            fragmentHome.progressBar.setVisibility(ProgressBar.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d(TAG, error.toString());
        }
    };

    private void updateUI() {
        TextView userNameGreeting = findViewById(R.id.textview_greeting);
        userNameGreeting.setText(String.format("Hi, %s", currentUser.getFullName()));
    }

    private void switchFragment(int fragmentContainerResourceId, Fragment fragmentObject) {
        getSupportFragmentManager().beginTransaction()
                .replace(fragmentContainerResourceId, fragmentObject)
                .addToBackStack(null)
                .commit();
    }
}