package com.example.mechachromemobileapp.Adapters;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Models.ForumTopic;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * ForumAdapter Class
 *
 * handles displaying forum topics information based on FirebaseUI
 */
public class ForumAdapter extends FirestoreRecyclerAdapter<ForumTopic, ForumAdapter.ForumHolder> {

    public static final String TAG = "ForumAdapter";
    private OnItemClickListener listener;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference topicsCollection = fStore.collection("forum_topics");

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ForumAdapter(@NonNull FirestoreRecyclerOptions<ForumTopic> options) {
        super(options);
    }

    /**
     * method responsible for displaying data on layout
     *
     * @param holder   ---> ForumHolder class that holds layout variables for each element in RecyclerView
     * @param position ---> position of element in RecyclerView
     * @param model    ---> ForumTopic class
     */
    @Override
    protected void onBindViewHolder(@NonNull final ForumHolder holder, int position, @NonNull ForumTopic model) {
        // setting data for each topic from model getter methods
        holder.topicText.setText(model.getTopic_name());
        holder.numReplies.setText(String.valueOf(model.getPost_num()));
        holder.topicAuthor.setText(model.getAuthor());
        // applying 24hr date format
        String date_published = DateFormat.format("dd-MM-yyyy HH:mm:ss", model.getDate_published()).toString();
        holder.topicPublished.setText(date_published);

        // getting an element snapshot to get its id
        DocumentSnapshot topicSnapshot = getSnapshots().getSnapshot(position);
        String topicID = topicSnapshot.getId();

        // referencing posts sub-collection inside forum_topics Collection document with specific ID
        CollectionReference postsCollection = topicsCollection.document(topicID).collection("posts");

        // getting the last element published in posts sub-collection
        postsCollection.orderBy("date_published", Query.Direction.DESCENDING)
                .limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    try{
                        for(QueryDocumentSnapshot post: task.getResult()) {
                            // setting last post author and date in our view with correct date format
                            holder.lastPostAuthor.setText(post.getString("author"));
                            String date_published = (String) DateFormat.format("dd-MM-yyyy HH:mm:ss", post.getDate("date_published"));
                            holder.lastPostPublished.setText(date_published);
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG,"onComplete: NullPointerException " + e.getMessage());
                    }

                }
            }
        });

    }

    /**
     * this method inflates custom layout for each element in recycler view
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ForumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // here we define which layout we want to use for each of our recyclerView elements
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_forum_item,
                parent, false);
        return new ForumHolder(v);
    }

    /**
     * This class holds variables from custom layout
     */
    class ForumHolder extends RecyclerView.ViewHolder {
        // class variables
        TextView topicText, topicAuthor, topicPublished, numReplies, lastPostAuthor, lastPostPublished;

        public ForumHolder(@NonNull View itemView) {
            super(itemView);
            // identifying layout elements and saving them to class variables
            topicText = itemView.findViewById(R.id.forum_topic);
            topicAuthor = itemView.findViewById(R.id.topic_author);
            topicPublished = itemView.findViewById(R.id.post_date_published);
            numReplies = itemView.findViewById(R.id.num_replies);
            lastPostAuthor = itemView.findViewById(R.id.last_post_author);
            lastPostPublished = itemView.findViewById(R.id.last_post_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
