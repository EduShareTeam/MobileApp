package com.fatihbaser.edusharedemo.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.activities.EditProfileActivity;
import com.fatihbaser.edusharedemo.activities.IntroActivity;
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
import com.squareup.picasso.Callback;
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

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        binding.recyclerViewMyPost.setLayoutManager(linearLayoutManager);
        binding.recyclerViewMyPost.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        binding.linearLayoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthProvider.logout();
                Intent intent = new Intent(getContext(), IntroActivity.class);
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
        return view;
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


    private void getUser() {
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("email")) {
                        String email = documentSnapshot.getString("email");
                        binding.textViewEmail.setText(email);
                    }
                    if (documentSnapshot.contains("bio")) {
                        String bio = documentSnapshot.getString("bio");
                        binding.textViewBio.setText(bio);
                    }

                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        binding.textViewUsername.setText(username);
                    }

                    if (documentSnapshot.contains("department")) {
                        String department = documentSnapshot.getString("department");
                        binding.textViewDepartment.setText(department);
                    }

                    if (documentSnapshot.contains("university")) {
                        String university = documentSnapshot.getString("university");
                        binding.textViewUniversity.setText(university);
                    }

                    if (documentSnapshot.contains("image")) {
                        String imageProfile = documentSnapshot.getString("image");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(getContext()).load(imageProfile).into(binding.circleImageProfile, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                       binding.postLoading.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        binding.circleImageProfile.setImageResource(R.drawable.ic_baseline_error_24);
                                        binding.postLoading.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }
}