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
    FirebaseFirestore fStore;
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
        ListView forumListView =  findViewById(R.id.ForumListView);
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
                                Map<String,Object> topicData = new HashMap<>();
                                topicData.put("topic_name",document.getId());
                                topicData.put("author",document.getString("author"));

                                Date date_published = document.getTimestamp("date_published").toDate();
                                topicData.put("date_published",DateFormat.format("dd-MM-yyyy hh:mm:ss",date_published).toString());

                                topicData.put("post_num",document.get("post_num").toString());
                                forumTopics.add(topicData);
                                Log.d(TAG, "Got the topic with name: " + document.get("topic_name"));
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
                startActivity(new Intent(Forum.this,ForumPostTopic.class));
            }
        });

    }
}
