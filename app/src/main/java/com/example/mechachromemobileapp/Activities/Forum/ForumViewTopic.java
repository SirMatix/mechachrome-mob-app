package com.example.mechachromemobileapp.Activities.Forum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Adapters.PostAdapter;
import com.example.mechachromemobileapp.Models.ForumPost;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ForumViewTopic extends Activity {

    public static final String TAG = "ForumViewTopic";
    private PostAdapter postAdapter;
    private FirebaseFirestore fStore;
    private String topicFeed, topicID;
    private TextView topic;
    private Button reply;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_view_topic);
        initViews();
        buildRecyclerView();
        setButtons();
    }

    private void initViews() {
        // getting button view
        reply = findViewById(R.id.topic_view_reply_button);

        // getting intent from Forum activity and getting extra string
        Intent intent = getIntent();
        topicFeed = intent.getStringExtra("topic_name");
        topicID = intent.getStringExtra("topic_id");
        Log.d(TAG,"topic Id: " + topicID);

        // getting and setting the topic name on top of our layout to topic name
        topic = findViewById(R.id.topic_name);
        topic.setText(topicFeed);

        // getting firebase instance
        fStore = FirebaseFirestore.getInstance();
    }

    public void buildRecyclerView() {
        CollectionReference postCollection = fStore.collection("forum_topics").document(topicID).collection("posts");
        Query query = postCollection.orderBy("date_published", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ForumPost> options = new FirestoreRecyclerOptions.Builder<ForumPost>()
                .setQuery(query, ForumPost.class)
                .build();

        postAdapter = new PostAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.topic_view_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(postAdapter);
    }

    private void setButtons() {
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForumPostReply.class);
                intent.putExtra("topic_name", topicFeed);
                intent.putExtra("topic_id", topicID);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Forum.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        postAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        postAdapter.stopListening();
    }
}
