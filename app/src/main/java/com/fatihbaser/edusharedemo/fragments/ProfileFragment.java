package com.fatihbaser.edusharedemo.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.activities.EditProfileActivity;
import com.fatihbaser.edusharedemo.activities.MainActivity;
import com.fatihbaser.edusharedemo.adapter.MyPostsAdapter;
import com.fatihbaser.edusharedemo.databinding.FragmentProfileBinding;
import com.fatihbaser.edusharedemo.models.Post;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    MyPostsAdapter mAdapter;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerViewMyPost.setLayoutManager(linearLayoutManager);

        binding.linearLayoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthProvider.logout();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        binding.imageViewEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();
            }
        });

        getUser();
        getPostNumber();
        //checkIfExistPost();

        return view;
    }

    private void checkIfExistPost() {
        mPostProvider.getPostByUser(mAuthProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int numberPost = queryDocumentSnapshots.size();
                if (numberPost > 0) {
                    binding.textViewPostExist.setText("Yayınlar");
                    binding.textViewPostExist.setTextColor(Color.RED);
                }
                else {
                    binding.textViewPostExist.setText("Yayın yok");
                    binding.textViewPostExist.setTextColor(Color.GRAY);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByUser(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mAdapter = new MyPostsAdapter(options, getContext());
        binding.recyclerViewMyPost.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void getPostNumber() {
        mPostProvider.getPostByUser(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberPost = queryDocumentSnapshots.size();
                binding.textViewPostNumber.setText(String.valueOf(numberPost));
            }
        });
    }

    private void getUser() {
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("email")) {
                        String email = documentSnapshot.getString("email");
                        binding.textViewEmail.setText(email);
                    }

                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        binding.textViewUsername.setText(username);
                    }

                    if (documentSnapshot.contains("department")) {
                        String username = documentSnapshot.getString("department");
                        binding.textViewDepartment.setText(username);
                    }

                    if (documentSnapshot.contains("university")) {
                        String username = documentSnapshot.getString("university");
                        binding.textViewUniversity.setText(username);
                    }

                    if (documentSnapshot.contains("image")) {
                        String imageProfile = documentSnapshot.getString("image");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(getContext()).load(imageProfile).into(binding.circleImageProfile);
                            }
                        }
                    }
                }
            }
        });
    }
}