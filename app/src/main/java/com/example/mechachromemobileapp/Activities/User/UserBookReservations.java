package com.example.mechachromemobileapp.Activities.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Adapters.ReviewAdapter;
import com.example.mechachromemobileapp.Models.Review;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserBookReservations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_book_reservations);
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    private void initViews() {
        // get data for previous activity
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        // initialize adapter
        //reviewAdapter = getAdapter(userID);

    }

    private ReviewAdapter getAdapter(String userID) {
        CollectionReference reviewsCollection = FirebaseFirestore.getInstance().collection("library_book_reviews");
        Query query = reviewsCollection.whereEqualTo("author_id", userID).orderBy("date_published", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Review> options = new FirestoreRecyclerOptions.Builder<Review>()
                .setQuery(query, Review.class)
                .build();
        return new ReviewAdapter(options);
    }

    private void buildBooksRecyclerView(ReviewAdapter adapter) {
        RecyclerView reviewRecyclerView = findViewById(R.id.user_book_review_recyclerview);
        reviewRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //reviewAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //reviewAdapter.stopListening();
    }
}
