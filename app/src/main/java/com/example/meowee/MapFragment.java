// MapFragment
package com.example.meowee;

import android.location.LocationRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";

    private LinearLayout listBranchLinearLayout;
    private ImageButton listBranchImageButton;
    protected boolean isBackButton;
    private ArrayList<Branch> listBranch;
    private ListView listView;
    private GoogleMap GGMAP;

    //location
    boolean isPermissionGranter;
    LocationRequest locationRequest;

    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference branchDatabaseref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // view
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // other parameters
        listBranchLinearLayout = view.findViewById(R.id.list_branch_LinearLayout);
        listBranchImageButton = view.findViewById(R.id.list_branch_ImageButton);
        ImageButton myLocationImageButton = view.findViewById(R.id.my_location_ImageButton);
        listView = view.findViewById(R.id.list_branch_ListView);
        isBackButton = true;

        firebaseDatabase = FirebaseDatabase.getInstance();
        branchDatabaseref = firebaseDatabase.getReference("Branches");
        listBranch = new ArrayList<Branch>();

        FusedLocationProviderClient fusedLocationClient;

        branchDatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Branch branch = dataSnapshot.getValue(Branch.class);
                    listBranch.add(branch);
                    addMarkers(branch);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: can not fetch data");
            }
        });

//        myLocationImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityResultLauncher<String[]> locationPermissionRequest =
//                        registerForActivityResult(new ActivityResultContracts
//                                .RequestMultiplePermissions(), result -> {
//                            Boolean fineLocationGranted = null;
//                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                                fineLocationGranted = result.getOrDefault(
//                                        Manifest.permission.ACCESS_FINE_LOCATION, false);
//                            }
//                            Boolean coarseLocationGranted = null;
//                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                                coarseLocationGranted = result.getOrDefault(
//                                        Manifest.permission.ACCESS_COARSE_LOCATION, false);
//                            }
//                            if ((fineLocationGranted != null && fineLocationGranted) ||
//                                    (coarseLocationGranted != null && coarseLocationGranted)) {
//                                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
//                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                    fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//                                        @Override
//                                        public void onSuccess(Location location) {
//                                            // Got last known location. In some rare situations this can be null.
//                                            if (location != null) {
//                                                LatLng newFocus = new LatLng(location.getLatitude(), location.getLongitude());
//                                                focusMap(newFocus, 17);
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//                            else {
//                                return;
//                            }
//                        });
//            }
//        });

        listBranchImageButton.setOnClickListener(v -> changeBackButton());

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.ggmap_fragment);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                GGMAP = googleMap;
                GGMAP.clear();
                LatLng defaultPos = new LatLng(10.764788611278338, 106.67918051020337);
                GGMAP.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        defaultPos, 15
                ));
            }
        });

        BranchListViewAdapter adapter = new BranchListViewAdapter(listBranch);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Branch branch = (Branch) adapter.getItem(i);
                LatLng newFocus = new LatLng(branch.getLatitude(), branch.getLongitude());

//                Log.d(TAG, "newFocus: Latitude - " + Double.toString(branch.latitude));

                focusMap(newFocus, 17);

                isBackButton = false;
                changeBackButton();

            }
        });

        return view;
    }

    private void focusMap(LatLng location, int zoom) {
        GGMAP.animateCamera(CameraUpdateFactory.newLatLngZoom(
                location, zoom
        ));
    }

    private void addMarkers(Branch markers) {
        GGMAP.addMarker(new MarkerOptions().position(new LatLng(markers.getLatitude(), markers.getLongitude())).title(markers.getName()));
    }

    private void changeBackButton() {
        if (isBackButton) {
            listBranchLinearLayout.setVisibility(View.VISIBLE);
            listBranchImageButton.setImageResource(R.drawable.ic_back_button);
            isBackButton = false;
        }
        else {
            listBranchLinearLayout.setVisibility(View.GONE);
            listBranchImageButton.setImageResource(R.drawable.ic_list_branch);
            isBackButton = true;
        }
    }
}
