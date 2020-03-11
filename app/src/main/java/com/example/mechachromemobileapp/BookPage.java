package com.example.mechachromemobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class BookPage extends AppCompatActivity {

    public static final String TAG = "TAG";
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    FirebaseFirestore fStore;
    String titleAuthorFeed, titleFeed;
    Button reserveButton, writeReviewButton;
    TextView bookTitle, bookAuthor, bookDescription, bookPages, availableBooks, reservedBooks;
    ImageView bookImage;
    RatingBar bookRating;
    DocumentReference bookReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);
        initViews();
        loadBookData();
        buildRecyclerView();

        writeReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeReview();
            }
        });

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveBook();
            }
        });

    }

    public void initViews(){
        fStore = FirebaseFirestore.getInstance();

        // getting intent from Library activity and getting extra string
        Intent intent = getIntent();
        titleAuthorFeed = intent.getStringExtra("title_author");
        titleFeed = intent.getStringExtra("title");

        // finding variables from layout
        bookTitle = findViewById(R.id.item_book_title);
        bookAuthor = findViewById(R.id.item_book_author);
        bookDescription = findViewById(R.id.availableBooks);
        bookPages = findViewById(R.id.item_book_pagesrev);
        availableBooks = findViewById(R.id.numAvailable);
        reservedBooks = findViewById(R.id.numReserved);
        bookImage = findViewById(R.id.item_book_img);
        bookRating = findViewById(R.id.item_book_ratingBar);
        writeReviewButton = findViewById(R.id.writeReviewBtn);
        reserveButton = findViewById(R.id.reserveBookBtn);
        reviewRecyclerView = findViewById(R.id.bookPageRecyclerView);
    }

    public void loadBookData() {
        bookReference = fStore.collection("library_books").document(titleAuthorFeed);
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
                availableBooks.setText(documentSnapshot.get("availableBooksNum").toString());
                reservedBooks.setText((documentSnapshot.get("numReserved").toString()));
                bookRating.setRating(Float.parseFloat(documentSnapshot.get("rating").toString()));

                Glide.with(BookPage.this)
                        .load(documentSnapshot.get("imgUrl").toString()) //set the img book url
                        .transforms(new CenterCrop() , new RoundedCorners(16))
                        .into(bookImage); //destination path
            }
        });
    }

    public void reserveBook() {
        Intent intent = new Intent(BookPage.this, ReserveBook.class);
        intent.putExtra("title_author", titleAuthorFeed);
        intent.putExtra("title", titleFeed);
        startActivity(intent);
    }

    public void writeReview() {
        Intent intent = new Intent(BookPage.this, AddReview.class);
        intent.putExtra("title_author", titleAuthorFeed);
        intent.putExtra("title", titleFeed);
        startActivity(intent);
    }

    public void buildRecyclerView() {
        CollectionReference reviewsReference = fStore.collection("library_book_reviews");
        Query query = reviewsReference.whereEqualTo("book_title", titleFeed).orderBy("date_published", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Review> options = new FirestoreRecyclerOptions.Builder<Review>()
                .setQuery(query, Review.class)
                .build();

        reviewAdapter = new ReviewAdapter(options);
        reviewRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(reviewAdapter);
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
