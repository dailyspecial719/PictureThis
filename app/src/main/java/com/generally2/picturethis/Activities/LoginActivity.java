package com.generally2.picturethis.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginMail, loginPassword;
    private TextView loginSignUp;
    private Button loginBtn;
    private ImageView loginPhoto;
    private ProgressBar loginBar;
    private FirebaseAuth mAuth;
    private Intent homeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginMail = findViewById(R.id.login_mail);
        loginPassword = findViewById(R.id.login_password);
        loginSignUp = findViewById(R.id.login_signup);
        loginBtn = findViewById(R.id.login_button);
        loginPhoto = findViewById(R.id.login_photo);
        loginBar = findViewById(R.id.login_progress);
        mAuth = FirebaseAuth.getInstance();
        homeActivity = new Intent(this, Home.class);

        loginBar.setVisibility(View.INVISIBLE);


        loginPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage("click 'Sign Up' to register");
                loginBtn.setVisibility(View.VISIBLE);
                loginBar.setVisibility(View.INVISIBLE);
            }
        });



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setVisibility(View.INVISIBLE);
                loginBar.setVisibility(View.VISIBLE);


                final String mail = loginMail.getText().toString();
                final String password = loginPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty()){

                    showMessage("Please verify all fields");
                    loginBtn.setVisibility(View.VISIBLE);
                    loginBar.setVisibility(View.INVISIBLE);

                } else {
                    signIn(mail, password);
                }
            }
        });

        loginSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regActivity);
                finish();
            }
        });


    }

    private void signIn(String mail, String password) {

        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    loginBar.setVisibility(View.INVISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                    updateUi();
                } else {
                    showMessage(task.getException().getMessage());
                    loginBtn.setVisibility(View.VISIBLE);
                    loginBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void updateUi() {
        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            updateUi();

        }
    }
}