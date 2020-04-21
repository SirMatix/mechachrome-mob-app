package com.example.mechachromemobileapp.Activities.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Activities.Library.BookPage;
import com.example.mechachromemobileapp.Adapters.ReviewAdapter;
import com.example.mechachromemobileapp.Models.Review;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * UserBookReviews activity
 *
 * Displays all the reviews written by a user
 */
public class UserBookReviews extends AppCompatActivity {

    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_book_reviews);
        initViews();
        buildBooksRecyclerView(reviewAdapter);
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    private void initViews() {
        // get data for previous activity
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        // initialize adapter
        reviewAdapter = getAdapter(userID);
    }

    /**
     *
     * @param userID
     * @return
     */
    private ReviewAdapter getAdapter(String userID) {
        CollectionReference reviewsCollection = FirebaseFirestore.getInstance().collection("library_book_reviews");
        Query query = reviewsCollection.whereEqualTo("author_id", userID).orderBy("date_published", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Review> options = new FirestoreRecyclerOptions.Builder<Review>()
                .setQuery(query, Review.class)
                .build();
        return new ReviewAdapter(options);
    }

    /**
     *
     * @param adapter
     */
    private void buildBooksRecyclerView(ReviewAdapter adapter) {
        RecyclerView reviewRecyclerView = findViewById(R.id.user_book_review_recyclerview);
        reviewRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Review review = documentSnapshot.toObject(Review.class);
                String book_id = review.getBook_id();
                String book_title = review.getBook_title();
                Intent intent = new Intent(getApplicationContext(), BookPage.class);
                intent.putExtra("book_id", book_id);
                intent.putExtra("book_title", book_title);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        reviewAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        reviewAdapter.stopListening();
    }
}
