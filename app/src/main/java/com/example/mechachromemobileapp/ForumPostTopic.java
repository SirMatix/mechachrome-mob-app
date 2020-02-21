package com.example.mechachromemobileapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ForumPostTopic extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText editTopic, editContent;
    Button addTopicBtn;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    Date date_published;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_post_topic);

        editTopic = findViewById(R.id.editTopic);
        editContent = findViewById(R.id.editContent);
        addTopicBtn = findViewById(R.id.addTopicBtn);

        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        fStore = FirebaseFirestore.getInstance();

        date_published = Calendar.getInstance().getTime();



        addTopicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = editTopic.getText().toString().trim();
                String content = editContent.getText().toString().trim();
                final ArrayList<String> author = new ArrayList<>();

                DocumentReference userRef = fStore.collection("users").document(userID);
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot user = task.getResult();
                        if(task.isSuccessful()) {
                            if (user.exists()) {
                                Log.d(TAG,"Got the user");
                                String author_temp = user.get("fname").toString() + " " + user.get("lname").toString();
                                author.add(author_temp);
                            } else {
                                Log.d(TAG,"No such user");
                            }
                        }
                        else {
                            Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                });

                DocumentReference topicRef = fStore.collection("forum_topics").document();
                Map<String, Object> addTopic = new HashMap<>();
                addTopic.put("topic_name", topic);
                addTopic.put("date_published", date_published);
                addTopic.put("author", author);
                topicRef.set(addTopic).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"New Topic document created");
                    }
                });

                DocumentReference postRef = fStore.collection("forum_posts").document();
                Map<String, Object> addPost = new HashMap<>();
                addPost.put("topic_name", topic);
                addPost.put("date_published", date_published);
                addPost.put("author", author);
                addPost.put("content", content);
                postRef.set(addPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"New Post document created");
                    }
                });
            }
        });



    }
}
