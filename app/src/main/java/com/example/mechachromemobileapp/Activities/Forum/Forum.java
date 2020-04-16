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

import com.example.mechachromemobileapp.Adapters.ForumAdapter;
import com.example.mechachromemobileapp.Models.ForumTopic;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 *  Forum Activity
 *
 *  Displays topics data like: topic_name, user,
 *  person who wrote last post, date of last post,
 *  date of creation of topic. It uses RecyclerView
 *  to display topic data
 *
 */
public class Forum extends Activity {

    // Global variables
    public static final String TAG = "Forum";
    private FirebaseFirestore fStore;
    private ForumAdapter forumAdapter;
    private Button addTopic;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        initViews();
        buildRecyclerView();
        setButtons();
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    public void initViews() {
        addTopic = findViewById(R.id.add_topic_button);
        fStore = FirebaseFirestore.getInstance();
        empty = findViewById(R.id.empty);
    }

    /**
     *  This method sets the onClickListeners to buttons
     */
    public void setButtons() {
        // Button to add new topic
        addTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Forum.this, ForumPostTopic.class));
                finish();
            }
        });

        // Attaching OnItemClickListener to forumAdapter, this enables clicking on each forum topic element
        forumAdapter.setOnItemClickListener(new ForumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                try {
                    // Creating instance of ForumTopic class called thisTopic from documentSnapshot object from Firestore
                    ForumTopic thisTopic = documentSnapshot.toObject(ForumTopic.class);
                    // Getting topicID from documentSnapshot
                    String topicID = documentSnapshot.getId();
                    // Asserting that this topic object isn't null
                    assert thisTopic != null;
                    // Saving topic name to string called topic_name
                    String topic_name = thisTopic.getTopic_name();
                    // Creating new intent to open ForumViewTopic activity
                    Intent intent = new Intent(getApplicationContext(), ForumViewTopic.class);
                    // Putting to intent extra String named 'topic_name' that contains String topic_name value
                    intent.putExtra("topic_name", topic_name);
                    // Putting to intent extra String named 'topic_id' that contains String topicID value
                    intent.putExtra("topic_id", topicID);
                    // Starting new activity with the intent
                    startActivity(intent);
                } catch(NullPointerException e) {
                    Log.e(TAG, "onItemClick: NullPointerException " + e.getMessage());
                }
            }
        });

    }
    /**
     * Method for building BookSale RecyclerView
     */
    public void buildRecyclerView() {
        // Getting forum_topics Collection from Firestore
        CollectionReference topicsCollection = fStore.collection("forum_topics");
        // Query forum_topics ordered by date_published
        Query query = topicsCollection.orderBy("date_published", Query.Direction.DESCENDING);

        // Building options for forumAdapter
        FirestoreRecyclerOptions<ForumTopic> options = new FirestoreRecyclerOptions.Builder<ForumTopic>()
                .setQuery(query, ForumTopic.class)
                .build();

        forumAdapter = new ForumAdapter(options);
        // Getting the RecyclerView widget from layout
        RecyclerView recyclerView = findViewById(R.id.forum_recycler_view);
        // Setting RecyclerView to FixedSize this is done for performance
        recyclerView.setHasFixedSize(true);
        // Setting RecyclerView layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // Setting RecyclerView adapter to adapter variable from parameter
        recyclerView.setAdapter(forumAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        forumAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        forumAdapter.stopListening();
    }
}