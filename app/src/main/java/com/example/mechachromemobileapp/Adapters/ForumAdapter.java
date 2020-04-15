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

    @Override
    protected void onBindViewHolder(@NonNull final ForumHolder holder, int position, @NonNull ForumTopic model) {
        holder.topicText.setText(model.getTopic_name());
        holder.numReplies.setText(String.valueOf(model.getPost_num()));
        holder.topicAuthor.setText(model.getAuthor());
        String date_published = DateFormat.format("dd-MM-yyyy HH:mm:ss", model.getDate_published()).toString();
        holder.topicPublished.setText(date_published);

        DocumentSnapshot topicSnapshot = getSnapshots().getSnapshot(position);
        String topicID = topicSnapshot.getId();
        CollectionReference postsCollection = topicsCollection.document(topicID).collection("posts");

        postsCollection.orderBy("date_published", Query.Direction.DESCENDING)
                .limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    try{
                        for(QueryDocumentSnapshot post: task.getResult()) {
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

    @NonNull
    @Override
    public ForumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_forum_item,
                parent, false);
        return new ForumHolder(v);
    }

    class ForumHolder extends RecyclerView.ViewHolder {
        TextView topicText, topicAuthor, topicPublished, numReplies, lastPostAuthor, lastPostPublished;

        public ForumHolder(@NonNull View itemView) {
            super(itemView);
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
