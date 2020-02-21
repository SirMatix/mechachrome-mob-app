package com.example.mechachromemobileapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Forum extends Activity {

    public static final String TAG = "TAG";
    FirebaseFirestore fStore;
    Button addTopic;
    //ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        addTopic = findViewById(R.id.addNewTopicBtn);
        fStore = FirebaseFirestore.getInstance();

        //progressBar.findViewById(R.layout.custom_forum_list);


        final ArrayList<Map> forumTopics = new ArrayList<>();

        final ListAdapter forumAdapter = new ForumAdapter(this, forumTopics);
        ListView forumListView =  findViewById(R.id.ForumListView);
        forumListView.setAdapter(forumAdapter);

        //progressBar.setVisibility(View.VISIBLE);
        fStore.collection("forum_topics")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> topicData = new HashMap<>();
                                topicData.put("topic_name",document.getString("topic_name"));
                                if(document.getString("author") == null){
                                    topicData.put("author", "no author");
                                } else                                {
                                    topicData.put("author",document.getString("author"));
                                }
                                topicData.put("date_published",document.get("date_published").toString());
                                forumTopics.add(topicData);
                                Log.d(TAG, "Got the topic with name: " + document.get("topic_name"));
                            }
                            ((ArrayAdapter) forumAdapter).notifyDataSetChanged();
                            //progressBar.setVisibility(View.GONE);
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
