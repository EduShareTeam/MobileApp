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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(new HomeFragment());
    }
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.itemHome) {
                        // FRAGMENT HOME
                        openFragment(new HomeFragment());
                    }
                    else if (item.getItemId() == R.id.itemChats) {
                        // FRAGMENT CHATS
                        openFragment(new ChatsFragment());

                    }
                    else if (item.getItemId() == R.id.itemFilters) {
                        // FRAGMENT FILTROS
                        openFragment(new FiltersFragment());

                    }
                    else if (item.getItemId() == R.id.itemProfile) {
                        // FRAGMENT PROFILE
                        openFragment(new ProfileFragment());
                    }
                    return true;
                }
            };
}