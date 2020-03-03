package com.example.mechachromemobileapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

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
        holder.reviewDatePublished.setText(currentReview.getDate_published());
        holder.reviewContent.setText(currentReview.getContent());
        holder.reviewScore.setNumStars(currentReview.getScore());

    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }
}
