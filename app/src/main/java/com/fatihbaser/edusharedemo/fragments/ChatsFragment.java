package com.fatihbaser.edusharedemo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.fatihbaser.edusharedemo.adapter.ChatsAdapter;
import com.fatihbaser.edusharedemo.databinding.FragmentChatsBinding;
import com.fatihbaser.edusharedemo.models.Chat;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.ChatsProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class ChatsFragment extends Fragment {
    private FragmentChatsBinding binding;
    ChatsAdapter mAdapter;
    //Provider
    ChatsProvider mChatsProvider;
    AuthProvider mAuthProvider;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mChatsProvider = new ChatsProvider();
        mAuthProvider = new AuthProvider();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerViewChats.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mChatsProvider.getAll(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Chat> options =
                new FirestoreRecyclerOptions.Builder<Chat>()
                        .setQuery(query, Chat.class)
                        .build();
        mAdapter = new ChatsAdapter(options, getContext(),binding.textViewNumberMessage);
        binding.recyclerViewChats.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter.getListener() != null) {
            mAdapter.getListener().remove();
        }
        if (mAdapter.getListenerLastMessage() != null) {
            mAdapter.getListenerLastMessage().remove();
        }
    }

}