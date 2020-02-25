package com.example.mechachromemobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class ForumPostReply extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText editContent;
    TextView topic, viewTopic;
    Button replyPostBtn;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    Date date_published;
    String userID, topicFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_post_reply);

        viewTopic = findViewById(R.id.viewTopic);
        editContent = findViewById(R.id.editContent);
        replyPostBtn = findViewById(R.id.addReplyBtn);
        date_published = Calendar.getInstance().getTime();

        // getting Auth and userID
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        // getting fStore instance
        fStore = FirebaseFirestore.getInstance();

        // getting intent from Forum activity and getting extra string
        Intent intent = getIntent();
        topicFeed = intent.getStringExtra("topic_name");

        // getting and setting the topic name on top of our layout to topic name
        topic = findViewById(R.id.topic);
        viewTopic.setText(topicFeed);


        replyPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = editContent.getText().toString().trim();
                final ArrayList<String> author = new ArrayList<>();


                // getting the user
                DocumentReference userRef = fStore.collection("users").document(userID);
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot user = task.getResult();
                        if (task.isSuccessful()) {
                            if (user.exists()) {
                                Log.d(TAG, "Got the user " + userID);
                                String author_temp = user.get("fname").toString() + " " + user.get("lname").toString();
                                author.add(0, author_temp);
                            } else {
                                Log.d(TAG, "No such user");
                            }

                            // setting the post
                            DocumentReference postRef = fStore.collection("forum_posts").document();
                            Map<String, Object> addPost = new TreeMap<>();
                            addPost.put("topic_name", topicFeed);
                            addPost.put("date_published", date_published);
                            addPost.put("author", author.get(0));
                            addPost.put("content", content);
                            postRef.set(addPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "New Post document created");

                                }
                            });

                            // updating topic post number value
                            DocumentReference topicRef = fStore.collection("forum_topics").document(topicFeed);
                            topicRef.update("post_num", FieldValue.increment(1));

                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });





            }
        });
    }
}
