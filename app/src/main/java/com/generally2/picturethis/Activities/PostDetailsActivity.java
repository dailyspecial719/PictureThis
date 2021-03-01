package com.generally2.picturethis.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.generally2.picturethis.Models.Comment;
import com.generally2.picturethis.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class PostDetailsActivity extends AppCompatActivity {

    ImageView imgPost, imgUserPost, imgCurrentPhoto;
    TextView textPostDesc, textPostTitle, textPostDateName;
    EditText editTextComment;
    Button editCommentBtn;
    String PostKey;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);


        imgPost = findViewById(R.id.post_detail_img);
        imgUserPost = findViewById(R.id.post_detial_user_img);
        imgCurrentPhoto = findViewById(R.id.post_detail_current_user_img);

        textPostTitle = findViewById(R.id.post_detail_title);
        textPostDesc = findViewById(R.id.post_detail_description);
        textPostDateName = findViewById(R.id.post_detail_datename);

        editTextComment = findViewById(R.id.post_detail_comment);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();


        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        getSupportActionBar().hide();

        editCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCommentBtn.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference("Comment").child(PostKey).push();
                String comment_context = editTextComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uimg = firebaseUser.getPhotoUrl().toString();

                Comment comment = new Comment(comment_context, uid, uimg, uname);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("Comment Added");
                        editTextComment.setText("");
                        editCommentBtn.setVisibility(View.VISIBLE);



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Failed To Add Comment" + e.getMessage());

                    }
                });
            }
        });




        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imgPost);

        String postTitle = getIntent().getExtras().getString("title");
        textPostTitle.setText(postTitle);

        String postUserImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(postUserImage).into(imgUserPost);

        String postDescription = getIntent().getExtras().getString("description");
        textPostDesc.setText(postDescription);


        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentPhoto);

        PostKey = getIntent().getExtras().getString("postKey");
        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        textPostDateName.setText(date);



    }

    private void showMessage(String message) {
        Toast.makeText(this , message, Toast.LENGTH_SHORT).show();
    }

    private String timestampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("MM-dd-yyyy", calendar).toString();
        return date;
    }
}