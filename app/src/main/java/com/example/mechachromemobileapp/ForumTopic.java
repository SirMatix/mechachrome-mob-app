package com.example.mechachromemobileapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ForumTopic extends Activity {

    public static final String TAG = "TAG";
    FirebaseFirestore fStore;
    String topicFeed;
    TextView topic;
    Button reply;
    Calendar cal;
    //ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_topic);

        // getting button view
        reply = findViewById(R.id.replyButton);

        // getting intent from Forum activity and getting extra string
        Intent intent = getIntent();
        topicFeed = intent.getStringExtra("topic_name");

        // getting and setting the topic name on top of our layout to topic name
        topic = findViewById(R.id.topic);
        topic.setText(topicFeed);

        // getting firebase instance
        fStore = FirebaseFirestore.getInstance();

        // getting calendar instance
        cal = Calendar.getInstance();

        //progressBar.findViewById(R.layout.custom_forum_list);
        // setting ArrayList which will contain Map objects for handling Post data
        final ArrayList<Map> forumPosts = new ArrayList<>();

        final ListAdapter postAdapter = new PostAdapter(this, forumPosts);
        ListView forumTopicListView =  findViewById(R.id.ForumTopicListView);
        forumTopicListView.setAdapter(postAdapter);

        //progressBar.setVisibility(View.VISIBLE);
        fStore.collection("forum_posts")
                .whereEqualTo("topic_name", topicFeed)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> postData = new HashMap<>();
                                postData.put("content",document.getString("content"));
                                postData.put("author",document.getString("author"));

                                Date date_published = document.getTimestamp("date_published").toDate();
                                postData.put("date_published", DateFormat.format("dd-MM-yyyy hh:mm:ss",date_published).toString());

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

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForumTopic.this, ForumPostReply.class);
                intent.putExtra("topic_name", topicFeed);
                startActivity(intent);
            }
        });
    }
}
