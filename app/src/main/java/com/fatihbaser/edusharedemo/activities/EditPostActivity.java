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

import com.fatihbaser.edusharedemo.databinding.ActivityEditPostBinding;
import com.fatihbaser.edusharedemo.models.Post;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.ImageProvider;
import com.fatihbaser.edusharedemo.providers.PostProvider;
import com.fatihbaser.edusharedemo.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class EditPostActivity extends AppCompatActivity {

    private ActivityEditPostBinding binding;
    String mExtraPostId;
    String mTitle = "";
    String mDescription = "";
    float mQuality = 0;
    String mSpinnerCategories = "";

    File mImageFile;
    File mImageFile2;

    String mImage1;
    String mImage2;
    //Providers
    ImageProvider mImageProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;

    //AlertDialog
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
        binding = ActivityEditPostBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mExtraPostId = getIntent().getStringExtra("id");

        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new AuthProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("L??tfen biraz bekleyiniz")
                .setCancelable(false).build();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("L??tfen bir se??enek se??iniz");
        options = new CharSequence[]{"Galeriden Resmi se??", "Fotograf ??ek"};

        binding.circleImageBack.setOnClickListener(view1 -> finish());

        binding.btnPost.setOnClickListener(view12 -> clickEditPost());

        binding.imageViewPost1.setOnClickListener(view13 -> selectOptionImage(1));

        binding.imageViewPost2.setOnClickListener(view14 -> selectOptionImage(2));
        //extra
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("image1") && documentSnapshot.contains("image2")) {
                    mImage1 = documentSnapshot.getString("image1");
                    mImage2 = documentSnapshot.getString("image2");
                }
            }
        });


        spinnerDataList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(EditPostActivity.this,
                android.R.layout.simple_spinner_dropdown_item, spinnerDataList);
        binding.spinnerProductCategory.setAdapter(arrayAdapter);
        retrieveSpinnerData();

        getPost();

    }

    private void retrieveSpinnerData() {
        DatabaseReference databaseReference = mPostProvider.getCategoryForSpinner();
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    spinnerDataList.add(Objects.requireNonNull(item.child("name").getValue()).toString());
                    mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.contains("category")) {
                             String category = documentSnapshot.getString("category");
                             spinnerDataList.set(0,category);
                             arrayAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getPost() {
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                if (documentSnapshot.contains("image1")) {
                    String image1 = documentSnapshot.getString("image1");
                    if (image1 != null) {
                        if (!image1.isEmpty()) {
                            Picasso.with(getApplicationContext()).load(image1).into(binding.imageViewPost1);
                        }
                    }
                }

                if (documentSnapshot.contains("image2")) {
                    String image2 = documentSnapshot.getString("image2");
                    if (image2 != null) {
                        if (!image2.isEmpty()) {
                            Picasso.with(getApplicationContext()).load(image2).into(binding.imageViewPost2);
                        }
                    }
                }
                if (documentSnapshot.contains("title")) {
                    String title = documentSnapshot.getString("title");
                    assert title != null;
                    binding.textInputTitle.setText(title);
                }
                if (documentSnapshot.contains("description")) {
                    String description = documentSnapshot.getString("description");
                    binding.textInputDescription.setText(description);
                }
                if (documentSnapshot.contains("quality")) {
                    Long quality = documentSnapshot.getLong("quality");
                    binding.ratingBarProductQualityUpload.setRating(quality);
                }
            }
        });
    }

    private void clickEditPost() {
        mTitle = Objects.requireNonNull(binding.textInputTitle.getText()).toString();
        mDescription = Objects.requireNonNull(binding.textInputDescription.getText()).toString();
        mQuality = binding.ratingBarProductQualityUpload.getRating();
        mSpinnerCategories = binding.spinnerProductCategory.getSelectedItem().toString();
        if (!mTitle.isEmpty() && !mDescription.isEmpty()) {
            // GALER??DEN ??K?? RES??M SE????MI
            if (mImageFile != null && mImageFile2 != null) {
                saveImageAndEdit(mImageFile, mImageFile2);
            }
            // KAMERANIN ??K?? RESIM CEKIMI
            else if (mPhotoFile != null && mPhotoFile2 != null) {
                saveImageAndEdit(mPhotoFile, mPhotoFile2);
                // DIGER DURUMLAR
            } else if (mImageFile != null && mPhotoFile2 != null) {
                saveImageAndEdit(mPhotoFile, mPhotoFile2);
            } else if (mPhotoFile != null && mImageFile2 != null) {
                saveImageAndEdit(mPhotoFile, mPhotoFile2);

            } else if (mPhotoFile != null) {
                saveImage(mPhotoFile, true);
            } else if (mPhotoFile2 != null) {
                saveImage(mPhotoFile2, false);
            } else if (mImageFile != null) {
                saveImage(mImageFile, true);
            } else if (mImageFile2 != null) {
                saveImage(mImageFile2, false);
            } else {
                //Toast.makeText(this, "Bir resim se??melisiniz", Toast.LENGTH_SHORT).show();
                Post post = new Post();
                post.setTitle(mTitle);
                post.setImage1(mImage1);
                post.setImage2(mImage2);
                post.setDescription(mDescription);
                post.setCategory(mSpinnerCategories);
                post.setId(mExtraPostId);
                post.setQuality((double) mQuality);
                post.setTimestamp(new Date().getTime());
                updatePost(post);
            }
        } else {
            Toast.makeText(this, "Alanlar?? doldurun l??tfen", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageAndEdit(File imageFile1, final File imageFile2) {
        mDialog.show();
        mImageProvider.save(EditPostActivity.this, imageFile1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    final String url = uri.toString();

                    mImageProvider.save(EditPostActivity.this, imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                            if (taskImage2.isSuccessful()) {
                                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri2) {
                                        final String url2 = uri2.toString();
                                        Post post = new Post();
                                        post.setImage1(url);
                                        post.setImage2(url2);
                                        post.setId(mExtraPostId);
                                        post.setTitle(mTitle);
                                        post.setDescription(mDescription);
                                        post.setCategory(mSpinnerCategories);
                                        post.setQuality((double) mQuality);
                                        post.setTimestamp(new Date().getTime());
                                        updatePost(post);
                                    }
                                });
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(EditPostActivity.this, "2 numaral?? resim kaydedilemedi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                });
            } else {
                mDialog.dismiss();
                Toast.makeText(EditPostActivity.this, "G??r??nt?? kaydedilirken bir hata olu??tu", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveImage(File image, final boolean isProfileImage) {
        mDialog.show();
        mImageProvider.save(EditPostActivity.this, image).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    final String url = uri.toString();
                    Post post = new Post();
                    post.setTitle(mTitle);
                    post.setDescription(mDescription);
                    post.setCategory(mSpinnerCategories);
                    post.setQuality((double) mQuality);
                    post.setId(mExtraPostId);
                    post.setTimestamp(new Date().getTime());
                    updatePost(post);
                    if (isProfileImage) {
                        post.setImage1(url);
                        post.setImage2(mImage2);
                    } else {
                        post.setImage2(url);
                        post.setImage1(mImage1);
                    }
                    post.setId(mExtraPostId);
                    updatePost(post);
                });
            } else {
                mDialog.dismiss();
                Toast.makeText(EditPostActivity.this, "G??r??nt?? kaydedilirken bir hata olu??tu", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updatePost(Post post) {
        if (mDialog.isShowing()) {
            mDialog.show();
        }
        mPostProvider.updatePost(post).addOnCompleteListener(task -> {
            mDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(EditPostActivity.this, "Bilgiler do??ru bir ??ekilde g??ncellendi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditPostActivity.this, "Bilgiler g??ncellenemedi", Toast.LENGTH_SHORT).show();
            }
        });
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
                Uri photoUri = FileProvider.getUriForFile(EditPostActivity.this, "com.fatihbaser.edusharedemo", photoFile);
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
                mImageFile = FileUtil.from(this, data.getData());
                binding.imageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Bir hata olu??tu " + e.getMessage());
                Toast.makeText(this, "Bir hata olu??tu" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            try {
                mPhotoFile2 = null;
                mImageFile2 = FileUtil.from(this, data.getData());
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
            Picasso.with(EditPostActivity.this).load(mPhotoPath).into(binding.imageViewPost1);
        }

        /**
         * FOTO??RAF SE????M??
         */
        if (requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            mImageFile2 = null;
            mPhotoFile2 = new File(mAbsolutePhotoPath2);
            Picasso.with(EditPostActivity.this).load(mPhotoPath2).into(binding.imageViewPost2);
        }
    }
}