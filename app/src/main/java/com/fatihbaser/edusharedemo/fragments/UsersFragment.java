package com.fatihbaser.edusharedemo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.activities.IntroActivity;
import com.fatihbaser.edusharedemo.adapter.UsersAdapter;
import com.fatihbaser.edusharedemo.databinding.FragmentUsersBinding;
import com.fatihbaser.edusharedemo.models.User;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;


public class UsersFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {

    private FragmentUsersBinding binding;

    AuthProvider mAuthProvider;
    UsersProvider mUserProvider;
    UsersAdapter mUsersAdapter;
    UsersAdapter mUsersAdapterSearch;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setHasOptionsMenu(true);

        binding.recyclerViewUsersFragment.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        setHasOptionsMenu(true);
        mAuthProvider = new AuthProvider();
        mUserProvider = new UsersProvider();

        binding.searchBarUser.setOnSearchActionListener(this);


        binding.searchBarUser.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.itemFav) {

                }
                return true;
            }
        });

        return view;
    }

    private void searchByTitle(String title) {
        Query query = mUserProvider.getUserByUsername(title);
        FirestoreRecyclerOptions<User> options =
                new FirestoreRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();
        mUsersAdapterSearch = new UsersAdapter(options, getContext());
        mUsersAdapterSearch.notifyDataSetChanged();
        binding.recyclerViewUsersFragment.setAdapter(mUsersAdapterSearch);
        mUsersAdapterSearch.startListening();
    }

    private void getAllUser() {
        Query query = mUserProvider.getAll();
        FirestoreRecyclerOptions<User> options =
                new FirestoreRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();
        mUsersAdapter = new UsersAdapter(options, getContext());
        mUsersAdapter.notifyDataSetChanged();
        binding.recyclerViewUsersFragment.setAdapter(mUsersAdapter);
        mUsersAdapter.startListening();
    }

    private void getAllDepartment(String department){
        Query query = mUserProvider.getUserByDepartment(department);
        FirestoreRecyclerOptions<User> options =
                new FirestoreRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();
        mUsersAdapterSearch = new UsersAdapter(options, getContext());
        mUsersAdapterSearch.notifyDataSetChanged();
        binding.recyclerViewUsersFragment.setAdapter(mUsersAdapterSearch);
        mUsersAdapterSearch.startListening();
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
        if (mUsersAdapterSearch != null) {
            mUsersAdapterSearch.stopListening();
        }
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
        getAllDepartment(text.toString());
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}
