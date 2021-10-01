package com.fatihbaser.edusharedemo.activities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.fatihbaser.edusharedemo.databinding.ActivityEditPostBinding;
import com.fatihbaser.edusharedemo.fragments.ProfileFragment;
import com.fatihbaser.edusharedemo.models.Post;
import com.fatihbaser.edusharedemo.models.User;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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
    CharSequence options[];

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

    //Spınner
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
                .setMessage("Biraz bekle ")
                .setCancelable(false).build();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Bir seçenek seçin");
        options = new CharSequence[]{"Galeriden Resmi alın", "Fotograf çek "};

        binding.circleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEditPost();
            }
        });

        binding.imageViewPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage(1);
            }
        });

        binding.imageViewPost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage(2);
            }
        });
        //extra
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("image1") && documentSnapshot.contains("image2")){
                        mImage1 = documentSnapshot.getString("image1");
                        mImage2 = documentSnapshot.getString("image2");
                    }
                }
            }
        });


        spinnerDataList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(EditPostActivity.this,
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
                    spinnerDataList.add(item.child("name").getValue().toString());
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getPost() {
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
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
                    //TODO: Spinerdan kaydedilen degeri cekme--
//                    if (documentSnapshot.contains("category")) {
//                        String category = documentSnapshot.getString("category");
//                        binding.spinnerProductCategory.r
//                    }

                    if (documentSnapshot.contains("quality")) {
                        Long quality = documentSnapshot.getLong("quality");
                        binding.ratingBarProductQualityUpload.setRating(quality);
                    }
                }
            }
        });
    }

    private void clickEditPost() {
        mTitle = binding.textInputTitle.getText().toString();
        mDescription = binding.textInputDescription.getText().toString();
        mQuality = binding.ratingBarProductQualityUpload.getRating();
        mSpinnerCategories = binding.spinnerProductCategory.getSelectedItem().toString();
        if (!mTitle.isEmpty() && !mDescription.isEmpty()) {
            // GALERİDEN İKİ RESİM SEÇİMI
            if (mImageFile != null && mImageFile2 != null) {
                saveImageAndEdit(mImageFile, mImageFile2);
            }
            // KAMERANIN İKİ RESIM CEKIMI
            else if (mPhotoFile != null && mPhotoFile2 != null) {
                saveImageAndEdit(mPhotoFile, mPhotoFile2);
                // DIGER DURUMLAR
            } else if (mImageFile != null && mPhotoFile2 != null) {
                saveImage(mImageFile, true);
            } else if (mPhotoFile != null && mImageFile2 != null) {
                saveImage(mPhotoFile, true);

            } else {
                //Toast.makeText(this, "Bir resim seçmelisiniz", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Alanları doldurun lütfen", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveImageAndEdit(File imageFile1, final File imageFile2) {
        mDialog.show();
        mImageProvider.save(EditPostActivity.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
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
                                        Toast.makeText(EditPostActivity.this, "2 numaralı resim kaydedilemedi", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(EditPostActivity.this, "Görüntü kaydedilirken bir hata oluştu", Toast.LENGTH_LONG).show();
                }
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
                    }
                    else {
                        post.setImage1("");
                    }
                });
            }
            else {
                mDialog.dismiss();
                Toast.makeText(EditPostActivity.this, "Görüntü kaydedilirken bir hata oluştu", Toast.LENGTH_LONG).show();
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
                Toast.makeText(EditPostActivity.this, "Bilgiler doğru bir şekilde güncellendi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditPostActivity.this, "Bilgiler güncellenemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectOptionImage(final int numberImage) {

        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
                Toast.makeText(this, "Dosyada bir hata oluştu " + e.getMessage(), Toast.LENGTH_LONG).show();
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
         * GALERİDEN GÖRÜNTÜ SEÇİMİ
         */
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                mPhotoFile = null;
                mImageFile = FileUtil.from(this, data.getData());
                binding.imageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Bir hata oluştu " + e.getMessage());
                Toast.makeText(this, "Bir hata oluştu" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            try {
                mPhotoFile2 = null;
                mImageFile2 = FileUtil.from(this, data.getData());
                binding.imageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Bir hata oluştu " + e.getMessage());
                Toast.makeText(this, "Bir hata oluştu " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        /**
         * FOTOĞRAF SEÇİMİ
         */
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.with(EditPostActivity.this).load(mPhotoPath).into(binding.imageViewPost1);
        }

        /**
         * FOTOĞRAF SEÇİMİ
         */
        if (requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            mImageFile2 = null;
            mPhotoFile2 = new File(mAbsolutePhotoPath2);
            Picasso.with(EditPostActivity.this).load(mPhotoPath2).into(binding.imageViewPost2);
        }
    }
}