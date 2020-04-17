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

/**
 * Activity for displaying a specific topic posts
 *
 * It uses recyclerview to display posts from
 * forum_topic document's collection posts
 *
 */
public class ForumViewTopic extends Activity {

    // Global variables
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

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    private void initViews() {
        // Initialization of Button widgets from layout
        reply = findViewById(R.id.topic_view_reply_button);

        // Getting intent from Forum activity and getting extra string
        Intent intent = getIntent();
        topicFeed = intent.getStringExtra("topic_name");
        topicID = intent.getStringExtra("topic_id");
        Log.d(TAG,"topic Id: " + topicID);

        // Getting and setting the topic name on top of our layout to topic name
        topic = findViewById(R.id.topic_name);
        topic.setText(topicFeed);

        // Getting firebase instance
        fStore = FirebaseFirestore.getInstance();
    }

    /**
     * Method for building BookSale RecyclerView
     *
     * RecyclerView is a view that displays data from Firestore in a form of a list.
     * In this case it displays data from forum_topics document Collection posts
     */
    public void buildRecyclerView() {
        // This collection references posts collection a subcollection that belongs to specific forun_topics document
        CollectionReference postCollection = fStore.collection("forum_topics").document(topicID).collection("posts");
        // Query of Posts Collection sorted by published date
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

    /**
     *  This method sets the onClickListeners to buttons
     */
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

    /**
     *  Activity for handling pressing back button
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Forum.class);
        startActivity(intent);
        finish();
    }

    /**
     * Activity from handling start of activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        postAdapter.startListening();
    }

    /**
     * Activity from handling a stop of activity
     */
    @Override
    protected void onStop() {
        super.onStop();
        postAdapter.stopListening();
    }
}
