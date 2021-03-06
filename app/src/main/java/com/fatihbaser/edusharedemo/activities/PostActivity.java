package com.fatihbaser.edusharedemo.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.databinding.ActivityPostBinding;
import com.fatihbaser.edusharedemo.models.Post;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.ImageProvider;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.fatihbaser.edusharedemo.utils.FileUtil;
import com.fatihbaser.edusharedemo.utils.ViewedMessageHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {
    private ActivityPostBinding binding;
    //Providers
    ImageProvider mImageProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;

    File mImageFile;
    File mImageFile2;

    String mCategory = "";
    String mTitle = "";
    float mQuality = 0;
    String mSpinnerCategories = "";
    String mDescription = "";
    AlertDialog mDialog;
    AlertDialog.Builder mBuilderSelector;
    CharSequence[] options;

    private final int GALLERY_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE_2 = 2;
    private final int PHOTO_REQUEST_CODE = 3;
    private final int PHOTO_REQUEST_CODE_2 = 4;

    // FOTO 1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;

    // FOTO 2
    String mAbsolutePhotoPath2;
    String mPhotoPath2;
    File mPhotoFile2;

    //Sp??nner
    ValueEventListener valueEventListener;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> spinnerDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new AuthProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Biraz bekle ")
                .setCancelable(false).build();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("L??tfen bir se??enek se??iniz");
        options = new CharSequence[]{"Galeriden resim se??", "Fotograf ??ek"};

        binding.circleImageBack.setOnClickListener(view1 -> finish());

        binding.btnPost.setOnClickListener(view12 -> clickPost());

        binding.imageViewPost1.setOnClickListener(view13 -> selectOptionImage(1));

        binding.imageViewPost2.setOnClickListener(view14 -> selectOptionImage(2));

        spinnerDataList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(PostActivity.this,
                android.R.layout.simple_spinner_dropdown_item, spinnerDataList);
        binding.spinnerProductCategory.setAdapter(arrayAdapter);
        retrieveData();
    }

    private void selectOptionImage(final int numberImage) {

        mBuilderSelector.setItems(options, (dialogInterface, i) -> {
            if (i == 0) {
                if (numberImage == 1) {
                    openGallery(GALLERY_REQUEST_CODE);
                } else if (numberImage == 2) {
                    openGallery(GALLERY_REQUEST_CODE_2);
                }
            } else if (i == 1) {
                if (numberImage == 1) {
                    takePhoto(PHOTO_REQUEST_CODE);
                } else if (numberImage == 2) {
                    takePhoto(PHOTO_REQUEST_CODE_2);
                }
            }
        });
        mBuilderSelector.show();
    }

    private void takePhoto(int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createPhotoFile(requestCode);
            } catch (Exception e) {
                Toast.makeText(this, "Dosyada bir hata olu??tu " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this, "com.fatihbaser.edusharedemo", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, requestCode);
            }
        }
    }

    private File createPhotoFile(int requestCode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir);
        if (requestCode == PHOTO_REQUEST_CODE) {
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        } else if (requestCode == PHOTO_REQUEST_CODE_2) {
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath2 = photoFile.getAbsolutePath();
        }
        return photoFile;
    }

    private void clickPost() {
        mTitle = Objects.requireNonNull(binding.textInputVideoGame.getText()).toString();
        mDescription = Objects.requireNonNull(binding.textInputDescription.getText()).toString();
        mQuality = binding.ratingBarProductQualityUpload.getRating();
        mSpinnerCategories = binding.spinnerProductCategory.getSelectedItem().toString();
        if (!mTitle.isEmpty() && !mDescription.isEmpty()) {
            // GALER??DEN ??K?? RES??M SE????YORUM
            if (mImageFile != null && mImageFile2 != null) {
                saveImage(mImageFile, mImageFile2);
            }
            // KAMERANIN ??K?? RES??M??N?? ??EK??YORUM
            else if (mPhotoFile != null && mPhotoFile2 != null) {
                saveImage(mPhotoFile, mPhotoFile2);
            } else if (mImageFile != null && mPhotoFile2 != null) {
                saveImage(mImageFile, mPhotoFile2);
            } else if (mPhotoFile != null && mImageFile2 != null) {
                saveImage(mPhotoFile, mImageFile2);
            } else {
                Toast.makeText(this, "Bir resim se??melisiniz", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "L??tfen bo?? alanlar?? doldurunuz", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage(File imageFile1, final File imageFile2) {
        mDialog.show();
        mImageProvider.save(PostActivity.this, imageFile1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    final String url = uri.toString();

                    mImageProvider.save(PostActivity.this, imageFile2).addOnCompleteListener(taskImage2 -> {
                        if (taskImage2.isSuccessful()) {
                            mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri2 -> {
                                String uuid = UUID.randomUUID().toString();
                                final String url2 = uri2.toString();
                                Post post = new Post();
                                post.setImage1(url);
                                post.setImage2(url2);
                                post.setId(uuid);
                                post.setTitle(mTitle);
                                post.setDescription(mDescription);
                                post.setCategory(mSpinnerCategories);
                                post.setQuality((double) mQuality);
                                post.setIdUser(mAuthProvider.getUid());
                                post.setTimestamp(new Date().getTime());
                                mPostProvider.save(post).addOnCompleteListener(taskSave -> {
                                    mDialog.dismiss();
                                    if (taskSave.isSuccessful()) {
                                       // clearForm();
                                        Toast.makeText(PostActivity.this, "Bilgiler do??ru bir ??ekilde sakland??", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PostActivity.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(PostActivity.this, "Bilgiler saklanamad??", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(PostActivity.this, "2 numaral?? resim kaydedilemedi", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            } else {
                mDialog.dismiss();
                Toast.makeText(PostActivity.this, "G??r??nt?? kaydedilirken bir hata olu??tu", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void retrieveData() {
        DatabaseReference databaseReference = mPostProvider.getCategoryForSpinner();
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item :snapshot.getChildren()){
                    spinnerDataList.add(Objects.requireNonNull(item.child("name").getValue()).toString());
                }
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void clearForm() {
        binding.textInputVideoGame.setText("");
        binding.textInputDescription.setText("");
        binding.imageViewPost1.setImageResource(R.drawable.img);
        binding.imageViewPost2.setImageResource(R.drawable.img);
        mTitle = "";
        mDescription = "";
        mSpinnerCategories = "";
        mImageFile = null;
        mImageFile2 = null;
    }

    private void openGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * GALER??DEN G??R??NT?? SE????M??
         */
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                mPhotoFile = null;
                if (data != null) {
                    mImageFile = FileUtil.from(this, data.getData());
                }
                binding.imageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Bir hata olu??tu " + e.getMessage());
                Toast.makeText(this, "Bir hata olu??tu" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            try {
                mPhotoFile2 = null;
                if (data != null) {
                    mImageFile2 = FileUtil.from(this, data.getData());
                }
                binding.imageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Bir hata olu??tu " + e.getMessage());
                Toast.makeText(this, "Bir hata olu??tu " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        /**
         * FOTO??RAF SE????M??
         */
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.with(PostActivity.this).load(mPhotoPath).into(binding.imageViewPost1);
        }

        /**
         * FOTO??RAF SE????M??
         */
        if (requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            mImageFile2 = null;
            mPhotoFile2 = new File(mAbsolutePhotoPath2);
            Picasso.with(PostActivity.this).load(mPhotoPath2).into(binding.imageViewPost2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, PostActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, PostActivity.this);
    }
}
