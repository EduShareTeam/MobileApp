package com.fatihbaser.edusharedemo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fatihbaser.edusharedemo.databinding.ActivityCompleteProfileBinding;
import com.fatihbaser.edusharedemo.models.User;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CompleteProfileActivity extends AppCompatActivity {
    private ActivityCompleteProfileBinding binding;
    //FirebaseAuth mAuth;
    //FirebaseFirestore mFirestore;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompleteProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();
        //mAuth = FirebaseAuth.getInstance();
        //mFirestore = FirebaseFirestore.getInstance();

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private void register() {
        String username = binding.textInputUsername.getText().toString();
        String university = binding.textInputUniversity.getText().toString();
        String faculty = binding.textInputFaculty.getText().toString();
        String bio = binding.textInputBio.getText().toString();
        if (!username.isEmpty()&&!university.isEmpty()&&!faculty.isEmpty()&&!bio.isEmpty()) {
            updateUser(username,university,faculty,bio);
        }
        else {
            Toast.makeText(this, "Devam etmek için tüm alanları ekleyin", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUser(final String username,final String university,final String faculty,final String bio) {
        String id = mAuthProvider.getUid();
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setUniversity(university);
        user.setDepartment(faculty);
        user.setBio(bio);
        mDialog.show();
        mUsersProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()) {
                    Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(CompleteProfileActivity.this, "Kullanıcı veritabanında saklanamadı", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}