package com.example.meowee;

import static com.example.meowee.MainActivity.currentUserDatabaseRef;
import static com.example.meowee.MainActivity.firebaseDatabase;
import static com.example.meowee.MainActivity.firebaseUser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {


    private static final String TAG = "MainActivity";
    public static FirebaseAuth firebaseAuth;
    public static FirebaseUser firebaseUser;
    public static FirebaseDatabase firebaseDatabase;
    public static User currentUser;
    public static DatabaseReference currentUserDatabaseRef;

    private ImageView imgviewTakeCam, imgviewBack;
    private EditText edtPhone, edtName, edtEmail, edtAddress;
    private Button btnUpdateInfo;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
            currentUser = snapshot.getValue(User.class);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d(TAG, error.toString());
        }

        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {

                        imgviewTakeCam.setImageURI(uri);
                    }
                });
    };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            currentUser = MainActivity.currentUser;

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();

            firebaseUser = firebaseAuth.getCurrentUser();
            String firebaseUserID = firebaseAuth.getCurrentUser().getUid();

            DatabaseReference cur_user = firebaseDatabase.getReference("Users").child(firebaseUserID);

            View view = inflater.inflate(R.layout.fragment_profile, container, false);



            imgviewBack = view.findViewById(R.id.imgview_back);
            edtPhone = view.findViewById(R.id.edt_phone);
            edtEmail = view.findViewById(R.id.edt_email);
            edtAddress = view.findViewById(R.id.edt_address);
            edtName = view.findViewById(R.id.edt_name);

            if(currentUser!=null){
                edtAddress.setText(currentUser.getAddress());
                edtPhone.setText(currentUser.getPhoneNumber());
                edtEmail.setText(currentUser.getEmail());
                edtName.setText(currentUser.getFullName());
            }



            btnUpdateInfo = view.findViewById(R.id.btn_update_infor);


            btnUpdateInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (cur_user == null) {
//                        Intent signInSignUpIntent = new Intent(MainActivity.this, SignInSignUpActivity.class);
//                        startActivity(signInSignUpIntent);
                    }
                    else {
                        cur_user.child("address").setValue(edtAddress.getText().toString());
                        cur_user.child("email").setValue(edtEmail.getText().toString());
                        cur_user.child("fullName").setValue(edtName.getText().toString());
                        cur_user.child("phoneNumber").setValue(edtPhone.getText().toString());
                        Toast.makeText(getActivity(), "Đã cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            // Inflate the layout for this fragment
            return view;
        }

};