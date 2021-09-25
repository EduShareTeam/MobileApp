package com.fatihbaser.edusharedemo.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.adapter.PostsAdapter;
import com.fatihbaser.edusharedemo.databinding.ActivityFilterBinding;
import com.fatihbaser.edusharedemo.databinding.ActivityMainBinding;
import com.fatihbaser.edusharedemo.models.Post;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.fatihbaser.edusharedemo.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class FilterActivity extends AppCompatActivity {
    private ActivityFilterBinding binding;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    PostsAdapter mPostsAdapter;
    String mExtraCategory;
    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFilterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
       mToolbar = findViewById(R.id.toolbar);
        //TODO Alternative notification bar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Filtre");
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.recyclerViewFilter.setLayoutManager(new GridLayoutManager(FilterActivity.this, 2));

        mExtraCategory = getIntent().getStringExtra("category");

        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByCategoryAndTimestamp(mExtraCategory);
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostsAdapter = new PostsAdapter(options, FilterActivity.this, binding.textViewNumberFilter);
        binding.recyclerViewFilter.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
        ViewedMessageHelper.updateOnline(true, FilterActivity.this);

    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
        ViewedMessageHelper.updateOnline(true, FilterActivity.this);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}