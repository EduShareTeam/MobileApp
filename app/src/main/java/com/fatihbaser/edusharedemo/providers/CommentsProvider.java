package com.fatihbaser.edusharedemo.providers;

import com.fatihbaser.edusharedemo.models.Comment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CommentsProvider {

    CollectionReference mCollection;

    public CommentsProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Comments");
    }

    public Task<Void> create(Comment comment) {
        return mCollection.document().set(comment);
    }

    public Query getCommentsByPost(String idPost) {
        return mCollection.whereEqualTo("idPost", idPost);
    }
}
