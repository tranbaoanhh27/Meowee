package com.example.meowee;

import static com.example.meowee.MainActivity.currentUser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProductListFragment extends Fragment {

    private static final String TAG = "WTF!ProductListFragment";

    // UI Elements
    private TextView usernameView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private final CatHomeAdapter adapter = new CatHomeAdapter();

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
        View view =inflater.inflate(R.layout.fragment_product_list, container, false);

        usernameView = (TextView) view.findViewById(R.id.textview_greeting);
        if (currentUser != null) {
            usernameView.setText(String.format("Hi, %s", currentUser.getFullName()));
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_home);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_home_fragment);
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapter() {
        progressBar.setVisibility(ProgressBar.GONE);
        adapter.notifyDataSetChanged();
    }
}