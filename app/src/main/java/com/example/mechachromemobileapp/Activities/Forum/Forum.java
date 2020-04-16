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

public class Forum extends Activity {

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

    public void initViews() {
        addTopic = findViewById(R.id.add_topic_button);
        fStore = FirebaseFirestore.getInstance();
        empty = findViewById(R.id.empty);
    }

    public void setButtons() {
        addTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Forum.this, ForumPostTopic.class));
                finish();
            }
        });

        forumAdapter.setOnItemClickListener(new ForumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                try {
                    ForumTopic thisTopic = documentSnapshot.toObject(ForumTopic.class);
                    String topicID = documentSnapshot.getId();
                    assert thisTopic != null;
                    String topic_name = thisTopic.getTopic_name();
                    Intent intent = new Intent(getApplicationContext(), ForumViewTopic.class);
                    intent.putExtra("topic_name", topic_name);
                    intent.putExtra("topic_id", topicID);
                    startActivity(intent);
                } catch(NullPointerException e) {
                    Log.e(TAG, "onItemClick: NullPointerException " + e.getMessage());
                }
            }
        });

    }

    public void buildRecyclerView() {
        CollectionReference topicsCollection = fStore.collection("forum_topics");
        Query query = topicsCollection.orderBy("date_published", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ForumTopic> options = new FirestoreRecyclerOptions.Builder<ForumTopic>()
                .setQuery(query, ForumTopic.class)
                .build();

        forumAdapter = new ForumAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.forum_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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

/*


final ArrayList<Map> forumTopics = new ArrayList<>();

final ListAdapter forumAdapter = new ForumAdapter_copy(this, forumTopics);
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





















































 */