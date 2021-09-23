package com.fatihbaser.edusharedemo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.databinding.ActivityPostDetailBinding;
import com.fatihbaser.edusharedemo.databinding.ActivityUserProfileBinding;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {



    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    String mExtraIdUser;
    private ActivityUserProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        mExtraIdUser = getIntent().getStringExtra("idUser");

        binding.circleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        getUser();
        getPostNumber();
    }
    private void getPostNumber() {
        mPostProvider.getPostByUser(mExtraIdUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberPost = queryDocumentSnapshots.size();
                binding.textViewPostNumber.setText(String.valueOf(numberPost));
            }
        });
    }

    private void getUser() {
        mUsersProvider.getUser(mExtraIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                    if (documentSnapshot.contains("image")) {
                        String imageProfile = documentSnapshot.getString("image");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(UserProfileActivity.this).load(imageProfile).into(binding.circleImageProfile);
                            }
                        }
                    }

                }
            }
        });
    }


}