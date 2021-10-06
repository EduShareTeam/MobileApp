package com.fatihbaser.edusharedemo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.fragments.ChatsFragment;
import com.fatihbaser.edusharedemo.fragments.FiltersFragment;
import com.fatihbaser.edusharedemo.fragments.HomeFragment;
import com.fatihbaser.edusharedemo.fragments.ProfileFragment;
import com.fatihbaser.edusharedemo.fragments.UsersFragment;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.TokenProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.fatihbaser.edusharedemo.utils.ViewedMessageHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();

        openFragment(new HomeFragment());
        createToken();
    }
    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, HomeActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, HomeActivity.this);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.itemHome) {
                        // FRAGMENT HOME
                        openFragment(new HomeFragment());
                    }else if (item.getItemId() == R.id.itemUsers) {
                        // FRAGMENT CHATS
                        openFragment(new UsersFragment());

                    }


                    else if (item.getItemId() == R.id.itemChats) {
                        // FRAGMENT CHATS
                        openFragment(new ChatsFragment());

                    } else if (item.getItemId() == R.id.itemFilters) {
                        // FRAGMENT FILTROS
                        openFragment(new FiltersFragment());

                    } else if (item.getItemId() == R.id.itemProfile) {
                        // FRAGMENT PROFILE
                        openFragment(new ProfileFragment());
                    }
                    return true;
                }
            };

    private void createToken() {
        mTokenProvider.create(mAuthProvider.getUid());
    }
}