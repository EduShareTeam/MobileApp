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

import java.util.Date;

import dmax.dialog.SpotsDialog;

public class CompleteProfileActivity extends AppCompatActivity {
    private ActivityCompleteProfileBinding binding;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompleteProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //Providers
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Kayıt  Yapılıyor ...")
                .setCancelable(false).build();

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
        user.setTimestamp(new Date().getTime());
        mDialog.show();
        mUsersProvider.updateProfile(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()) {
                    Intent intent = new Intent(CompleteProfileActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(CompleteProfileActivity.this, "Kullanıcı veritabanında saklanamadı", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}