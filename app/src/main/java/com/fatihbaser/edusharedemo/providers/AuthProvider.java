package com.fatihbaser.edusharedemo.providers;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthProvider {

    private FirebaseAuth mAuth;

    public AuthProvider() {
        mAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> register(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> login(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

//    public Task<AuthResult> googleLogin(GoogleSignInAccount googleSignInAccount) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
//        return mAuth.signInWithCredential(credential);
//    }

    public String getEmail() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getEmail();
        }
        else {
            return null;
        }
    }

    public String getUid() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }
        else {
            return null;
        }
    }

    public FirebaseUser getUserSession() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser();
        }
        else {
            return null;
        }
    }

    public void logout() {
        if (mAuth != null) {
            mAuth.signOut();
        }
    }

    public void resetPassword(String email){
        mAuth.sendPasswordResetEmail(email);
    }

}

