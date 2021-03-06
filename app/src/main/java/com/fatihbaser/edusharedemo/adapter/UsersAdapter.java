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
import androidx.recyclerview.widget.RecyclerView;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.activities.UserProfileActivity;
import com.fatihbaser.edusharedemo.models.User;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final User user) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String userId = document.getId();

        if (mTextViewNumberFilter != null) {
            int numberFilter = getSnapshots().size();
            mTextViewNumberFilter.setText(String.valueOf(numberFilter));
        }

        holder.textViewUsername.setText(user.getUsername());
        holder.textViewUniversity.setText(user.getUniversity());
        holder.textViewDepartment.setText(user.getDepartment());
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
        holder.viewHolder.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("idUser", userId);
            context.startActivity(intent);
        });

    }

    public ListenerRegistration getListener() {
        return mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_user, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewUniversity;
        TextView textViewDepartment;
        TextView textViewLikes;
        ImageView imageViewPost;
        ImageView imageViewLike;
        View viewHolder;
        ProgressBar bar;

        public ViewHolder(View view) {
            super(view);
            textViewUsername = view.findViewById(R.id.textViewUserUserCard);
            textViewUniversity = view.findViewById(R.id.textViewUserCardUniversity);
            textViewDepartment = view.findViewById(R.id.textViewUserCardDepartment);
            textViewLikes = view.findViewById(R.id.textViewLikes);
            imageViewPost = view.findViewById(R.id.imageViewUserCard);
            imageViewLike = view.findViewById(R.id.imageViewLike);
            bar = view.findViewById(R.id.postLoading);
            viewHolder = view;
        }
    }

}
