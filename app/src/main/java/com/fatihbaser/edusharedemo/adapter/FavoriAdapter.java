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
import com.fatihbaser.edusharedemo.activities.PostDetailActivity;
import com.fatihbaser.edusharedemo.activities.UserProfileActivity;
import com.fatihbaser.edusharedemo.models.Like;
import com.fatihbaser.edusharedemo.models.User;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.LikesProvider;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Locale;

public class FavoriAdapter extends FirestoreRecyclerAdapter<Like, FavoriAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    LikesProvider mLikeprovider;
    AuthProvider mAuthProvider;
    TextView mTextViewNumberFilter;
    ListenerRegistration mListener;

    public FavoriAdapter(FirestoreRecyclerOptions<Like> options, Context context) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mLikeprovider=new LikesProvider();
        mAuthProvider = new AuthProvider();
    }

    public FavoriAdapter(FirestoreRecyclerOptions<Like> options, Context context, TextView textView) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mLikeprovider=new LikesProvider();
        mAuthProvider = new AuthProvider();
        mTextViewNumberFilter = textView;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Like like) {
       //TODO: Favori kisminda veri olmadiginda usera bilgi verme
        if (mTextViewNumberFilter != null) {
            int numberFilter = getSnapshots().size();
            mTextViewNumberFilter.setText(String.valueOf(numberFilter));
        }

        holder.delete.setOnClickListener(view -> deletePost(like.getId()));
        holder.textViewTitle.setText(like.getTitle().toUpperCase(Locale.ROOT));
        holder.textViewCategory.setText(like.getCategory());

        if (like.getImage() != null) {
            if (!like.getImage().isEmpty()) {
                Picasso.with(context).load(like.getImage()).into(holder.imageViewPost, new Callback() {
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
        }     holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("id", like.getIdPost());
                context.startActivity(intent);
            }
        });


    }

    public ListenerRegistration getListener() {
        return mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //TODO: burda bazen uygulama patlıyor kayıt ve giriş yaparken kardeşimin telefonunda oldu android 6.0.1 alttaki iki satırı gösteriyor hata
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewfavorite, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewCategory;

        ImageView imageViewPost;

        ImageView delete;
        View viewHolder;
        ProgressBar bar;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewCategory = view.findViewById(R.id.textViewCategory);

            delete = view.findViewById(R.id.imageViewLikedelete);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);

            bar = view.findViewById(R.id.postLoading);
            viewHolder = view;
        }
    }


   /* private void showConfirmDelete(final String postId) {
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
    }*/

    private void deletePost(String postId) {
        mLikeprovider.delete(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
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

}
