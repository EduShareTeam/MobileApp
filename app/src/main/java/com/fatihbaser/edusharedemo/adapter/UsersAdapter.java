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
import com.fatihbaser.edusharedemo.activities.UserProfileActivity;
import com.fatihbaser.edusharedemo.models.Like;
import com.fatihbaser.edusharedemo.models.Post;
import com.fatihbaser.edusharedemo.models.User;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.LikesProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class UsersAdapter extends FirestoreRecyclerAdapter<User, UsersAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;

    AuthProvider mAuthProvider;
    TextView mTextViewNumberFilter;
    ListenerRegistration mListener;

    public UsersAdapter(FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();

        mAuthProvider = new AuthProvider();
    }

    public UsersAdapter(FirestoreRecyclerOptions<User> options, Context context, TextView textView) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();

        mAuthProvider = new AuthProvider();
        mTextViewNumberFilter = textView;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final User user) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String userıd = document.getId();

        if (mTextViewNumberFilter != null) {
            int numberFilter = getSnapshots().size();
            mTextViewNumberFilter.setText(String.valueOf(numberFilter));
        }

        holder.textViewTitle.setText(user.getUsername().toUpperCase());
        holder.textViewCategory.setText(user.getUniversity());
        if (user.getImage() != null) {
            if (!user.getImage().isEmpty()) {
                Picasso.with(context).load(user.getImage()).into(holder.imageViewPost, new Callback() {
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
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("idUser", userıd);
                context.startActivity(intent);
            }
        });



        // getUserInfo(post.getIdUser(), holder);

    }







   /* private void getUserInfo(String idUser, final ViewHolder holder) {
        mUsersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        //holder.textViewUsername.setText("BY: " + username.toUpperCase());
                    }
                }
            }
        });

    }*/

    public ListenerRegistration getListener() {
        return mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //TODO: burda bazen uygulama patlıyor kayıt ve giriş yaparken kardeşimin telefonunda oldu android 6.0.1 alttaki iki satırı gösteriyor hata
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_user, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewCategory;
        TextView textViewUsername;
        TextView textViewLikes;
        ImageView imageViewPost;
        ImageView imageViewLike;
        View viewHolder;
        ProgressBar bar;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewUserUserCard);
            textViewCategory = view.findViewById(R.id.textViewUserCardUniversity);
            //textViewUsername = view.findViewById(R.id.textViewUsernamePostCard);
            textViewLikes = view.findViewById(R.id.textViewLikes);
            imageViewPost = view.findViewById(R.id.imageViewUserCard);
            imageViewLike = view.findViewById(R.id.imageViewLike);
            bar = view.findViewById(R.id.postLoading);
            viewHolder = view;
        }
    }

}
