package com.example.mechachromemobileapp.Activities.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mechachromemobileapp.Models.User;
import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserMessage extends AppCompatActivity {

    CircleImageView userImage;
    TextView username;

    FirebaseUser fUser;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    CollectionReference userRef;
    DocumentReference userDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message);
        initViews();
    }

    public void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        userImage = findViewById(R.id.user_image);
        username = findViewById(R.id.user_name);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        fStore = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = fStore.collection("users");
        userDocument = userRef.document(userID);

        userDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                String fullName = user.getFname() + " " + user.getLname();
                username.setText(fullName);
                if(user.getImgUrl().equals("default")) {
                    userImage.setImageResource(R.drawable.ic_account);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImgUrl()).into(userImage);
                }
            }
        });

    }


    public void methodName() {

    }


}
