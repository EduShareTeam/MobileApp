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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;

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
    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Like like) {


        holder.textViewTitle.setText(like.getTitle().toUpperCase());
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

        holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Like like = new Like();

                like(like, holder);

            }
        });



        checkIfExistLike(like.getIdPost(), mAuthProvider.getUid(), holder);

        // getUserInfo(post.getIdUser(), holder);

    }




    private void like(final Like like, final FavoriAdapter.ViewHolder holder) {
        mLikeprovider.getLikeByPostAndUser(like.getIdPost(), mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if (numberDocuments > 0) {
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.imageViewLike.setImageResource(R.drawable.heart);
                    mLikeprovider.delete(idLike);
                } else {
                    holder.imageViewLike.setImageResource(R.drawable.heartdolu);
                    mLikeprovider.create(like);
                }
            }
        });

    }

    private void checkIfExistLike(String idPost, String idUser, final FavoriAdapter.ViewHolder holder) {
        mLikeprovider.getLikeByPostAndUser(idPost, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if (numberDocuments > 0) {
                    holder.imageViewLike.setImageResource(R.drawable.heartdolu);
                } else {
                    holder.imageViewLike.setImageResource(R.drawable.heart);
                }
            }
        });

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
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
            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewCategory = view.findViewById(R.id.textViewCategory);
            //textViewUsername = view.findViewById(R.id.textViewUsernamePostCard);
            textViewLikes = view.findViewById(R.id.textViewLikes);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            imageViewLike = view.findViewById(R.id.imageViewLike);
            bar = view.findViewById(R.id.postLoading);
            viewHolder = view;
        }
    }

}
