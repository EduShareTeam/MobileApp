package com.fatihbaser.edusharedemo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.adapter.FavoriAdapter;
import com.fatihbaser.edusharedemo.databinding.ActivityFavoriteBinding;
import com.fatihbaser.edusharedemo.models.Like;
import com.fatihbaser.edusharedemo.models.User;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.LikesProvider;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class FavoriteActivity extends AppCompatActivity {

   private ActivityFavoriteBinding binding;
    LikesProvider mLikerProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;
    FavoriAdapter mPostsAdapter;
    FavoriAdapter favoriAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
        mLikerProvider = new LikesProvider();

        binding.recyclerViewLikes.setLayoutManager(new GridLayoutManager(FavoriteActivity.this, 2));

    }



    private void getAllPost() {
        Like like = new Like();
        User user = new User();
        Query query = mLikerProvider.getLikeByPostByUser(mAuthProvider.getUid());
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
        Query query = mLikerProvider.getLikeByPostByUser(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Like> options =
                new FirestoreRecyclerOptions.Builder<Like>()
                        .setQuery(query, Like.class)
                        .build();
        favoriAdapter = new FavoriAdapter(options,getApplicationContext(), binding.textViewNumberFilter);
        getAllPost();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
    }

}