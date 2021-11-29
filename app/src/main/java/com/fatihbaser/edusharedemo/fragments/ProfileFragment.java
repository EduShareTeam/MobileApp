package com.fatihbaser.edusharedemo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.activities.EditProfileActivity;
import com.fatihbaser.edusharedemo.activities.IntroActivity;
import com.fatihbaser.edusharedemo.activities.PostActivity;
import com.fatihbaser.edusharedemo.adapter.MyPostsAdapter;
import com.fatihbaser.edusharedemo.databinding.FragmentProfileBinding;
import com.fatihbaser.edusharedemo.models.Post;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.fatihbaser.edusharedemo.utils.SpacingItemDecorator;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    DatabaseReference databaseReference;

    //Spınner
    ValueEventListener valueEventListener;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> spinnerDataList;
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
        databaseReference = FirebaseDatabase.getInstance().getReference("universities");
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerViewMyPost.setLayoutManager(linearLayoutManager);
        SpacingItemDecorator spacingItemDecorator = new SpacingItemDecorator(50);
        binding.recyclerViewMyPost.addItemDecoration(spacingItemDecorator);
        //binding.recyclerViewMyPost.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        binding.linearLayoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthProvider.logout();
                Intent intent = new Intent(getContext(), IntroActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        binding.cardViewEditProfile.setOnClickListener(new View.OnClickListener() {
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

    private void retrieveData() {

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item :snapshot.getChildren()){
                    spinnerDataList.add(item.child("name").getValue().toString());
                }
                arrayAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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