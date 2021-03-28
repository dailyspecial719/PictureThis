package com.generally2.picturethis.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.generally2.picturethis.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    ImageView regUserPhoto;
    static int PReqCode = 1;
    static int REQUESTCODE = 1;
    Uri pickedImgUri;
    private FirebaseAuth mAuth;


    private EditText userName, userMail, userPassword, userPassword2;
    private Button regBtn;
    private ProgressBar regProgress;
    private TextView regLogin;
    private Intent loginActivity;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regUserPhoto = findViewById(R.id.regUserPhoto);
        userName = findViewById(R.id.regName);
        userMail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userPassword2 = findViewById(R.id.regPassword2);
        regBtn = findViewById(R.id.regBtn);
        regProgress = findViewById(R.id.regProgress);
        regLogin = findViewById(R.id.regLogin);
        loginActivity = new Intent(getApplicationContext(), LoginActivity.class);

        mAuth = FirebaseAuth.getInstance();


        regLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(loginActivity);
                finish();
            }
        });


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtn.setVisibility(View.INVISIBLE);
                regProgress.setVisibility(View.VISIBLE);
                final String name = userName.getText().toString();
                final String email = userMail.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();


                if (email.isEmpty() || name.isEmpty() || password.isEmpty() || password2.isEmpty() || !password.equals(password2)){
                    showMessage("Please verify all fields");
                    regBtn.setVisibility(View.VISIBLE);
                    regProgress.setVisibility(View.INVISIBLE);


                } else {
                    createUserAccount(email, name, password);

                }


            }
        });



        regUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 22){
                    permissionRequest();

                } else {
                    openGallery();
                }
            }
        });
    }

    private void createUserAccount(String email, final String name, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    showMessage("Account Created Successfully");

                    //check to see if picked image is null or not
                    if(pickedImgUri != null) {
                        updateUserInfo(name, pickedImgUri, mAuth.getCurrentUser());
                    } else {
                        updateUserInfoNoPhoto(name, mAuth.getCurrentUser());
                    }

                } else {
                    showMessage("Account Creation Failed");
                    regBtn.setVisibility(View.VISIBLE);
                    regProgress.setVisibility(View.INVISIBLE);

                }

            }
        });


    }

    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("user_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            showMessage("Registration Successful");
                                            updateUi();

                                        }

                                    }
                                });
                    }
                });

            }
        });

    }

    private void updateUserInfoNoPhoto(final String name, final FirebaseUser currentUser) {

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            showMessage("Registration Successful");
                                            updateUi();

                                        }

                                    }
                                });





    }


    private void updateUi() {

        Intent homeActivity = new Intent(getApplicationContext(), Home.class);
        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);


    }


    private void permissionRequest() {

        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){

                Toast.makeText(RegisterActivity.this, "Please accept for required permissions", Toast.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                                          new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                          PReqCode);
            }

        }
        else {
            openGallery();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null ){

            pickedImgUri = data.getData();
            regUserPhoto.setImageURI(pickedImgUri);

        }
    }
}