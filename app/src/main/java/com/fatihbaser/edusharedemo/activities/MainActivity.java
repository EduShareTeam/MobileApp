package com.fatihbaser.edusharedemo.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fatihbaser.edusharedemo.databinding.ActivityMainBinding;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    private GoogleSignInClient mGoogleSignInClient;
    AlertDialog mDialog;
    //private static final String TAG = "GoogleActivity";
    //private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Giriş Yapılıyor ...")
                .setCancelable(false).build();

//        // Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        binding.textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

//    private void checkUserExist(final String id) {
//        mUsersProvider.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    mDialog.dismiss();
//                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                    startActivity(intent);
//                }
//                else {
//                    String email = mAuthProvider.getEmail();
//                    User user = new User();
//                    user.setEmail(email);
//                    user.setId(id);
//                    mUsersProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                           mDialog.dismiss();
//                            if (task.isSuccessful()) {
//                                Intent intent = new Intent(MainActivity.this, CompleteProfileActivity.class);
//                                startActivity(intent);
//                            }
//                            else {
//                                Toast.makeText(MainActivity.this, "Kullanıcı bilgileri saklanamadı", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//            }
//        });
//    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthProvider.getUserSession() != null) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

//    private void signInWithGoogle() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                //firebaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//                Log.w("ERROR", "Google sign in failed", e);
//            }
//        }
//    }

//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        mDialog.show();
//        mAuthProvider.googleLogin(acct).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    String id = mAuthProvider.getUid();
//                    checkUserExist(id);
//                }
//                else {
//                 mDialog.dismiss();
//                    Log.w("ERROR", "signInWithCredential:failure", task.getException());
//                    Toast.makeText(MainActivity.this, "google ile giriş yapılamadı", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    private void login() {
        String email = binding.textInputEmail.getText().toString();
        String password = binding.textInputPassword.getText().toString();
        mDialog.show();
        mAuthProvider.login(email, password).addOnCompleteListener(task -> {
            mDialog.dismiss();
            if (task.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

            } else {
                Toast.makeText(MainActivity.this, "Girdiğiniz e-posta veya şifre doğru değil", Toast.LENGTH_LONG).show();
            }
        });
    }
}