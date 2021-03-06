package com.fatihbaser.edusharedemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.activities.PostDetailActivity;
import com.fatihbaser.edusharedemo.models.Like;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.LikesProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class FavoriteAdapter extends FirestoreRecyclerAdapter<Like, FavoriteAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    LikesProvider mLikeProvider;
    AuthProvider mAuthProvider;
    TextView mTextViewNumberOfFavoriteItem;
    ListenerRegistration mListener;

    public FavoriteAdapter(FirestoreRecyclerOptions<Like> options, Context context, TextView textView) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mLikeProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
        mTextViewNumberOfFavoriteItem = textView;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Like like) {
        //TODO: Favori kisminda veri olmadiginda usera bilgi verme
        if (mTextViewNumberOfFavoriteItem != null) {
            int numberFilter = getSnapshots().size();
            mTextViewNumberOfFavoriteItem.setText(String.valueOf(numberFilter));
        }

        holder.delete.setOnClickListener(view -> deletePost(like.getId()));
        holder.textViewTitle.setText(like.getTitle());
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
        }
        holder.viewHolder.setOnClickListener(view -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("id", like.getIdPost());
            context.startActivity(intent);
        });


    }

    public ListenerRegistration getListener() {
        return mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //TODO: burda bazen uygulama patl??yor kay??t ve giri?? yaparken karde??imin telefonunda oldu android 6.0.1 alttaki iki sat??r?? g??steriyor hata
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewfavorite, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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

            delete = view.findViewById(R.id.imageViewLike);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);

            bar = view.findViewById(R.id.postLoading);
            viewHolder = view;
        }
    }

    private void deletePost(String postId) {
        mLikeProvider.delete(postId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Yay??n ba??ar??yla kald??r??ld??", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "G??nderi silinemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
