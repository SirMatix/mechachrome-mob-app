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

public class ReviewAdapter extends FirestoreRecyclerAdapter<Review, ReviewAdapter.ReviewHolder> {


    public ReviewAdapter(@NonNull FirestoreRecyclerOptions<Review> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReviewHolder holder, int position, @NonNull Review model) {
        holder.reviewAuthor.setText(model.getAuthor());
        CharSequence date_published = DateFormat.format("dd-MM-yyyy HH:mm:ss",model.getDate_published());
        holder.reviewDatePublished.setText(date_published.toString());
        holder.reviewContent.setText(model.getContent());
        holder.reviewScore.setRating(model.getRating());
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_review_item, parent, false);
        return new ReviewHolder(view);
    }

    class ReviewHolder extends RecyclerView.ViewHolder {
        public TextView reviewAuthor, reviewDatePublished, reviewContent;
        public RatingBar reviewScore;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            reviewAuthor = itemView.findViewById(R.id.post_author);
            reviewDatePublished = itemView.findViewById(R.id.post_date_published);
            reviewContent = itemView.findViewById(R.id.content);
            reviewScore = itemView.findViewById(R.id.review_score);
        }
    }
}


    /*
    private ArrayList<Review> reviewsList;

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        public TextView reviewAuthor, reviewDatePublished, reviewContent;
        public RatingBar reviewScore;
        //public DividerItemDecoration divider;


        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewAuthor = itemView.findViewById(R.id.author);
            reviewDatePublished = itemView.findViewById(R.id.date_published);
            reviewContent = itemView.findViewById(R.id.content);
            reviewScore = itemView.findViewById(R.id.review_score);
        }
    }

    public ReviewAdapter(ArrayList<Review> reviews) {
        ArrayList<Review> reviewsList = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_review_item, parent, false);
        ReviewViewHolder reviewViewHolder = new ReviewViewHolder(view);
        return reviewViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review currentReview = reviewsList.get(position);

        holder.reviewAuthor.setText(currentReview.getAuthor());
        CharSequence date_published = DateFormat.format("dd-MM-yyyy hh:mm:ss",currentReview.getDate_published());
        holder.reviewDatePublished.setText(date_published.toString());
        holder.reviewContent.setText(currentReview.getContent());
        holder.reviewScore.setRating(currentReview.getRating());

    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Object model) {

    }
}

     */
