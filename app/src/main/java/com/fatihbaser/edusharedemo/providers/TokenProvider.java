package com.fatihbaser.edusharedemo.providers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fatihbaser.edusharedemo.models.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

public class TokenProvider {

    CollectionReference mCollection;

    public TokenProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Tokens");
    }

    public void create(final String idUser) {
        if (idUser == null) {
            return;
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // Get new FCM registration token
                //String token = task.getResult();
                Token token = new Token(task.getResult());
                mCollection.document(idUser).set(token);
            }
        });
    }

    public Task<DocumentSnapshot> getToken(String idUser) {
        return  mCollection.document(idUser).get();
    }
}
