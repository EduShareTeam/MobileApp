package com.fatihbaser.edusharedemo.providers;

import com.fatihbaser.edusharedemo.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class PostProvider {

    CollectionReference mCollection;

    public PostProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Posts");
    }

    public Task<Void> save(Post post) {
        return mCollection.document().set(post);
    }

    public Query getAll() {
        //TODO: SIRALAMADA SIKINTI VAR  , VİDEO 52
        return mCollection.orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public Query getPostByUser(String id) {
        return mCollection.whereEqualTo("idUser", id);
    }


    public Task<DocumentSnapshot> getPostById(String id) {
        return mCollection.document(id).get();
    }

    public Task<Void> delete(String id) {
        return mCollection.document(id).delete();
    }
}
