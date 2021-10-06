package com.fatihbaser.edusharedemo.activities;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.databinding.ActivityCompleteProfileBinding;
import com.fatihbaser.edusharedemo.databinding.ActivityRegisterBinding;
import com.fatihbaser.edusharedemo.models.User;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.ImageProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.fatihbaser.edusharedemo.utils.FileUtil;
import com.fatihbaser.edusharedemo.utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;
    UsersProvider mUsersProvider;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //Providers
        mAuthProvider = new AuthProvider();
        mImageProvider = new ImageProvider();
        mUsersProvider = new UsersProvider();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Bir seçenek seçin");
        options = new CharSequence[] {"Galeri resmi","Fotoğraf çek"};


        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Kayıt  Yapılıyor ...")
                .setCancelable(false).build();

        binding.imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage(1);
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private void register() {
        String username = binding.textInputUsername.getText().toString();
        String email = binding.textInputEmail.getText().toString();
        String university = binding.uniname.getText().toString();
        String department = binding.department.getText().toString();
        String bio = binding.bio.getText().toString();
        String password = binding.textInputPassword.getText().toString();
        String confirmPassword = binding.textInputConfirmPassword.getText().toString();

        if (mImageFile != null ) {
            if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !university.isEmpty() && !department.isEmpty() && !bio.isEmpty() && !confirmPassword.isEmpty()&&mImageFile != null) {
                if (isEmailValid(email)) {
                    if (password.equals(confirmPassword)) {
                        if (password.length() >= 6) {
                            createUser(username, email, password, university, department, bio,mImageFile);
                        } else {
                            Toast.makeText(this, "Şifre en az 6 karakter olmalıdır", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Parolalar uyuşmuyor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Tüm alanları girdiniz ancak e-posta geçerli değil", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Devam etmek için tüm alanları ekleyin", Toast.LENGTH_SHORT).show();
            }
        }
      else if (mPhotoFile != null ) {
            if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !university.isEmpty() && !department.isEmpty() && !bio.isEmpty() && !confirmPassword.isEmpty()&&mPhotoFile != null) {
                if (isEmailValid(email)) {
                    if (password.equals(confirmPassword)) {
                        if (password.length() >= 6) {
                            createUser(username, email, password, university, department, bio,mPhotoFile);
                        } else {
                            Toast.makeText(this, "Şifre en az 6 karakter olmalıdır", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Parolalar uyuşmuyor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Tüm alanları girdiniz ancak e-posta geçerli değil", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Devam etmek için tüm alanları ekleyin", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void createUser(final String username, final String email, final String password, final String university, final String department, final String bio,File imageFile1) {
        mDialog.show();
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuthProvider.getUid();

                    User user = new User();
                    user.setId(id);
                    user.setEmail(email);

                    mUsersProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDialog.dismiss();
                            if (task.isSuccessful()) {
                                mImageProvider.save(RegisterActivity.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()){
                                            mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    User user = new User();
                                                    final String urlProfile = uri.toString();
                                                    String id = mAuthProvider.getUid();
                                                    user.setId(id);
                                                    user.setImageProfile(urlProfile);
                                                    user.setUsername(username);
                                                    user.setUniversity(university);
                                                    user.setDepartment(department);
                                                    user.setBio(bio);
                                                    user.setTimestamp(new Date().getTime());
                                                    mUsersProvider.updateProfile(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            mDialog.dismiss();
                                                            if (task.isSuccessful()) {
                                                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(intent);
                                                            }
                                                            else {
                                                                Toast.makeText(RegisterActivity.this, "Kullanıcı veritabanında saklanamadı", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "Mail Kullanıcı veritabanında saklanamadı", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Kullanıcı kaydedilemedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
                Uri photoUri = FileProvider.getUriForFile(RegisterActivity.this, "com.fatihbaser.edusharedemo", photoFile);
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
            Picasso.with(RegisterActivity.this).load(mPhotoPath).into(binding.imageViewProfile);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, RegisterActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, RegisterActivity.this);
    }

}