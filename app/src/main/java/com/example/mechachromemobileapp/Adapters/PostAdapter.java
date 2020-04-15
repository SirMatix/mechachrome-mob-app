package com.example.mechachromemobileapp.Adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Models.ForumPost;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class PostAdapter extends FirestoreRecyclerAdapter<ForumPost, PostAdapter.PostHolder> {

    private OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PostAdapter(@NonNull FirestoreRecyclerOptions<ForumPost> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final PostHolder holder, int position, @NonNull ForumPost model) {
        String author = "Post author: " + model.getAuthor();
        holder.postAuthor.setText(author);
        String content = "Content: \n" + model.getContent();
        holder.postContent.setText(content);
        String date_published = DateFormat.format("dd-MM-yyyy HH:mm:ss", model.getDate_published()).toString();
        String text_date = "Published: " + date_published;
        holder.postDate.setText(text_date);
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_post_item,
                parent, false);
        return new PostHolder(v);
    }

    class PostHolder extends RecyclerView.ViewHolder {
        TextView postAuthor, postDate, postContent;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            postAuthor = itemView.findViewById(R.id.post_author);
            postContent = itemView.findViewById(R.id.post_content);
            postDate = itemView.findViewById(R.id.post_date_published);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}