package com.example.meowee;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FavoritesFragment extends Fragment implements UserDataChangedListener, CatsDataChangedListener {

    private final String TAG = "SOS!FavoritesFragment";

    private RecyclerView recyclerView;
    private CatAdapter adapter;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        adapter = new CatAdapter(view.getContext(), Cat.allCats);
        adapter.filterByFavorite();
        recyclerView = view.findViewById(R.id.recyclerview_favorites);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    public void notifyAdapter() {
        try {
            if (adapter != null) adapter.filterByFavorite();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void updateUserRelatedViews() {
        notifyAdapter();
    }

    @Override
    public void updateCatsRelatedViews() {
        notifyAdapter();
    }
}