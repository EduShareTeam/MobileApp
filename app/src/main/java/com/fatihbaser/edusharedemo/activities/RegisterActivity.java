package com.fatihbaser.edusharedemo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fatihbaser.edusharedemo.databinding.ActivityRegisterBinding;
import com.fatihbaser.edusharedemo.models.User;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    //FirebaseAuth mAuth;
   // FirebaseFirestore mFirestore;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    AlertDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //mAuth = FirebaseAuth.getInstance();
       // mFirestore = FirebaseFirestore.getInstance();
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void register() {
        String username = binding.textInputUsername.getText().toString();
        String email = binding.textInputEmail.getText().toString();
        String university = binding.uniname.getText().toString();
        String department = binding.department.getText().toString();
        String bio = binding.bio.getText().toString();
        String password  = binding.textInputPassword.getText().toString();
        String confirmPassword = binding.textInputConfirmPassword.getText().toString();


        if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !university.isEmpty() && !department.isEmpty() && !bio.isEmpty() && !confirmPassword.isEmpty()) {
            if (isEmailValid(email)) {
                if (password.equals(confirmPassword)) {
                    if (password.length() >= 6) {
                        createUser(username, email, password, university, department, bio);
                    }
                    else {
                        Toast.makeText(this, "Şifre en az 6 karakter olmalıdır", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "Parolalar uyuşmuyor", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Tüm alanları girdiniz ancak e-posta geçerli değil", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Devam etmek için tüm alanları ekleyin", Toast.LENGTH_SHORT).show();
        }
    }

    private void createUser(final String username, final String email, final String password, final String university, final String department, final String bio) {
       // mDialog.show();
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuthProvider.getUid();


                    User user = new User();
                    user.setId(id);
                    user.setEmail(email);
                    user.setUsername(username);
                    user.setUniversity(university);
                    user.setDepartment(department);
                    user.setBio(bio);
                   /* Map<String, Object> map = new HashMap<>();
                    map.put("email", email);
                    map.put("isim ve soyisim", username);
                    map.put("Üniversite İsmi", university);
                    map.put("Fakulte", department);
                    map.put("Bio", bio);*/
                    mUsersProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           // mDialog.dismiss();
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, "Kullanıcı veritabanında saklanamadı", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                   // mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Kullanıcı kaydedilemedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}