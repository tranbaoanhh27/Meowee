package com.example.meowee;

import static com.example.meowee.MainActivity.currentSyncedUser;
import static com.example.meowee.Tools.showToast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ProductListFragment extends Fragment implements UserDataChangedListener, CatsDataChangedListener{

    private static final String TAG = "SOS!ProductListFragment";

    // Views
    private TextView usernameView;
    private ImageButton buttonGoToCart, buttonFilter;
    private SearchView searchView;
    private RecyclerView recyclerView;
    ProgressBar progressBar;
    private CatAdapter adapter;
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private ImageView micButton;

    FirebaseTranslator englishGermanTranslator;
    public ProductListFragment() {
        // Required empty public constructor
    }
    ActivityResultLauncher<Intent> someActivityResultLauncher  = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        // There are no request codes
                        Intent data = result.getData();
                        ArrayList<String> res = data.getStringArrayListExtra(
                                RecognizerIntent.EXTRA_RESULTS);

                        searchView.setQuery(
                                Objects.requireNonNull(res).get(0),true);
//                        downloadModal(Objects.requireNonNull(res).get(0));

                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Reload arguments from savedInstanceState
        }
        if(ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
        else{
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
        // Create an English-German translator:
//        FirebaseTranslatorOptions options =
//                new FirebaseTranslatorOptions.Builder()
//                        // below line we are specifying our source language.
//                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
//                        // in below line we are displaying our target language.
//                        .setTargetLanguage(FirebaseTranslateLanguage.VI)
//                        // after that we are building our options.
//                        .build();
//        // below line is to get instance
//        // for firebase natural language.
//        englishGermanTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options);



    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        usernameView = view.findViewById(R.id.textview_greeting);
        updateUsernameView();

        adapter = new CatAdapter(view.getContext(), Cat.allCats);
        recyclerView = view.findViewById(R.id.recyclerview_home);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar = view.findViewById(R.id.progressbar_home_fragment);
        updateProgressBar();

        searchView = view.findViewById(R.id.searchview_home);
        searchView.setOnQueryTextListener(searchQueryTextListener);

        buttonGoToCart = view.findViewById(R.id.imagebutton_home_mycart);
        updateCartButton();
        buttonGoToCart.setOnClickListener(v -> startMyCartActivity());

        buttonFilter = view.findViewById(R.id.imagebutton_filter);
        buttonFilter.setOnClickListener(v -> showToast(getActivity(), R.string.this_feature_is_being_developed));

        micButton = view.findViewById(R.id.img_mic);

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                try {
                    someActivityResultLauncher.launch(intent);

                } catch (Exception e) {
                    String appPackageName = "com.google.android.googlequicksearchbox";
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
        });

        return view;
    }

    public void updateProgressBar() {
        try {
            progressBar.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        } catch (Exception exception) {
            Log.d(TAG, exception.toString());
        }
    }

    private void startMyCartActivity() {
        startActivity(new Intent(ProductListFragment.this.getActivity(), MyCartActivity.class));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapter() {
        adapter.setCats(Cat.allCats);
        adapter.notifyDataSetChanged();
    }

    private final SearchView.OnQueryTextListener searchQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            adapter.filterByName(newText);
            return false;
        }
    };

    public void updateUsernameView() {
        try {
            if (usernameView != null)
                usernameView.setText(String.format("Hi, %s", currentSyncedUser.getFullName()));
        } catch (Exception exception) {
            Log.d(TAG, exception.toString());
        }
    }

    public void updateCartButton() {
        try {
            if (buttonGoToCart != null && currentSyncedUser != null)
                buttonGoToCart.setImageResource(
                        currentSyncedUser.hasEmptyCart() ?
                                R.drawable.cart_button_no_dot
                                :
                                R.drawable.cart_not_empty);
        } catch (Exception exception) {
            Log.d(TAG, exception.toString());
        }
    }

    public void updateViews() {
        notifyAdapter();
        updateProgressBar();
        updateUsernameView();
        updateCartButton();
    }

    @Override
    public void updateUserRelatedViews() {
        updateCartButton();
        updateUsernameView();
    }


    @Override
    public void updateCatsRelatedViews() {
        notifyAdapter();
        updateProgressBar();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
//        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this.requireActivity(),new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }


    private ActivityResultLauncher<String> requestPermissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Toast.makeText(this.requireContext(),"Permission Granted",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.requireContext(),"Permission Denied. Feature is unavailable! ",Toast.LENGTH_SHORT).show();
            }
        });


//    private void downloadModal(String input) {
//        // below line is use to download the modal which
//        // we will require to translate in german language
//        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();
//
//        // below line is use to download our modal.
//        englishGermanTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//
////                // this method is called when modal is downloaded successfully.
////                Toast.makeText(getActivity(), "Please wait language modal is being downloaded.", Toast.LENGTH_SHORT).show();
//
//                // calling method to translate our entered text.
//                translateLanguage(input);
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getActivity(), "Fail to download modal", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void translateLanguage(String input) {
//        englishGermanTranslator.translate(input).addOnSuccessListener(new OnSuccessListener<String>() {
//            @Override
//            public void onSuccess(String s) {
//                searchView.setQuery(s, true);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getActivity(), "Fail to translate", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

}