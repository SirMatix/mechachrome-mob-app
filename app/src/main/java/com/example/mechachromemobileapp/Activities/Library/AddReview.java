package com.example.mechachromemobileapp.Activities.Library;

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

import com.example.mechachromemobileapp.Models.Review;
import com.example.mechachromemobileapp.R;
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
import java.util.Objects;

/**
 *  AddReview acitivty
 *
 *  Used to add a new review object to Firestore with all the necessary data
 *
 */
public class AddReview extends AppCompatActivity {

    // Global variables
    private final static String TAG = "AddReview";
    private Button addReviewBtn, discardReviewBtn;
    private RatingBar reviewScore;
    private TextView editReviewContent;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userID, bookID, titleFeed;
    private Date date_published;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        initViews();
        setButtons();
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    public void initViews() {
        // Getting intent from BookPage activity and getting extra string
        Intent intent = getIntent();
        bookID = intent.getStringExtra("book_id");
        titleFeed = intent.getStringExtra("title");

        // Initialization of widgets from layout
        reviewScore = findViewById(R.id.ratingBar);
        editReviewContent = findViewById(R.id.edit_content);
        addReviewBtn = findViewById(R.id.add_review_button);
        discardReviewBtn = findViewById(R.id.discard_review_button);

        // Instantiating of Firebase widgets
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
    }

    /**
     *  This method sets the onClickListener to buttons
     */
    private void setButtons() {
        // Button for adding a new review
        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReview();
            }
        });

        // Button for discarding review edition
        discardReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finishing the activity and starting previous one
                Intent intent = new Intent(AddReview.this, BookPage.class);
                intent.putExtra("book_id", bookID);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     *  addReview method
     *
     *  Method gets userID from current user and gets the data from layout widgets and vaildates them.
     *  Next method gets document form users Collection and concatenates its fields fname and lname to
     *  create user full_name String, then it saves this String in ArrayList at position 0.
     *  Then method takes bookID we got from previous activity and specific book document
     *  is referenced in library_books Collection, method gets that document and uses
     *  data from it to calculate new rating from a book, then saves some of the book data
     *  into Review object and create a new review document in library_book_reviews collection
     *  after that it sets the Review object to that document
     *
     */
    public void addReview() {
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();


        // Getting data inputted in layouts widgets
        final float rating = reviewScore.getRating();
        final String content = editReviewContent.getText().toString().trim();
        final ArrayList<String> author = new ArrayList<>();
        date_published = Calendar.getInstance().getTime();

        // Fields validation, you can't have empty content or give no rating
        if(TextUtils.isEmpty(content)){
            editReviewContent.setError("You must add content");
            return;
        }
        if(rating == 0) {
            Toast.makeText(AddReview.this,"You must add the rating", Toast.LENGTH_SHORT).show();
            return;
        }

        // Getting the user
        DocumentReference userRef = fStore.collection("users").document(userID);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot user = task.getResult();
                if (task.isSuccessful()) {
                    try {
                        assert user != null;
                        if (user.exists()) {
                            Log.d(TAG, "Got the user " + userID);
                            String full_name = Objects.requireNonNull(user.get("fname")).toString() + " " + Objects.requireNonNull(user.get("lname")).toString();
                            author.add(0, full_name);
                        } else {
                            Log.d(TAG, "No such user");
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG, "onComplete: NullPointerException " + e.getMessage());
                    }

                    final DocumentReference bookRef = fStore.collection("library_books").document(bookID);
                    // Incrementing values of review number and number of ratings
                    bookRef.update("numReviews", FieldValue.increment(1));
                    bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot book = task.getResult();
                                // Calculating new average rating
                                assert book != null;
                                long numReviews = (long) book.get("numReviews");
                                float prev_rating = Float.parseFloat(Objects.requireNonNull(book.get("rating")).toString());
                                float new_rating = (prev_rating + rating)/numReviews;

                                // Updating "rating" in Firestore to new_rating
                                bookRef.update("rating",new_rating);

                                // Setting the review data in Review class instance
                                Review review = new Review();
                                review.setAuthor(author.get(0));
                                review.setAuthor_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                review.setBook_title(titleFeed);
                                review.setBook_id(bookID);
                                review.setContent(content);
                                review.setDate_published(date_published);
                                review.setRating(rating);

                                // Creating new entry in Firestore in library_book_review Collection and setting review object data to it
                                DocumentReference reviewRef = fStore.collection("library_book_reviews").document();
                                reviewRef.set(review).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: new book review document has been created");
                                    }
                                });

                                // Finishing the activity
                                finish();
                            } else {
                                Log.d(TAG, "onSuccess: failed, error message: ", task.getException());
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "onComplete: failed, error message: ", task.getException());
                }
            }
        });
    }
}

