package com.fatihbaser.edusharedemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.activities.PostDetailActivity;
import com.fatihbaser.edusharedemo.models.Like;
import com.fatihbaser.edusharedemo.models.Post;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.LikesProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    LikesProvider mLikesProvider;
    AuthProvider mAuthProvider;
    TextView mTextViewNumberFilter;
    ListenerRegistration mListener;

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mLikesProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
    }

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context, TextView textView) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mLikesProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
        mTextViewNumberFilter = textView;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();

        if (mTextViewNumberFilter != null) {
            int numberFilter = getSnapshots().size();
            mTextViewNumberFilter.setText(String.valueOf(numberFilter));
        }

        holder.textViewTitle.setText(post.getTitle());
        holder.textViewCategory.setText(post.getCategory());
        if (post.getImage1() != null) {
            if (!post.getImage1().isEmpty()) {
                Picasso.with(context).load(post.getImage1()).into(holder.imageViewPost, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.bar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.imageViewPost.setImageResource(R.drawable.ic_baseline_error_24);
                        holder.bar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
        holder.viewHolder.setOnClickListener(view -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("id", postId);
            context.startActivity(intent);
        });

        holder.imageViewLike.setOnClickListener(view -> {
            Like like = new Like();
            like.setIdUser(mAuthProvider.getUid());
            like.setIdPost(postId);
            like.setTitle(post.getTitle());
            like.setCategory(post.getCategory());
            like.setImage(post.getImage1());
            like.setTimestamp(new Date().getTime());
            like(like, holder);
        });

        // getUserInfo(post.getIdUser(), holder);
        getNumberLikesByPost(postId, holder);
        checkIfExistLike(postId, mAuthProvider.getUid(), holder);
    }


    private void getNumberLikesByPost(String idPost, final ViewHolder holder) {
        mListener = mLikesProvider.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    int numberLikes = queryDocumentSnapshots.size();
                    //holder.textViewLikes.setText(String.valueOf(numberLikes) + " BE??EN??");
                }
            }
        });
    }

    private void like(final Like like, final ViewHolder holder) {
        mLikesProvider.getLikeByPostAndUser(like.getIdPost(), mAuthProvider.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int numberDocuments = queryDocumentSnapshots.size();
            if (numberDocuments > 0) {
                String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                holder.imageViewLike.setImageResource(R.drawable.heart);
                mLikesProvider.delete(idLike);
            } else {
                holder.imageViewLike.setImageResource(R.drawable.heartdolu);
                mLikesProvider.create(like);
            }
        });

    }

    private void checkIfExistLike(String idPost, String idUser, final ViewHolder holder) {
        mLikesProvider.getLikeByPostAndUser(idPost, idUser).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int numberDocuments = queryDocumentSnapshots.size();
            if (numberDocuments > 0) {
                holder.imageViewLike.setImageResource(R.drawable.heartdolu);
            } else {
                holder.imageViewLike.setImageResource(R.drawable.heart);
            }
        });

    }

    public ListenerRegistration getListener() {
        return mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //TODO: burda bazen uygulama patl??yor kay??t ve giri?? yaparken karde??imin telefonunda oldu android 6.0.1 alttaki iki sat??r?? g??steriyor hata
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewCategory;
        TextView textViewLikes;
        ImageView imageViewPost;
        ImageView imageViewLike;
        View viewHolder;
        ProgressBar bar;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewCategory = view.findViewById(R.id.textViewCategory);
            textViewLikes = view.findViewById(R.id.textViewLikes);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            imageViewLike = view.findViewById(R.id.imageViewLike);
            bar = view.findViewById(R.id.postLoading);
            viewHolder = view;
        }
    }

}
