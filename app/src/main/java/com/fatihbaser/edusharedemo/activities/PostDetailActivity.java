package com.fatihbaser.edusharedemo.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.adapter.SliderAdapter;
import com.fatihbaser.edusharedemo.databinding.ActivityPostDetailBinding;
import com.fatihbaser.edusharedemo.models.SliderItem;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.LikesProvider;
import com.fatihbaser.edusharedemo.providers.NotificationProvider;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.fatihbaser.edusharedemo.providers.TokenProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.fatihbaser.edusharedemo.utils.RelativeTime;
import com.fatihbaser.edusharedemo.utils.ViewedMessageHelper;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    String mExtraIdUser;
    String mExtraPostId;
    String mExtraTitle;
    String mIdUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mSliderView = findViewById(R.id.imageSlider);
        mExtraPostId = getIntent().getStringExtra("id");
        mExtraTitle = getIntent().getStringExtra("title");
        mExtraIdUser = getIntent().getStringExtra("idUser");

        //TODO: LinearLayout neden bos kontrol edilecek
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Providers
        mPostProvider = new PostProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mLikesProvider = new LikesProvider();
        mTokenProvider = new TokenProvider();
        mNotificationProvider = new NotificationProvider();

        getPost();
        binding.btnShowProfile.setOnClickListener(view1 -> goToShowProfile());

        // getNumberLikes();
        mExtraTitle = getIntent().getStringExtra("title");
        binding.chat.setOnClickListener(view12 -> goToChatActivity());

    }

    private void goToShowProfile() {
        if (!mIdUser.equals("")) {
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser", mIdUser);
            System.out.println(mIdUser+"idUser");
            startActivity(intent);
        } else {
            Toast.makeText(this, "Kullan??c?? kimli??i hala y??klenmedi", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToChatActivity() {


        if (!mAuthProvider.getUid().equals(mIdUser)) {

            Intent intent = new Intent(PostDetailActivity.this, ChatActivity.class);
            intent.putExtra("idUser1", mAuthProvider.getUid());
            intent.putExtra("idUser2", mIdUser);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "??zg??n??m kendine mesaj g??nderemezsin !!", Toast.LENGTH_LONG).show();
        }

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
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(documentSnapshot -> {
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
                    if (title != null) {
                        binding.textViewTitle.setText(title.toUpperCase());
                    }
                    mExtraTitle =title;

                }
                if (documentSnapshot.contains("description")) {
                    String description = documentSnapshot.getString("description");
                    binding.textViewDescription.setText(description);
                }
                if (documentSnapshot.contains("category")) {
                    String category = documentSnapshot.getString("category");
                    binding.textViewNameCategory.setText(category);


                }
                if (documentSnapshot.contains("idUser")) {
                    mIdUser = documentSnapshot.getString("idUser");
                    getUserInfo(mIdUser);

                    if (mAuthProvider.getUid().equals(mIdUser)) {
                        binding.chat.setVisibility(View.INVISIBLE);
                    }
                    System.out.println(mAuthProvider.getUid()+" title");
                    System.out.println(mIdUser+" titleaaa");
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
        });

    }

    private void getUserInfo(String idUser) {
        mUsersProvider.getUser(idUser).addOnSuccessListener(documentSnapshot -> {
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
