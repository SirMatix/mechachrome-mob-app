package com.example.mechachromemobileapp.Activities.Forum;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mechachromemobileapp.Models.ForumPost;
import com.example.mechachromemobileapp.Models.ForumTopic;
import com.example.mechachromemobileapp.Models.User;
import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

/**
 * Activity to post a topic and add it to forun_post Collection in Firestore
 *
 */
public class ForumPostTopic extends AppCompatActivity {

    public static final String TAG = "ForumPostTopic";
    private EditText editTopic, editContent;
    private Button addTopicBtn, discardTopicBtn;
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private FirebaseFirestore fStore;
    private Date date_published = Calendar.getInstance().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_post_topic);
        initViews();
        setButtons();
    }


    public void initViews() {
        editTopic = findViewById(R.id.editTopic);
        editContent = findViewById(R.id.editContent);
        addTopicBtn = findViewById(R.id.addTopicBtn);
        discardTopicBtn = findViewById(R.id.discardTopicBtn);

        // initialize Firebase variables
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
    }

    public void setButtons() {
        addTopicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewTopic();
            }
        });

        discardTopicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForumPostTopic.this, Forum.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void createNewTopic() {
        final String topicName = editTopic.getText().toString().trim();
        final String content = editContent.getText().toString().trim();

        if(TextUtils.isEmpty(topicName)){
            editTopic.setError("Topic can't be empty");
            return;
        }
        // checks password field
        if(TextUtils.isEmpty(content)){
            editContent.setError("Please add content");
            return;
        }

        DocumentReference userRef = fStore.collection("users").document(fUser.getUid());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    try{
                        // Instantiating User object from DocumentSnapshot
                        assert document != null;
                        User user = document.toObject(User.class);

                        // Creating new instance of ForumTopic class
                        ForumTopic newTopic = new ForumTopic();

                        // Assigning variables to newTopic
                        assert user != null;
                        final String author_name = user.getFname() + " " + user.getLname();
                        newTopic.setAuthor(author_name);
                        newTopic.setAuthor_id(fUser.getUid());
                        newTopic.setDate_published(date_published);
                        newTopic.setPost_num(1);
                        newTopic.setTopic_name(topicName);

                        // Writing newTopic to the FirebaseFirestore database
                        DocumentReference topicRef = fStore.collection("forum_topics").document();
                        topicRef.set(newTopic).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "New Topic document created");
                            }
                        });

                        // Creating instance of ForumPost class
                        ForumPost firstPost = new ForumPost();

                        // Setting first post variables
                        firstPost.setTopic_name(topicName);
                        firstPost.setAuthor(author_name);
                        firstPost.setAuthor_id(fUser.getUid());
                        firstPost.setContent(content);
                        firstPost.setDate_published(date_published);

                        // Writing the post to the FirebaseFirestore database
                        DocumentReference postRef = topicRef.collection("posts").document();
                        postRef.set(firstPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "New Post document created");
                            }
                        });

                        // Finishing the activity and starting new
                        Intent intent = new Intent(getApplicationContext(), Forum.class);
                        startActivity(intent);
                        finish();

                    } catch (NullPointerException e) {
                        Log.e(TAG, "onComplete: NullPointerException " + e);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
