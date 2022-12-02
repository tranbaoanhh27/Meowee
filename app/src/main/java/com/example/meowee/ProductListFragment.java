package com.example.meowee;

import static com.example.meowee.MainActivity.currentSyncedUser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProductListFragment extends Fragment implements UserDataChangedListener, CatsDataChangedListener{

    private static final String TAG = "SOS!ProductListFragment";

    // Views
    private TextView usernameView;
    private ImageButton buttonGoToCart;
    private SearchView searchView;
    private RecyclerView recyclerView;
    ProgressBar progressBar;
    private CatAdapter adapter;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Reload arguments from savedInstanceState
        }
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
}