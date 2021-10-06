package com.fatihbaser.edusharedemo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.adapter.CommentAdapter;
import com.fatihbaser.edusharedemo.adapter.SliderAdapter;
import com.fatihbaser.edusharedemo.databinding.ActivityPostDetailBinding;
import com.fatihbaser.edusharedemo.models.Comment;
import com.fatihbaser.edusharedemo.models.FCMBody;
import com.fatihbaser.edusharedemo.models.FCMResponse;
import com.fatihbaser.edusharedemo.models.SliderItem;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.CommentsProvider;
import com.fatihbaser.edusharedemo.providers.LikesProvider;
import com.fatihbaser.edusharedemo.providers.NotificationProvider;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.fatihbaser.edusharedemo.providers.TokenProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.fatihbaser.edusharedemo.utils.RelativeTime;
import com.fatihbaser.edusharedemo.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {
    private ActivityPostDetailBinding binding;
    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();
    CommentAdapter mAdapter;
    //Providers
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    TokenProvider mTokenProvider;
    NotificationProvider mNotificationProvider;
    CommentsProvider mCommentsProvider;
    UsersProvider mUsersProvider;
    LikesProvider mLikesProvider;

    String mExtraPostId;
    String mIdUser = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mSliderView = findViewById(R.id.imageSlider);
        mExtraPostId = getIntent().getStringExtra("id");

        //mExtraIdUser = getIntent().getStringExtra("idUser");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
        binding.recyclerViewComments.setLayoutManager(linearLayoutManager);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Providers
        mPostProvider = new PostProvider();
        mCommentsProvider = new CommentsProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mLikesProvider = new LikesProvider();
        mTokenProvider = new TokenProvider();
        mNotificationProvider = new NotificationProvider();

        binding.fabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogComment();
            }
        });

        binding.btnShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShowProfile();
            }
        });
        getPost();
        getNumberLikes();

        binding.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    goToChatActivity();




            }
        });

    }

    private void getNumberLikes() {
        mLikesProvider.getLikesByPost(mExtraPostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int numberLikes = queryDocumentSnapshots.size();
                if (numberLikes == 1) {
                    binding.textViewLikes.setText(numberLikes + " Beğendim");
                } else {
                    binding.textViewLikes.setText(numberLikes + " Beğenmedim");
                }
            }
        });

    }

    private void goToShowProfile() {
        if (!mIdUser.equals("")) {
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser", mIdUser);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Kullanıcı kimliği hala yüklenmedi", Toast.LENGTH_SHORT).show();
        }
    }


    private void goToChatActivity() {

        if (mAuthProvider.getUid().equals(mIdUser)) {
            binding.chat.setVisibility(View.INVISIBLE);
        }
        Intent intent = new Intent(PostDetailActivity.this, ChatActivity.class);
        intent.putExtra("idUser1", mAuthProvider.getUid());
        intent.putExtra("idUser2", mIdUser);
        startActivity(intent);
    }

    private void instanceSlider() {
        mSliderAdapter = new SliderAdapter(PostDetailActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }

    private void getPost() {
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    if (documentSnapshot.contains("image1")) {
                        String image1 = documentSnapshot.getString("image1");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image1);
                        mSliderItems.add(item);
                    }
                    if (documentSnapshot.contains("image2")) {
                        String image2 = documentSnapshot.getString("image2");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image2);
                        mSliderItems.add(item);
                    }
                    if (documentSnapshot.contains("title")) {
                        String title = documentSnapshot.getString("title");
                        binding.textViewTitle.setText(title.toUpperCase());
                    }
                    if (documentSnapshot.contains("description")) {
                        String description = documentSnapshot.getString("description");
                        binding.textViewDescription.setText(description);
                    }
                    if (documentSnapshot.contains("category")) {
                        String category = documentSnapshot.getString("category");
                        binding.textViewNameCategory.setText(category);

                        if (category.equals("Fen bilimleri")) {
                            binding.imageViewCategory.setImageResource(R.drawable.fen);
                        } else if (category.equals("Egitim bilimleri")) {
                            binding.imageViewCategory.setImageResource(R.drawable.egitim);
                        } else if (category.equals("Dil ve Edebiyat")) {
                            binding.imageViewCategory.setImageResource(R.drawable.literature);
                        } else if (category.equals("Yabanci diller")) {
                            binding.imageViewCategory.setImageResource(R.drawable.dil);
                        } else if (category.equals("Mimarlik")) {
                            binding.imageViewCategory.setImageResource(R.drawable.arc);
                        } else if (category.equals("Teknoloji ve Muhendislik")) {
                            binding.imageViewCategory.setImageResource(R.drawable.tek);
                        } else if (category.equals("Guzel Sanatlar")) {
                            binding.imageViewCategory.setImageResource(R.drawable.art);
                        }else if (category.equals("Iktisadi bilimler")) {
                            binding.imageViewCategory.setImageResource(R.drawable.economic);
                        } else if (category.equals("Spor bilimleri")) {
                            binding.imageViewCategory.setImageResource(R.drawable.sports);
                        }
                    }
                    if (documentSnapshot.contains("idUser")) {
                        mIdUser = documentSnapshot.getString("idUser");
                        getUserInfo(mIdUser);
                    }
                    if (documentSnapshot.contains("quality")) {
                        Long mQuality = documentSnapshot.getLong("quality");

                        binding.ratingBarProductQualityUpload.setRating(mQuality);
                    }
                    if (documentSnapshot.contains("timestamp")) {
                        long timestamp = documentSnapshot.getLong("timestamp");
                        String relativeTime = RelativeTime.getTimeAgo(timestamp, PostDetailActivity.this);
                        binding.textViewRelativeTime.setText(relativeTime);
                    }

                    instanceSlider();
                }
            }
        });
    }

    private void getUserInfo(String idUser) {
        mUsersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        binding.textViewUsername.setText(username);
                    }

                    if (documentSnapshot.contains("image")) {
                        String imageProfile = documentSnapshot.getString("image");
                        Picasso.with(PostDetailActivity.this).load(imageProfile).into(binding.circleImageProfile, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                binding.postLoading.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                binding.circleImageProfile.setImageResource(R.drawable.ic_baseline_error_24);
                                binding.postLoading.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }
            }
        });
    }

    private void showDialogComment() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetailActivity.this);
        alert.setTitle("¡YORUM!");
        alert.setMessage("Yorumunuzu girin");

        final EditText editText = new EditText(PostDetailActivity.this);
        editText.setHint("Metin");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(36, 0, 36, 36);
        editText.setLayoutParams(params);
        RelativeLayout container = new RelativeLayout(PostDetailActivity.this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        container.setLayoutParams(relativeParams);
        container.addView(editText);

        alert.setView(container);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = editText.getText().toString();
                if (!value.isEmpty()) {
                    createComment(value);
                } else {
                    Toast.makeText(PostDetailActivity.this, "Yoruma girmelisiniz", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alert.show();
    }

    private void createComment(final String value) {
        Comment comment = new Comment();
        comment.setComment(value);
        comment.setIdPost(mExtraPostId);
        comment.setIdUser(mAuthProvider.getUid());
        comment.setTimestamp(new Date().getTime());
        mCommentsProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sendNotification(value);
                    Toast.makeText(PostDetailActivity.this, "Yorum doğru oluşturuldu", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostDetailActivity.this, "Yorum oluşturulamadı", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = mCommentsProvider.getCommentsByPost(mExtraPostId);
        FirestoreRecyclerOptions<Comment> options =
                new FirestoreRecyclerOptions.Builder<Comment>()
                        .setQuery(query, Comment.class)
                        .build();
        mAdapter = new CommentAdapter(options, PostDetailActivity.this);
        binding.recyclerViewComments.setAdapter(mAdapter);
        mAdapter.startListening();
        ViewedMessageHelper.updateOnline(true, PostDetailActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, PostDetailActivity.this);
    }

    private void sendNotification(final String comment) {
        if (mIdUser == null) {
            return;
        }
        mTokenProvider.getToken(mIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "YENİ YORUM");
                        data.put("body", comment);
                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body() != null) {
                                    if (response.body().getSuccess() == 1) {
                                        Toast.makeText(PostDetailActivity.this, "Bildirim doğru gönderildi", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PostDetailActivity.this, "Bildirim gönderilemedi", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(PostDetailActivity.this, "Bildirim gönderilemedi", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, "Kullanıcı talepleri belirteci mevcut değil", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
