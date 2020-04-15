package com.example.mechachromemobileapp.Activities.Forum;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mechachromemobileapp.Models.ForumPost;
import com.example.mechachromemobileapp.Models.User;
import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class ForumPostReply extends AppCompatActivity {

    public static final String TAG = "TAG";
    private EditText editContent;
    private TextView topic, viewTopic;
    private Button replyPostBtn, discardPostBtn;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private Date date_published = Calendar.getInstance().getTime();;
    private String topicName, topicID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_post_reply);
        initViews();
        setButtons();
    }

    private void initViews() {
        viewTopic = findViewById(R.id.viewTopic);
        editContent = findViewById(R.id.editContent);
        replyPostBtn = findViewById(R.id.addReplyBtn);
        discardPostBtn = findViewById(R.id.discardPostBtn);

        // getting Auth and userID
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        // getting fStore instance
        fStore = FirebaseFirestore.getInstance();

        // getting intent from Forum activity and getting extra string
        Intent intent = getIntent();
        topicName = intent.getStringExtra("topic_name");
        topicID = intent.getStringExtra("topic_id");

        // getting and setting the topic name on top of our layout to topic name
        topic = findViewById(R.id.topic_name);
        viewTopic.setText(topicName);
    }

    private void setButtons() {
        replyPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReply();
            }
        });

        discardPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finishing the activity and starting previous one
                Intent intent = new Intent(ForumPostReply.this, ForumViewTopic.class);
                intent.putExtra("topic_name", topicName);
                startActivity(intent);
                finish();
            }
        });
    }

    private void postReply() {
        final String content = editContent.getText().toString().trim();

        if(TextUtils.isEmpty(content)){
            editContent.setError("Please add content");
            return;
        }

        DocumentReference userRef = fStore.collection("users").document(fUser.getUid());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        DocumentSnapshot document = task.getResult();
                        User user = document.toObject(User.class);

                        ForumPost newPost = new ForumPost();

                        String author_name = user.getFname() + " " + user.getLname();
                        newPost.setAuthor(author_name);
                        newPost.setAuthor_id(fUser.getUid());
                        newPost.setContent(content);
                        newPost.setDate_published(date_published);
                        newPost.setTopic_name(topicName);

                        // setting the post
                        DocumentReference postReference = fStore.collection("forum_topics").document(topicID).collection("posts").document();
                        postReference.set(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "New Post document created");
                            }
                        });

                        // updating topic post number value
                        DocumentReference topicRef = fStore.collection("forum_topics").document(topicID);
                        topicRef.update("post_num", FieldValue.increment(1));

                        // Finishing the activity and starting new
                        Intent intent = new Intent(ForumPostReply.this, ForumViewTopic.class);
                        intent.putExtra("topic_name", topicName);
                        intent.putExtra("topic_id", topicID);
                        startActivity(intent);
                        finish();
                    } catch(NullPointerException e) {
                        Log.e(TAG, "onComplete: NullPointerException: " + e);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
