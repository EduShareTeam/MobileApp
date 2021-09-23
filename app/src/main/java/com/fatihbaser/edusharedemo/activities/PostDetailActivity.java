package com.fatihbaser.edusharedemo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.adapter.SliderAdapter;
import com.fatihbaser.edusharedemo.databinding.ActivityPostBinding;
import com.fatihbaser.edusharedemo.databinding.ActivityPostDetailBinding;
import com.fatihbaser.edusharedemo.models.SliderItem;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {
    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();
    PostProvider mPostProvider;

    UsersProvider mUsersProvider;
    String mExtraPostId;

    private ActivityPostDetailBinding binding;
    String mIdUser = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mSliderView = findViewById(R.id.imageSlider);
        mPostProvider = new PostProvider();

        mUsersProvider = new UsersProvider();
        mExtraPostId = getIntent().getStringExtra("id");

        getPost();



        binding.circleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShowProfile();
            }
        });

    }

    private void goToShowProfile() {
        if (!mIdUser.equals("")) {
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser", mIdUser);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "El id del usuario aun no se carga", Toast.LENGTH_SHORT).show();
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

                        if (category.equals("Elektronik ve mimarlık")) {
                            binding.imageViewCategory.setImageResource(R.drawable.icon_ps4);
                        }
                        else if (category.equals("Dil ve Edebiyat")) {
                            binding.imageViewCategory.setImageResource(R.drawable.icon_xbox);
                        }
                        else if (category.equals("Sanat")) {
                            binding.imageViewCategory.setImageResource(R.drawable.icon_pc);
                        }
                        else if (category.equals("Fen Bilimleri")) {
                            binding.imageViewCategory.setImageResource(R.drawable.icon_nintendo);
                        }
                    }
                    if (documentSnapshot.contains("idUser")) {
                        mIdUser = documentSnapshot.getString("idUser");
                        getUserInfo(mIdUser);
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
                        Picasso.with(PostDetailActivity.this).load(imageProfile).into(binding.circleImageProfile);
                    }
                }
            }
        });
    }
}
