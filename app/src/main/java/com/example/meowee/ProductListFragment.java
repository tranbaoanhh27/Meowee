package com.example.meowee;

import static com.example.meowee.MainActivity.currentSyncedUser;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductListFragment extends Fragment implements UserDataChangedListener, CatsDataChangedListener {

    private static final String TAG = "SOS!ProductListFragment";

    // Views
    private TextView usernameView;
    private ImageButton buttonGoToCart, buttonFilter;
    private SearchView searchView;
    private RecyclerView recyclerView;
    ProgressBar progressBar;
    private CatAdapter adapter;
    private ScrollView filterContainer;
    private Button filterConfirmButton, filterResetButton;
    private Button buttonSexMale, buttonSexFemale;
    private Button buttonAgeLow, buttonAgeHigh;
    private EditText inputMinPrice, inputMaxPrice;
    private Button buttonColorBlack, buttonColorWhite, buttonColorYellow, buttonColorGray, buttonColorBrown;

    public ProductListFragment() {
        // Required empty public constructor
    }

    // Filters selection
    ArrayList<String> selectedSex = new ArrayList<>();
    ArrayList<Integer> selectedAge = new ArrayList<>();
    Integer minPrice = null, maxPrice = null;
    ArrayList<String> selectedColor = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

        filterContainer = view.findViewById(R.id.filters_container);

        filterConfirmButton = view.findViewById(R.id.button_filter_confirm);
        filterResetButton = view.findViewById(R.id.button_filter_reset);
        filterConfirmButton.setOnClickListener(v -> onConfirm());
        filterResetButton.setOnClickListener(v -> resetFilters());

        buttonSexMale = view.findViewById(R.id.button_select_filter_sex_male);
        buttonSexFemale = view.findViewById(R.id.button_select_filter_sex_female);
        buttonSexMale.setOnClickListener(filterSelectListener);
        buttonSexFemale.setOnClickListener(filterSelectListener);

        buttonAgeLow = view.findViewById(R.id.button_select_filter_age_low);
        buttonAgeHigh = view.findViewById(R.id.button_select_filter_age_high);
        buttonAgeLow.setOnClickListener(filterSelectListener);
        buttonAgeHigh.setOnClickListener(filterSelectListener);

        inputMinPrice = view.findViewById(R.id.edittextMinimumPrice);
        inputMaxPrice = view.findViewById(R.id.edittextMaximumPrice);

        buttonColorBlack = view.findViewById(R.id.button_select_filter_color_black);
        buttonColorWhite = view.findViewById(R.id.button_select_filter_color_white);
        buttonColorGray = view.findViewById(R.id.button_select_filter_color_gray);
        buttonColorYellow = view.findViewById(R.id.button_select_filter_color_yellow);
        buttonColorBrown = view.findViewById(R.id.button_select_filter_color_brown);
        buttonColorBrown.setOnClickListener(filterSelectListener);
        buttonColorYellow.setOnClickListener(filterSelectListener);
        buttonColorGray.setOnClickListener(filterSelectListener);
        buttonColorWhite.setOnClickListener(filterSelectListener);
        buttonColorBlack.setOnClickListener(filterSelectListener);

        buttonFilter = view.findViewById(R.id.imagebutton_filter);
        buttonFilter.setOnClickListener(v -> showFilterMenu());

        return view;
    }

    private void resetFilters() {
        selectedColor.clear();
        selectedAge.clear();
        selectedSex.clear();
        minPrice = null;
        maxPrice = null;
        updateAgeFilterButtons();
        updateColorFilterButtons();
        updateSexFilterButtons();
        inputMinPrice.setText("");
        inputMaxPrice.setText("");
    }

    private void onConfirm() {
        hideFilterMenu();
        String minPriceString = inputMinPrice.getText().toString();
        String maxPriceString = inputMaxPrice.getText().toString();

        if (!minPriceString.isEmpty())
            minPrice = Integer.parseInt(inputMinPrice.getText().toString());

        if (!maxPriceString.isEmpty())
            maxPrice = Integer.parseInt(inputMaxPrice.getText().toString());

        adapter.filterByOptions(selectedSex, selectedAge, minPrice, maxPrice, selectedColor);
    }

    View.OnClickListener filterSelectListener = v -> {
        if (v.getId() == R.id.button_select_filter_sex_male) {
            if (selectedSex.contains("male")) selectedSex.remove("male");
            else selectedSex.add("male");
            updateSexFilterButtons();
        } else if (v.getId() == R.id.button_select_filter_sex_female) {
            if (selectedSex.contains("female")) selectedSex.remove("female");
            else selectedSex.add("female");
            updateSexFilterButtons();
        } else if (v.getId() == R.id.button_select_filter_age_low) {
            if (selectedAge.contains(1)) selectedAge.remove(Integer.valueOf(1));
            else selectedAge.add(1);
            updateAgeFilterButtons();
        } else if (v.getId() == R.id.button_select_filter_age_high) {
            if (selectedAge.contains(2)) selectedAge.remove(Integer.valueOf(2));
            else selectedAge.add(2);
            updateAgeFilterButtons();
        } else if (v.getId() == R.id.button_select_filter_color_black) {
            if (selectedColor.contains("đen")) selectedColor.remove("đen");
            else selectedColor.add("đen");
            updateColorFilterButtons();
        } else if (v.getId() == R.id.button_select_filter_color_white) {
            if (selectedColor.contains("trắng")) selectedColor.remove("trắng");
            else selectedColor.add("trắng");
            updateColorFilterButtons();
        } else if (v.getId() == R.id.button_select_filter_color_gray) {
            if (selectedColor.contains("xám")) selectedColor.remove("xám");
            else selectedColor.add("xám");
            updateColorFilterButtons();
        } else if (v.getId() == R.id.button_select_filter_color_yellow) {
            if (selectedColor.contains("vàng")) selectedColor.remove("vàng");
            else selectedColor.add("vàng");
            updateColorFilterButtons();
        } else if (v.getId() == R.id.button_select_filter_color_brown) {
            if (selectedColor.contains("nâu")) selectedColor.remove("nâu");
            else selectedColor.add("nâu");
            updateColorFilterButtons();
        }
    };

    private void updateColorFilterButtons() {
        if (selectedColor.contains("đen")) {
            buttonColorBlack.setBackgroundColor(Color.BLACK);
            buttonColorBlack.setTextColor(Color.WHITE);
        } else {
            buttonColorBlack.setBackgroundColor(Color.WHITE);
            buttonColorBlack.setTextColor(Color.GRAY);
        }
        if (selectedColor.contains("trắng")) {
            buttonColorWhite.setBackgroundColor(Color.BLACK);
            buttonColorWhite.setTextColor(Color.WHITE);
        } else {
            buttonColorWhite.setBackgroundColor(Color.WHITE);
            buttonColorWhite.setTextColor(Color.GRAY);
        }
        if (selectedColor.contains("xám")) {
            buttonColorGray.setBackgroundColor(Color.BLACK);
            buttonColorGray.setTextColor(Color.WHITE);
        } else {
            buttonColorGray.setBackgroundColor(Color.WHITE);
            buttonColorGray.setTextColor(Color.GRAY);
        }
        if (selectedColor.contains("vàng")) {
            buttonColorYellow.setBackgroundColor(Color.BLACK);
            buttonColorYellow.setTextColor(Color.WHITE);
        } else {
            buttonColorYellow.setBackgroundColor(Color.WHITE);
            buttonColorYellow.setTextColor(Color.GRAY);
        }
        if (selectedColor.contains("nâu")) {
            buttonColorBrown.setBackgroundColor(Color.BLACK);
            buttonColorBrown.setTextColor(Color.WHITE);
        } else {
            buttonColorBrown.setBackgroundColor(Color.WHITE);
            buttonColorBrown.setTextColor(Color.GRAY);
        }
    }

    private void updateAgeFilterButtons() {
        if (selectedAge.contains(1)) {
            buttonAgeLow.setBackgroundColor(Color.BLACK);
            buttonAgeLow.setTextColor(Color.WHITE);
        } else {
            buttonAgeLow.setBackgroundColor(Color.WHITE);
            buttonAgeLow.setTextColor(Color.GRAY);
        }
        if (selectedAge.contains(2)) {
            buttonAgeHigh.setBackgroundColor(Color.BLACK);
            buttonAgeHigh.setTextColor(Color.WHITE);
        } else {
            buttonAgeHigh.setBackgroundColor(Color.WHITE);
            buttonAgeHigh.setTextColor(Color.GRAY);
        }
    }

    void updateSexFilterButtons() {
        if (selectedSex.contains("male")) {
            buttonSexMale.setBackgroundColor(Color.BLACK);
            buttonSexMale.setTextColor(Color.WHITE);
        } else {
            buttonSexMale.setBackgroundColor(Color.WHITE);
            buttonSexMale.setTextColor(Color.GRAY);
        }
        if (selectedSex.contains("female")) {
            buttonSexFemale.setBackgroundColor(Color.BLACK);
            buttonSexFemale.setTextColor(Color.WHITE);
        } else {
            buttonSexFemale.setBackgroundColor(Color.WHITE);
            buttonSexFemale.setTextColor(Color.GRAY);
        }
    }

    private void hideFilterMenu() {
        Log.d(TAG, "hide filter now");
        ObjectAnimator animator = ObjectAnimator.ofFloat(filterContainer, "translationX", 0);
        animator.setDuration(500);
        animator.start();
    }

    private void showFilterMenu() {
        Log.d(TAG, "filter button clicked");
        ObjectAnimator animator = ObjectAnimator.ofFloat(filterContainer, "translationX", -filterContainer.getWidth());
        animator.setDuration(500);
        animator.start();
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
                buttonGoToCart.setImageResource(currentSyncedUser.hasEmptyCart() ? R.drawable.cart_button_no_dot : R.drawable.cart_not_empty);
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
}