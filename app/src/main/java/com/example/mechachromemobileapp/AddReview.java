package com.example.mechachromemobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddReview extends AppCompatActivity {

    private String TAG = "TAG";
    Button addReviewBtn, discardReviewBtn;
    RatingBar reviewScore;
    TextView editReviewContent;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID, titleAuthorFeed, titleFeed;
    Date date_published;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        initViews();
        addReview();

    }

    public void initViews() {
        // getting data from previous activity
        Intent intent = getIntent();
        titleAuthorFeed = intent.getStringExtra("title_author");
        titleFeed = intent.getStringExtra("title");

        // getting items from layout
        reviewScore = findViewById(R.id.ratingBar);
        editReviewContent = findViewById(R.id.edit_content);
        addReviewBtn = findViewById(R.id.add_review_button);
        discardReviewBtn = findViewById(R.id.discard_review_button);

        // getting Firebase instances
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

    }

    public void addReview() {
        userID = fAuth.getCurrentUser().getUid();

        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final float rating = reviewScore.getRating();
                final String content = editReviewContent.getText().toString().trim();
                final ArrayList<String> author = new ArrayList<>();
                date_published = Calendar.getInstance().getTime();

                if(TextUtils.isEmpty(content)){
                    editReviewContent.setError("You must add content");
                    return;
                }
                if(rating == 0) {
                    Toast.makeText(AddReview.this,"You must add the rating", Toast.LENGTH_SHORT).show();
                    return;
                }

                // getting the user
                DocumentReference userRef = fStore.collection("users").document(userID);
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot user = task.getResult();
                        if (task.isSuccessful()) {
                            if (user.exists()) {
                                Log.d(TAG, "Got the user " + userID);
                                String author_temp = user.get("fname").toString() + " " + user.get("lname").toString();
                                author.add(0, author_temp);
                            } else {
                                Log.d(TAG, "No such user");
                            }

                            final DocumentReference bookRef = fStore.collection("library_books").document(titleAuthorFeed);
                            // incrementing values of review number and number of ratings
                            bookRef.update("numReviews", FieldValue.increment(1));
                            bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot book = task.getResult();
                                        // calculating new average rating
                                        long numReviews = (long) book.get("numReviews");
                                        float prev_rating = Float.parseFloat(book.get("rating").toString());
                                        float new_rating = (prev_rating + rating)/numReviews;

                                        // updating new rating
                                        bookRef.update("rating",new_rating);

                                        // setting the review
                                        DocumentReference reviewRef = fStore.collection("library_book_reviews").document();
                                        Review review = new Review(author.get(0),titleFeed,date_published,content,rating);

                                        reviewRef.set(review).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "New Review document created");
                                            }
                                        });

                                        // finishing the activity
                                        finish();
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });

        discardReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finishing the activity and starting previous one
                Intent intent = new Intent(AddReview.this, BookPage.class);
                intent.putExtra("title_author", titleAuthorFeed);
                startActivity(intent);
                finish();
            }
        });
    }
}

