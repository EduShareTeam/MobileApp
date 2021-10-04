package com.fatihbaser.edusharedemo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.activities.EditPostActivity;
import com.fatihbaser.edusharedemo.activities.PostDetailActivity;
import com.fatihbaser.edusharedemo.models.Post;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.LikesProvider;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.fatihbaser.edusharedemo.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsAdapter extends FirestoreRecyclerAdapter<Post, MyPostsAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    LikesProvider mLikesProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    public MyPostsAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mLikesProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();
        String relativeTime = RelativeTime.getTimeAgo(post.getTimestamp(), context);
        holder.textViewRelativeTime.setText(relativeTime);
        holder.textViewTitle.setText(post.getTitle().toUpperCase());
        if (post.getIdUser().equals(mAuthProvider.getUid())) {
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewDelete.setVisibility(View.GONE);
        }

        if (post.getIdUser().equals(mAuthProvider.getUid())) {
            holder.imageViewEdit.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewEdit.setVisibility(View.GONE);
        }

        if (post.getImage1() != null) {
            if (!post.getImage1().isEmpty()) {
                Picasso.with(context).load(post.getImage1()).into(holder.circleImagePost, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.bar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.circleImagePost.setImageResource(R.drawable.ic_baseline_error_24);
                        holder.bar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("id", postId);
                context.startActivity(intent);
            }
        });

        holder.imageViewDelete.setOnClickListener(view -> showConfirmDelete(postId));
        holder.imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(context, EditPostActivity.class);
                intent.putExtra("id",postId);
                context.startActivity(intent);
            }
        });
    }

    private void showConfirmDelete(final String postId) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Gönderiyi sil")
                .setMessage("Bu eylemi gerçekleştireceğinizden emin misiniz?")
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletePost(postId);
                    }
                })
                .setNegativeButton("Hayir", null)
                .show();
    }

    private void deletePost(String postId) {
        mPostProvider.delete(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Yayın başarıyla kaldırıldı", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Gönderi silinemedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_my_post, parent, false);
        return new ViewHolder(view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewRelativeTime;
        CircleImageView circleImagePost;
        ImageView imageViewDelete;
        ImageView imageViewEdit;
        View viewHolder;

        ProgressBar bar;
        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitleMyPost);
            textViewRelativeTime = view.findViewById(R.id.textViewRelativeTimeMyPost);
            circleImagePost = view.findViewById(R.id.circleImageMyPost);
            imageViewDelete = view.findViewById(R.id.imageViewDeleteMyPost);
            imageViewEdit = view.findViewById(R.id.imageViewEditMyPost);
            bar = view.findViewById(R.id.postLoading);
            viewHolder = view;
        }
    }


}
