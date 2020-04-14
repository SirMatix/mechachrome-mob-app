package com.example.mechachromemobileapp.Activities.Library;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mechachromemobileapp.Models.Books;
import com.example.mechachromemobileapp.R;
import com.example.mechachromemobileapp.Models.Reservation;
import com.example.mechachromemobileapp.Models.Review;
import com.example.mechachromemobileapp.Adapters.ReviewAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

public class BookPage extends AppCompatActivity {

    public static final String TAG = "TAG";
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String bookIDFeed, titleFeed;
    Button reserveButton, writeReviewButton, cancelButton;
    TextView bookTitle, bookAuthor, bookDescription, bookPages, availableBooks, reservedBooks, bookISBN;
    ImageView bookImage;
    RatingBar bookRating;
    CollectionReference booksCollection, reservationCollection, usersCollection;
    DocumentReference bookReference, reservationReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);

        initViews();
        buildReviewsRecyclerView();

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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelReservation();
            }
        });

    }

    public void initViews(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        booksCollection = fStore.collection("library_books");
        reservationCollection = fStore.collection(("library_books_reservations"));
        usersCollection = fStore.collection("users");

        // getting intent from Library activity and getting extra string
        Intent intent = getIntent();
        bookIDFeed = intent.getStringExtra("book_id");
        titleFeed = intent.getStringExtra("book_title");

        // finding variables from layout
        bookTitle = findViewById(R.id.item_book_title);
        bookAuthor = findViewById(R.id.item_book_author);
        bookDescription = findViewById(R.id.bookDescription);
        bookPages = findViewById(R.id.item_book_pagesrev);
        bookISBN = findViewById(R.id.item_book_isbn);
        availableBooks = findViewById(R.id.numAvailable);
        reservedBooks = findViewById(R.id.numReserved);
        bookImage = findViewById(R.id.item_book_img);
        bookRating = findViewById(R.id.item_book_ratingBar);
        writeReviewButton = findViewById(R.id.writeReviewBtn);
        reserveButton = findViewById(R.id.reserveBookBtn);
        cancelButton = findViewById(R.id.cancelBookBtn);
        reviewRecyclerView = findViewById(R.id.bookPageRecyclerView);
    }

    public void loadBookData() {
        bookReference = booksCollection.document(bookIDFeed);
        bookReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Books book = documentSnapshot.toObject(Books.class);
                assert book != null;
                String title = "Title: " + book.getTitle();
                bookTitle.setText(title);
                String author = "Author: " + book.getAuthor();
                bookAuthor.setText(author);
                bookDescription.setText(book.getDescription());
                bookDescription.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                String pages = book.getPages().toString();
                String numreviews = book.getNumReviews().toString();
                String pagesrev = pages + " Pages | " + numreviews + " reviews";
                bookPages.setText(pagesrev);
                String available = "Available books: " + String.valueOf(book.getAvailableBooksNum());
                availableBooks.setText(available);
                String reserved = "Reserved books: " + String.valueOf(book.getNumReserved());
                reservedBooks.setText(reserved);
                bookRating.setRating(book.getRating());
                String isbn = "ISBN: " + book.getISBN();
                bookISBN.setText(isbn);


                Glide.with(BookPage.this)
                        .load(documentSnapshot.get("imgUrl").toString()) //set the img book url
                        .transforms(new CenterCrop() , new RoundedCorners(16))
                        .into(bookImage); //destination path
            }
        });
    }

    public void reserveBook() {
        final Date today = Calendar.getInstance().getTime();
        reservationCollection
                .whereEqualTo("book_title", titleFeed)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference doc = document.getReference();
                                Reservation reservation = document.toObject(Reservation.class);
                                document.getId();
                                if (reservation.getReserved_to() == today) {
                                    bookReference.update("availableBooksNum", FieldValue.increment(1));
                                    bookReference.update("numReserved", FieldValue.increment(-1));
                                    doc.update("is_active",false);
                                    doc.update("is_done", true);
                                }
                            }
                        }
                    }
                });

        bookReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Books book = documentSnapshot.toObject(Books.class);
                if(book.getAvailableBooksNum() > 0) {
                    Intent intent = new Intent(BookPage.this, ReserveBook.class);
                    intent.putExtra("title_author", bookIDFeed);
                    intent.putExtra("title", titleFeed);
                    startActivity(intent);
                } else {
                    reserveButton.setEnabled(false);
                    Toast.makeText(BookPage.this,"All books are reserved" , Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void cancelReservation() {
        final String userID = fAuth.getCurrentUser().getUid();
        DocumentReference userReference = usersCollection.document(userID);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    reservationCollection
                            .whereEqualTo("book_title",titleFeed)
                            .whereEqualTo("user_reserver_id", userID)
                            .whereEqualTo("is_active", true)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for(QueryDocumentSnapshot document: task.getResult()) {
                                        reservationReference = document.getReference();
                                        //String resID = reservationReference.getId();
                                        reservationReference.update("is_active", false);
                                        reservationReference.update("is_cancelled", true);
                                        reservationReference.update("is_done",true);
                                        bookReference.update("availableBooksNum", FieldValue.increment(1));
                                        bookReference.update("numReserved", FieldValue.increment(-1));
                                        loadBookData();
                                        Toast.makeText(BookPage.this,"Reservation cancelled" , Toast.LENGTH_SHORT).show();
                                    }
                                    reserveButton.setVisibility(View.VISIBLE);
                                    cancelButton.setVisibility(View.INVISIBLE);
                                }

                            });
                }
            }
        });
    }

    public void checkUserReserved() {
        final String userID = fAuth.getCurrentUser().getUid();
        DocumentReference userReference = usersCollection.document(userID);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    reservationCollection
                            .whereEqualTo("book_title",titleFeed)
                            .whereEqualTo("user_reserver_id", userID)
                            .whereEqualTo("is_active", true)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for(QueryDocumentSnapshot document: task.getResult()) {
                                        Log.d(TAG, "User has book reserved");
                                        reserveButton.setVisibility(View.INVISIBLE);
                                        cancelButton.setVisibility(View.VISIBLE);
                                    }
                                }

                            });
                }
            }
        });
    }

    public void writeReview() {
        Intent intent = new Intent(BookPage.this, AddReview.class);
        intent.putExtra("title_author", bookIDFeed);
        intent.putExtra("title", titleFeed);
        startActivity(intent);
    }

    public void buildReviewsRecyclerView() {
        CollectionReference reviewsCollection = fStore.collection("library_book_reviews");
        Query query = reviewsCollection.whereEqualTo("book_title", titleFeed).orderBy("date_published", Query.Direction.DESCENDING);

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
        checkUserReserved();
        loadBookData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        reviewAdapter.stopListening();
    }
}
