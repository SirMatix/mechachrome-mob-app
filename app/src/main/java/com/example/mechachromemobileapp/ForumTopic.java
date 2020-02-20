package com.example.mechachromemobileapp;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ForumTopic extends Activity {

    public static final String TAG = "TAG";
    FirebaseFirestore db;
    String topicFeed;
    TextView topic;
    Button reply;
    //ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_topic);

        // getting button view
        reply = findViewById(R.id.replyButton);

        // getting intent from Forum activity and getting extra string
        Intent intent = getIntent();
        topicFeed = intent.getStringExtra("topic");

        // getting and setting the topic name on top of our layout to topic name
        topic = findViewById(R.id.topic);
        topic.setText(topicFeed);

        // getting firebase instance
        db = FirebaseFirestore.getInstance();

        //progressBar.findViewById(R.layout.custom_forum_list);
        // setting ArrayList which will contain Map objects for handling Post data
        final ArrayList<Map> forumPosts = new ArrayList<>();

        final ListAdapter postAdapter = new PostAdapter(this, forumPosts);
        ListView forumTopicListView =  findViewById(R.id.ForumTopicListView);
        forumTopicListView.setAdapter(postAdapter);

        //progressBar.setVisibility(View.VISIBLE);
        db.collection("forum_posts")
                .whereEqualTo("topic", topicFeed)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> postData = new HashMap<>();
                                postData.put("content",document.getString("content"));
                                postData.put("author",document.getString("author"));
                                postData.put("date_published",document.get("date_published").toString());
                                forumPosts.add(postData);
                                Log.d(TAG, "Got the topic with name: " + document.get("topic_name"));
                            }
                            ((ArrayAdapter) postAdapter).notifyDataSetChanged();
                            //progressBar.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}