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
import com.fatihbaser.edusharedemo.adapter.SliderAdapter;
import com.fatihbaser.edusharedemo.databinding.ActivityPostDetailBinding;
import com.fatihbaser.edusharedemo.models.FCMBody;
import com.fatihbaser.edusharedemo.models.FCMResponse;
import com.fatihbaser.edusharedemo.models.SliderItem;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
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
    //Providers
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    TokenProvider mTokenProvider;
    NotificationProvider mNotificationProvider;
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


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Providers
        mPostProvider = new PostProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mLikesProvider = new LikesProvider();
        mTokenProvider = new TokenProvider();
        mNotificationProvider = new NotificationProvider();

        if (mAuthProvider.getUid().equals(mIdUser)) {
            binding.chat.setVisibility(View.INVISIBLE);
        }

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


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, PostDetailActivity.this);
    }

}
