package com.fatihbaser.edusharedemo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.adapter.FavoriAdapter;
import com.fatihbaser.edusharedemo.adapter.PostsAdapter;
import com.fatihbaser.edusharedemo.databinding.ActivityCompleteProfileBinding;
import com.fatihbaser.edusharedemo.databinding.ActivityFavoriBinding;
import com.fatihbaser.edusharedemo.models.Like;
import com.fatihbaser.edusharedemo.models.Post;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.LikesProvider;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.fatihbaser.edusharedemo.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class FavoriteActivity extends AppCompatActivity {

    private ActivityFavoriBinding binding;
    LikesProvider mLikerProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;
    FavoriAdapter mPostsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
        mLikerProvider = new LikesProvider();

        binding.recyclerViewLikes.setLayoutManager(new GridLayoutManager(FavoriteActivity.this, 2));

    }



    private void getAllPost() {
        Query query = mLikerProvider.getAll();
        FirestoreRecyclerOptions<Like> options =
                new FirestoreRecyclerOptions.Builder<Like>()
                        .setQuery(query, Like.class)
                        .build();
        mPostsAdapter = new FavoriAdapter(options, FavoriteActivity.this);
        mPostsAdapter.notifyDataSetChanged();
        binding.recyclerViewLikes.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllPost();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
    }

}