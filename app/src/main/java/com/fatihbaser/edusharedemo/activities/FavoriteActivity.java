package com.fatihbaser.edusharedemo.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.fatihbaser.edusharedemo.adapter.FavoriteAdapter;
import com.fatihbaser.edusharedemo.databinding.ActivityFavoriteBinding;
import com.fatihbaser.edusharedemo.models.Like;
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
    FavoriteAdapter mPostsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (binding.textViewNumberOfFavoriteItem != null){
            binding.textViewNumberOfFavoriteItem.setVisibility(View.VISIBLE);
            binding.textView2.setVisibility(View.VISIBLE);
        }

        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
        mLikerProvider = new LikesProvider();

        binding.recyclerViewLikes.setLayoutManager(new GridLayoutManager(FavoriteActivity.this, 2));
    }

    private void getAllPost() {
        Query query = mLikerProvider.getLikeByPostByUser(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Like> options =
                new FirestoreRecyclerOptions.Builder<Like>()
                        .setQuery(query, Like.class)
                        .build();
        mPostsAdapter = new FavoriteAdapter(options, FavoriteActivity.this, binding.textViewNumberOfFavoriteItem);
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
        mPostsAdapter = new FavoriteAdapter(options, FavoriteActivity.this, binding.textViewNumberOfFavoriteItem);
        getAllPost();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
    }

}