package com.fatihbaser.edusharedemo.activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.fragments.ChatsFragment;
import com.fatihbaser.edusharedemo.fragments.FiltersFragment;
import com.fatihbaser.edusharedemo.fragments.HomeFragment;
import com.fatihbaser.edusharedemo.fragments.ProfileFragment;
import com.fatihbaser.edusharedemo.fragments.UsersFragment;
import com.fatihbaser.edusharedemo.models.Chat;
import com.fatihbaser.edusharedemo.models.Message;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.ChatsProvider;
import com.fatihbaser.edusharedemo.providers.MessagesProvider;
import com.fatihbaser.edusharedemo.providers.TokenProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.fatihbaser.edusharedemo.utils.ViewedMessageHelper;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    ChatsProvider mChatsProvider;
    MessagesProvider messagesProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();
        mChatsProvider = new ChatsProvider();
        messagesProvider = new MessagesProvider();

        hasMessages();
        openFragment(new HomeFragment());
        createToken();
    }



    private void hasMessages() {
            messagesProvider.getMessagesBySender(mAuthProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    int size = value.size();
                    System.out.println(size);
                    if (size > 0) {
                        bottomNavigation.getOrCreateBadge(R.id.itemChats);
                    } else {

                    }
                }
            }
        });
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
            item -> {
                if (item.getItemId() == R.id.itemHome) {
                    // FRAGMENT HOME
                    openFragment(new HomeFragment());
                } else if (item.getItemId() == R.id.itemUsers) {
                    // FRAGMENT CHATS
                    openFragment(new UsersFragment());

                } else if (item.getItemId() == R.id.itemChats) {
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
            };

    private void createToken() {
        mTokenProvider.create(mAuthProvider.getUid());
    }
}