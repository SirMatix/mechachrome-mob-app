package com.example.mechachromemobileapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Forum extends Activity {

    public static final String TAG = "TAG";
    FirebaseFirestore fStore, fStore2;
    Button addTopic;
    TextView empty;
    //ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forum);
        addTopic = findViewById(R.id.addNewTopicBtn);
        fStore = FirebaseFirestore.getInstance();
        empty = findViewById(R.id.empty);


        final ArrayList<Map> forumTopics = new ArrayList<>();

        final ListAdapter forumAdapter = new ForumAdapter(this, forumTopics);
        ListView forumListView = findViewById(R.id.ForumListView);
        forumListView.setEmptyView(empty);
        forumListView.setAdapter(forumAdapter);


        fStore.collection("forum_topics")
                .orderBy("date_published", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final Map<String, Object> topicData = new HashMap<>();
                                // getting topic name and putting it to the topicData HashMap
                                topicData.put("topic_name", document.getId());
                                // getting topic author and putting it to the topicData HashMap
                                topicData.put("author", document.getString("author"));
                                // getting the date_published timestamp
                                Date date_published = document.getTimestamp("date_published").toDate();
                                // formatting the date to the desired 24hr format and putting it into the HashMap
                                topicData.put("date_published", DateFormat.format("dd-MM-yyyy hh:mm:ss", date_published).toString());
                                // getting the post_num and putting it to the topicData HashMap
                                topicData.put("post_num", document.get("post_num").toString());
                                Log.d(TAG, "Got the topic with name: " + document.getId());

                                fStore.collection("forum_posts")
                                        .whereEqualTo("topic_name", document.getId())
                                        .orderBy("date_published", Query.Direction.DESCENDING)
                                        .limit(1)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot post : task.getResult()) {
                                                        Log.d(TAG, "got the last author " + post.getString("author"));
                                                        topicData.put("last_post_author", post.getString("author"));
                                                        Date date_published = post.getTimestamp("date_published").toDate();
                                                        String last_date_published = DateFormat.format("dd-MM-yyyy hh:mm:ss", date_published).toString();
                                                        topicData.put("last_post_published", last_date_published);
                                                        Log.d(TAG, "got the last post " + last_date_published);
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                                // notifying the forumAdapter with data change
                                                ((ArrayAdapter) forumAdapter).notifyDataSetChanged();
                                            }
                                        });

                                forumTopics.add(topicData);
                            }

                            ((ArrayAdapter) forumAdapter).notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }

                });


        forumListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Map topicData = (Map) parent.getItemAtPosition(position);
                        String topic = (String) topicData.get("topic_name");
                        Intent intent = new Intent(Forum.this, ForumTopic.class);
                        intent.putExtra("topic_name", topic);
                        startActivity(intent);
                        finish();
                    }
                }
        );

        addTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Forum.this, ForumPostTopic.class));
                finish();
            }
        });

    }

}
