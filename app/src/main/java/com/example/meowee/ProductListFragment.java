package com.example.meowee;

import static com.example.meowee.MainActivity.currentUser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

public class ProductListFragment extends Fragment {

    private static final String TAG = "SOS!ProductListFragment";

    // UI Elements
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

        usernameView = (TextView) view.findViewById(R.id.textview_greeting);
        if (currentUser != null) {
            usernameView.setText(String.format("Hi, %s", currentUser.getFullName()));
        }

        adapter = new CatAdapter(view.getContext(), Cat.allCats);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_home);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_home_fragment);
        if (adapter.getItemCount() == 0)
            progressBar.setVisibility(ProgressBar.VISIBLE);

        searchView = (SearchView) view.findViewById(R.id.searchview_home);
        searchView.setOnQueryTextListener(searchQueryTextListener);

        buttonGoToCart = (ImageButton) view.findViewById(R.id.imagebutton_home_mycart);
        updateCartButon();
        buttonGoToCart.setOnClickListener(v -> startMyCartActivity());

        return view;
    }

    private void startMyCartActivity() {
        startActivity(new Intent(ProductListFragment.this.getActivity(), MyCartActivity.class));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapter(ArrayList<Cat> cats) {
        adapter.setCats(cats);
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
        if (usernameView != null)
            usernameView.setText(String.format("Hi, %s", currentUser.getFullName()));
    }

    public void updateCartButon() {
        if (buttonGoToCart != null && currentUser != null)
            buttonGoToCart.setImageResource(
                    currentUser.hasEmptyCart() ? R.drawable.cart_button_no_dot : R.drawable.cart_not_empty);
    }
}