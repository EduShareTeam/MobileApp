package com.fatihbaser.edusharedemo.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.databinding.ActivityCompleteProfileBinding;
import com.fatihbaser.edusharedemo.models.User;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.ImageProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.fatihbaser.edusharedemo.utils.FileUtil;
import com.fatihbaser.edusharedemo.utils.ViewedMessageHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class CompleteProfileActivity extends AppCompatActivity {
    private ActivityCompleteProfileBinding binding;
    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;
    UsersProvider mUsersProvider;


    AlertDialog.Builder mBuilderSelector;
    AlertDialog mDialog;
    CharSequence[] options;
    private final int GALLERY_REQUEST_CODE_PROFILE = 1;
    private final int PHOTO_REQUEST_CODE_PROFILE = 3;

    // FOTO 1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;
    File mImageFile;

    String username = "";
    String mUniversity = "";
    String mDepartment = "";
    String mBio = "";
    String mImageProfile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompleteProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //Providers
        mAuthProvider = new AuthProvider();
        mImageProvider = new ImageProvider();
        mUsersProvider = new UsersProvider();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Lütfen bir seçenek seçiniz");
        options = new CharSequence[] {"Galeriden resim seç","Fotoğraf çek"};


        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Kayıt  Yapılıyor ...")
                .setCancelable(false).build();


        binding.imageViewProfile.setOnClickListener(view1 -> selectOptionImage(1));

        binding.btnRegister.setOnClickListener(view12 -> {

            if (mImageFile != null ) {
                completeUser(mImageFile);
            }
            else if (mPhotoFile != null ) {
                completeUser(mPhotoFile);
            }

        });
    }

    @Override
    public void onBackPressed() {
        myAlert(CompleteProfileActivity.this);
    }

    public void myAlert(Context context){
        new AlertDialog.Builder(context)
                .setIcon(R.drawable.edu).setTitle("Çıkış ?")
                .setMessage("Lütfen kayıt kısmını tamamlayınız...")
                .setPositiveButton("Tamam", (dialogInterface, i) -> {

        }).show();
    }

    private void completeUser(File imageFile1) {
        mDialog.show();
        username = Objects.requireNonNull(binding.textInputUsername.getText()).toString();
        mUniversity = Objects.requireNonNull(binding.textInputUniversity.getText()).toString();
        mDepartment = Objects.requireNonNull(binding.textInputFaculty.getText()).toString();
        mBio = Objects.requireNonNull(binding.textInputBio.getText()).toString();
        mImageProvider.save(CompleteProfileActivity.this, imageFile1).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    User user = new User();
                    final String urlProfile = uri.toString();
                    String id = mAuthProvider.getUid();
                    user.setId(id);
                    user.setImageProfile(urlProfile);
                    user.setUsername(username.toLowerCase());
                    user.setUniversity(mUniversity.toLowerCase());
                    user.setDepartment(mDepartment.toLowerCase());
                    user.setBio(mBio);
                    user.setTimestamp(new Date().getTime());
                    mUsersProvider.updateProfile(user).addOnCompleteListener(task1 -> {
                        mDialog.dismiss();
                        if (task1.isSuccessful()) {
                            Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(CompleteProfileActivity.this, "Oppss! Kullanıcı veri tabanında saklanamadı", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });
    }

    private void selectOptionImage(final int numberImage) {

        mBuilderSelector.setItems(options, (dialogInterface, i) -> {
            if (i == 0) {
                if (numberImage == 1) {
                    openGallery(GALLERY_REQUEST_CODE_PROFILE);
                }
            }
            else if (i == 1){
                if (numberImage == 1) {
                    takePhoto(PHOTO_REQUEST_CODE_PROFILE);
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
            } catch(Exception e) {
                Toast.makeText(this, "Oppss! Dosyada bir hata oluştu " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(CompleteProfileActivity.this, "com.fatihbaser.edusharedemo", photoFile);
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
                storageDir
        );
        if (requestCode == PHOTO_REQUEST_CODE_PROFILE) {
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
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
        if (requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK) {
            try {
                mPhotoFile = null;
                mImageFile = FileUtil.from(this, data.getData());
                binding.imageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch(Exception e) {
                Log.d("ERROR", "Bir hata oluştu" + e.getMessage());
                Toast.makeText(this, "Bir hata oluştu " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        /**
         * FOTOĞRAF SEÇİMİ
         */
        if (requestCode == PHOTO_REQUEST_CODE_PROFILE && resultCode == RESULT_OK) {
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.with(CompleteProfileActivity.this).load(mPhotoPath).into(binding.imageViewProfile);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, CompleteProfileActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, CompleteProfileActivity.this);
    }

}