package com.fatihbaser.edusharedemo.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.activities.IntroActivity;
import com.fatihbaser.edusharedemo.activities.PostActivity;
import com.fatihbaser.edusharedemo.activities.UserProfileActivity;
import com.fatihbaser.edusharedemo.adapter.PostsAdapter;
import com.fatihbaser.edusharedemo.adapter.UsersAdapter;
import com.fatihbaser.edusharedemo.databinding.FragmentHomeBinding;
import com.fatihbaser.edusharedemo.databinding.FragmentUsersBinding;
import com.fatihbaser.edusharedemo.models.Post;
import com.fatihbaser.edusharedemo.models.User;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;


public class UsersFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {

    private FragmentUsersBinding binding;
    AuthProvider mAuthProvider;


    UsersProvider mUserProvider;
    UsersAdapter mUsersAdapter;
    UsersAdapter mUsersSearch;


    public UsersFragment() {
        // Required empty public constructor
    }

    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerViewHome.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        setHasOptionsMenu(true);
        mAuthProvider = new AuthProvider();
       mUserProvider =new UsersProvider();

        binding.searchBar.setOnSearchActionListener(this);
        binding.searchBar.inflateMenu(R.menu.main_menu);
        binding.searchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.itemLogout) {
                    logout();
                }
                return true;
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    private void searchByTitle(String title) {
        Query query = mUserProvider.getUserByTitle(title);
        FirestoreRecyclerOptions<User> options =
                new FirestoreRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();
        mUsersSearch = new UsersAdapter(options, getContext());
        mUsersSearch.notifyDataSetChanged();
        binding.recyclerViewHome.setAdapter(mUsersSearch);
        mUsersSearch.startListening();
    }

    private void getAllUser() {
        Query query = mUserProvider.getAll();
        FirestoreRecyclerOptions<User> options =
                new FirestoreRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();
        mUsersAdapter = new UsersAdapter(options, getContext());
        mUsersAdapter.notifyDataSetChanged();
        binding.recyclerViewHome.setAdapter(mUsersAdapter);
        mUsersAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        mUsersAdapter.stopListening();
        if (mUsersSearch != null) {
            mUsersSearch.stopListening();
        }
    }

    private void goTousers() {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        startActivity(intent);
    }

    private void logout() {
        mAuthProvider.logout();
        Intent intent = new Intent(getContext(), IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUsersAdapter.getListener() != null) {
            mUsersAdapter.getListener().remove();
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            getAllUser();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        searchByTitle(text.toString().toLowerCase());
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}
