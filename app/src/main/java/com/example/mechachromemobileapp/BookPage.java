package com.example.mechachromemobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BookPage extends AppCompatActivity {

    public static final String TAG = "TAG";
    ArrayList<Review> reviewsList;
    private RecyclerView reviewRecyclerView;
    private RecyclerView.Adapter reviewAdapter;
    private RecyclerView.LayoutManager reviewLayoutManager;
    FirebaseFirestore fStore;
    String titleAuthorFeed;
    Button reserveButton, writeReviewButton;
    TextView bookTitle, bookAuthor, bookDescription, bookPages, availableBooks, reservedBooks;
    ImageView bookImage;
    RatingBar bookScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);
        initViews();
        loadBookData();


    }

    public void initViews(){
        fStore = FirebaseFirestore.getInstance();

        // getting intent from Library activity and getting extra string
        Intent intent = getIntent();
        titleAuthorFeed = intent.getStringExtra("title_author");

        // finding variables from layout
        bookTitle = findViewById(R.id.item_book_title);
        bookAuthor = findViewById(R.id.item_book_author);
        bookDescription = findViewById(R.id.item_book_description);
        bookPages = findViewById(R.id.item_book_pagesrev);
        availableBooks = findViewById(R.id.numAvailable);
        reservedBooks = findViewById(R.id.numReserved);
        bookImage = findViewById(R.id.item_book_img);
    }

    public void loadBookData() {
        DocumentReference bookReference = fStore.collection("library_books").document(titleAuthorFeed);
        bookReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                bookTitle.setText(documentSnapshot.getString("title"));
                bookAuthor.setText(documentSnapshot.getString("author"));
                bookDescription.setText(documentSnapshot.getString("description"));
                String pages = documentSnapshot.get("pages").toString();
                String numreviews = documentSnapshot.get("numReviews").toString();
                String pagesrev = pages + " Pages | " + numreviews + " reviews";
                bookPages.setText(pagesrev);
                // availableBooks.setText(documentSnapshot.get("availableBooks").toString();
                // reservedBooks.setText(documentSnapshot.get("numReserved").toString();
                Glide.with(BookPage.this)
                        .load(documentSnapshot.get("imgUrl").toString()) //set the img book url
                        .transforms(new CenterCrop() , new RoundedCorners(16))
                        .into(bookImage); //destination path
            }
        });


    }

    public void loadReviews() {
        reviewsList = new ArrayList<>();
        reviewsList.add(new Review("John Doe","12-02-2020", "This book is super cool", 5));
        reviewsList.add(new Review("Mark Twain","15-02-2020", "This book is everything but cool", 1));
        reviewsList.add(new Review("Agatha Christie","12-02-2020", "This book is super cool", 5));

        buildRecyclerView();

        /*
        fStore.collection("bookReviews")
                .whereEqualTo("book_title", titleFeed)
                .orderBy("date_published", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //int count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Review review = new Review;
                                review.setContent(document.getString("content"));
                                review.setAuthor(document.getString("author"));
                                Date date_published = document.getTimestamp("date_published").toDate();
                                review.setDate_published(DateFormat.format("dd-MM-yyyy hh:mm:ss",date_published).toString());
                                reviewsList.add(review);

                                Log.d(TAG, "Got the book titied: " + document.get("book_title"));
                            }
                            (reviewAdapter).notifyDataSetChanged();
                            //progressBar.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

         */


    }

    public void reserveBook() {


    }

    public void writeReview() {


    }

    public void buildRecyclerView() {
        reviewRecyclerView.findViewById(R.id.bookPageRecyclerView);
        reviewRecyclerView.setHasFixedSize(true);
        reviewLayoutManager = new LinearLayoutManager(this);
        reviewAdapter = new ReviewAdapter(reviewsList);

        reviewRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewRecyclerView.setAdapter(reviewAdapter);
    }
}
