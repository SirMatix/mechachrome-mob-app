package com.example.mechachromemobileapp;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Forum extends Activity {

    public static final String TAG = "TAG";
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        db = FirebaseFirestore.getInstance();
        final ArrayList<String> forumTopics = new ArrayList<>();

        final ListAdapter forumAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, forumTopics);
        ListView forumListView =  findViewById(R.id.ForumListView);
        forumListView.setAdapter(forumAdapter);

        db.collection("forum_topics")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            int count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                forumTopics.add(count, document.getString("topic_name"));
                                count += 1;
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
                        String topic = String.valueOf(parent.getItemAtPosition(position));
                        Toast.makeText(Forum.this, topic, Toast.LENGTH_LONG).show();
                    }
                }
        );


    }
}
