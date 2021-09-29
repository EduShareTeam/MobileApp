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
import android.widget.Toast;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.databinding.ActivityEditProfileBinding;
import com.fatihbaser.edusharedemo.databinding.ActivityProfileBinding;
import com.fatihbaser.edusharedemo.models.User;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.ImageProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.fatihbaser.edusharedemo.utils.FileUtil;
import com.fatihbaser.edusharedemo.utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    AlertDialog.Builder mBuilderSelector;
    AlertDialog mDialog;
    CharSequence options[];
    private final int GALLERY_REQUEST_CODE_PROFILE = 1;
    private final int PHOTO_REQUEST_CODE_PROFILE = 3;

    // FOTO 1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;
    File mImageFile;

    String mUsername = "";
    String mUniversity = "";
    String mDepartment = "";
    String mBio = "";
    String mImageProfile = "";

    //Providers
    ImageProvider mImageProvider;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Bir seçenek seçin");
        options = new CharSequence[] {"Galeri resmi","Fotoğraf çek"};

        mImageProvider = new ImageProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Biraz bekle")
                .setCancelable(false).build();
        //TODO: Fotografi kaydettikten sonra profile fragmentine gecerken guncel resim gelmiyor
        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEditProfile();
            }
        });

        binding.circleImageProfile.setOnClickListener(view12 -> selectOptionImage(1));

        binding.circleImageBack.setOnClickListener(view1 -> finish());

        getUser();
    }

    private void getUser() {
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("username")) {
                    mUsername = documentSnapshot.getString("username");
                    binding.textInputUsername.setText(mUsername);
                }
                if (documentSnapshot.contains("university")) {
                    mUniversity = documentSnapshot.getString("university");
                    binding.textInputUniversity.setText(mUniversity);
                }
                if (documentSnapshot.contains("department")) {
                    mDepartment = documentSnapshot.getString("department");
                    binding.textInputDepartment.setText(mDepartment);
                }
                if (documentSnapshot.contains("bio")) {
                    mBio = documentSnapshot.getString("bio");
                    binding.textInputBio.setText(mBio);
                }
                if (documentSnapshot.contains("image")) {
                    mImageProfile = documentSnapshot.getString("image");
                    if (mImageProfile != null) {
                        if (!mImageProfile.isEmpty()) {
                            Picasso.with(ProfileActivity.this).load(mImageProfile).into(binding.circleImageProfile);
                        }
                    }
                }
            }
        });
    }

    private void clickEditProfile() {
        mUsername = binding.textInputUsername.getText().toString();
        mUniversity = binding.textInputUniversity.getText().toString();
        mDepartment = binding.textInputDepartment.getText().toString();
        mBio = binding.textInputBio.getText().toString();
        if (!mUsername.isEmpty() && !mUniversity.isEmpty()&& !mDepartment.isEmpty()&& !mBio.isEmpty()) {
            if (mImageFile != null) {
                saveImageCoverAndProfile(mImageFile);
            }
            //KAMERANIN İKİ RESİMİNİ ÇEKİYORUM
            else if (mPhotoFile != null) {
                saveImageCoverAndProfile(mPhotoFile);
            }
            else if (mPhotoFile != null) {
                saveImage(mPhotoFile, true);
            }
            else if (mImageFile != null) {
                saveImage(mImageFile, true);
            }
            else {
                User user = new User();
                user.setUsername(mUsername);
                user.setUniversity(mUniversity);
                user.setDepartment(mDepartment);
                user.setBio(mBio);
                user.setId(mAuthProvider.getUid());
                updateInfo(user);
            }
        }
        else {
            Toast.makeText(this, "Kullanıcı adını ve telefonu girin", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageCoverAndProfile(File imageFile1) {
        mDialog.show();
        mImageProvider.save(ProfileActivity.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlProfile = uri.toString();

                            mImageProvider.save(ProfileActivity.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if (taskImage2.isSuccessful()) {
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                User user = new User();
                                                user.setImageProfile(urlProfile);
                                                user.setUsername(mUsername);
                                                user.setUniversity(mUniversity);
                                                user.setDepartment(mDepartment);
                                                user.setBio(mBio);
                                                user.setId(mAuthProvider.getUid());
                                                updateInfo(user);
                                            }
                                        });
                                    }
                                    else {
                                        mDialog.dismiss();
                                        Toast.makeText(ProfileActivity.this, "Resim kaydedilemedi", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Görüntü kaydedilirken bir hata oluştu", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveImage(File image, final boolean isProfileImage) {
        mDialog.show();
        mImageProvider.save(ProfileActivity.this, image).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    final String url = uri.toString();
                    User user = new User();
                    user.setUsername(mUsername);
                    user.setUniversity(mUniversity);
                    user.setDepartment(mDepartment);
                    user.setBio(mBio);
                    if (isProfileImage) {
                        user.setImageProfile(url);
                    }
                    else {
                        user.setImageProfile(mImageProfile);
                    }
                    user.setId(mAuthProvider.getUid());
                    updateInfo(user);
                });
            }
            else {
                mDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Görüntü kaydedilirken bir hata oluştu", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateInfo(User user) {
        if (mDialog.isShowing()) {
            mDialog.show();
        }
        mUsersProvider.update(user).addOnCompleteListener(task -> {
            mDialog.dismiss();
            if (task.isSuccessful()) {

                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(ProfileActivity.this, "Bilgiler doğru bir şekilde güncellendi", Toast.LENGTH_SHORT).show();


            }
            else {
                Toast.makeText(ProfileActivity.this, "Bilgiler güncellenemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectOptionImage(final int numberImage) {

        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
                Toast.makeText(this, "Dosyada bir hata oluştu " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(ProfileActivity.this, "com.fatihbaser.edusharedemo", photoFile);
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
                binding.circleImageProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
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
            Picasso.with(ProfileActivity.this).load(mPhotoPath).into(binding.circleImageProfile);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, ProfileActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, ProfileActivity.this);
    }
}