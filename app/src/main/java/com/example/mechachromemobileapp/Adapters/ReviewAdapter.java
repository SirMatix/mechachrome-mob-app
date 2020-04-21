package com.example.mechachromemobileapp.Adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Models.Review;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * ReviewAdapter Class
 *
 * handles displaying review information based on FirebaseUI
 */
public class ReviewAdapter extends FirestoreRecyclerAdapter<Review, ReviewAdapter.ReviewHolder> {

    private OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ReviewAdapter(@NonNull FirestoreRecyclerOptions<Review> options) {
        super(options);
    }

    /**
     * method responsible for displaying data on layout
     *
     * @param holder   ---> ReviewHolder class that holds layout variables for each element in RecyclerView
     * @param position ---> position of element in RecyclerView
     * @param model    ---> Review class
     */
    @Override
    protected void onBindViewHolder(@NonNull ReviewHolder holder, int position, @NonNull Review model) {
        holder.reviewAuthor.setText(model.getAuthor());
        CharSequence date_published = DateFormat.format("dd-MM-yyyy HH:mm:ss",model.getDate_published());
        holder.reviewDatePublished.setText(date_published.toString());
        holder.reviewContent.setText(model.getContent());
        holder.reviewScore.setRating(model.getRating());
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
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_review_item, parent, false);
        return new ReviewHolder(view);
    }

    /**
     * This class holds variables from custom layout
     */
    class ReviewHolder extends RecyclerView.ViewHolder {
        // class variables
        private TextView reviewAuthor, reviewDatePublished, reviewContent;
        private RatingBar reviewScore;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            // identifying layout elements and saving them to class variables
            reviewAuthor = itemView.findViewById(R.id.post_author);
            reviewDatePublished = itemView.findViewById(R.id.post_date_published);
            reviewContent = itemView.findViewById(R.id.content);
            reviewScore = itemView.findViewById(R.id.review_score);

            // setting onClickListener to make possible clicking on each element in RecyclerView
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
