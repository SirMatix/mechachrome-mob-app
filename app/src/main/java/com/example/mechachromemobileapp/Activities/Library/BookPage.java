package com.example.mechachromemobileapp.Activities.Library;

import android.content.Intent;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mechachromemobileapp.Adapters.ReviewAdapter;
import com.example.mechachromemobileapp.Models.Books;
import com.example.mechachromemobileapp.Models.Reservation;
import com.example.mechachromemobileapp.Models.Review;
import com.example.mechachromemobileapp.R;
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
import java.util.Objects;

/**
 * BookPage activity
 *
 * This activity displays all the information about
 * a book from library_books collection from Firestore
 *
 */
public class BookPage extends AppCompatActivity {

    // Global variables
    private static final String TAG = "BookPage";
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String bookIDFeed, titleFeed;
    private Button reserveButton, writeReviewButton, cancelButton;
    private TextView bookTitle, bookAuthor, bookDescription, bookPages, availableBooks, reservedBooks, bookISBN;
    private ImageView bookImage;
    private RatingBar bookRating;
    private CollectionReference booksCollection, reservationCollection, usersCollection;
    private DocumentReference bookReference, reservationReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);
        initViews();
        buildReviewsRecyclerView();
        setButtons();
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    public void initViews(){
        // Instantiating of Firebase widgets
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        booksCollection = fStore.collection("library_books");
        reservationCollection = fStore.collection("library_books_reservations");
        usersCollection = fStore.collection("users");

        // Getting intent from Library activity and getting extra string
        Intent intent = getIntent();
        bookIDFeed = intent.getStringExtra("book_id");
        titleFeed = intent.getStringExtra("book_title");

        // Initialization widgets from layout
        bookTitle = findViewById(R.id.item_book_title);
        bookAuthor = findViewById(R.id.item_book_author);
        bookDescription = findViewById(R.id.numberOfBooks);
        bookPages = findViewById(R.id.item_book_pagesrev);
        bookISBN = findViewById(R.id.item_book_isbn);
        availableBooks = findViewById(R.id.numAvailable);
        reservedBooks = findViewById(R.id.numReserved);
        bookImage = findViewById(R.id.item_book_img);
        bookRating = findViewById(R.id.item_book_ratingBar);
        writeReviewButton = findViewById(R.id.writeReviewBtn);
        reserveButton = findViewById(R.id.reserveBookBtn);
        cancelButton = findViewById(R.id.cancelBookBtn);
    }

    /**
     *  This method sets the onClickListener to buttons
     */
    public void setButtons() {
        // Button to write review
        writeReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeReview();
            }
        });
        // Button to reserve a book
        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveBook();
            }
        });
        // Button to cancel reservation
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelReservation();
            }
        });
    }

    /**
     *  Method to load book data from Firestore database
     */
    public void loadBookData() {
        // Referencing specific book in Firestore
        bookReference = booksCollection.document(bookIDFeed);
        // Getting the book
        bookReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Making Book object from documentSnapshot
                Books book = documentSnapshot.toObject(Books.class);
                assert book != null;
                // Getting book data from Book object and Setting it to Layout widgets
                // Getting title
                String title = "Title: " + book.getTitle();
                // Setting title
                bookTitle.setText(title);
                // Getting author
                String author = "Author: " + book.getAuthor();
                // Setting author
                bookAuthor.setText(author);
                // Getting and setting book description and justifying text
                bookDescription.setText(book.getDescription());
                bookDescription.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                // Getting book pages and reviews number and creating one String variable pagesrev
                String pages = book.getPages().toString();
                String numreviews = book.getNumReviews().toString();
                String pagesrev = pages + " Pages | " + numreviews + " reviews";
                // Setting pagesrev variable to bookPages
                bookPages.setText(pagesrev);
                // Getting available book variable
                String available = "Available books: " + String.valueOf(book.getAvailableBooksNum());
                // Setting available book variable
                availableBooks.setText(available);
                // Getting reserved book number
                String reserved = "Reserved books: " + String.valueOf(book.getNumReserved());
                // Setting reserved book number
                reservedBooks.setText(reserved);
                // Getting and Setting the rating
                bookRating.setRating(book.getRating());
                // Getting isbn String
                String isbn = "ISBN: " + book.getISBN();
                // Setting ISBN variable
                bookISBN.setText(isbn);
                // Setting the book image
                Glide.with(BookPage.this)
                        .load(documentSnapshot.get("imgUrl").toString()) //set the img book url
                        .transforms(new CenterCrop() , new RoundedCorners(16))
                        .into(bookImage); //destination path
            }
        });
    }

    /**
     *  Method to reserve a book
     */
    public void reserveBook() {
        // Getting current date
        final Date today = Calendar.getInstance().getTime();

        reservationCollection
                .whereEqualTo("book_title", titleFeed)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            try {
                                for(QueryDocumentSnapshot document : task.getResult()) {
                                    DocumentReference doc = document.getReference();
                                    Reservation reservation = document.toObject(Reservation.class);
                                    document.getId();
                                    /*
                                        Condition that checks if reservation date is due today,
                                        if it is the book is no longer reserved and fields
                                        is_active and is_done are updated.
                                     */
                                    if (reservation.getReserved_to() == today) {
                                        bookReference.update("availableBooksNum", FieldValue.increment(1));
                                        bookReference.update("numReserved", FieldValue.increment(-1));
                                        doc.update("is_active",false);
                                        doc.update("is_done", true);
                                    }
                                }
                            } catch (NullPointerException e) {
                                Log.e(TAG, "onComplete: NullPointerException " + e.getMessage());
                            }
                        }
                    }
                });


        bookReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Books book = documentSnapshot.toObject(Books.class);
                assert book != null;
                /*
                    number of available books is bigger
                    than zero a new activity
                    is opened to reserve a book
                 */
                if(book.getAvailableBooksNum() > 0) {
                    Intent intent = new Intent(BookPage.this, ReserveBook.class);
                    intent.putExtra("book_id", bookIDFeed);
                    intent.putExtra("title", titleFeed);
                    startActivity(intent);
                } else {
                    // reserved button is disabled
                    reserveButton.setEnabled(false);
                    Toast.makeText(BookPage.this,"All books are reserved" , Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     *  Method to cancel book reservation
     */
    public void cancelReservation() {
        final String userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference userReference = usersCollection.document(userID);
        // Getting user document from users collection
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    /*
                        Getting document from reservation collection;
                        Book with specific title, reserved by a current user
                        and reservation has to be active
                     */
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
                                        // Updating reservation fields
                                        reservationReference.update("is_active", false);
                                        reservationReference.update("is_cancelled", true);
                                        reservationReference.update("is_done",true);
                                        // Updating book fields
                                        bookReference.update("availableBooksNum", FieldValue.increment(1));
                                        bookReference.update("numReserved", FieldValue.increment(-1));
                                        // Loading book data after updating fields
                                        loadBookData();
                                        // Toasting a message to a user
                                        Toast.makeText(BookPage.this,"Reservation cancelled" , Toast.LENGTH_SHORT).show();
                                    }
                                    // changing Buttons visibility
                                    reserveButton.setVisibility(View.VISIBLE);
                                    cancelButton.setVisibility(View.INVISIBLE);
                                }

                            });
                }
            }
        });
    }


    /**
     *  Method to check if user already reserved a book
     *  if true changing visibility of reserved button
     *  to INVISIBLE and making CANCEL button VISIBLE
     */
    public void checkUserReserved() {
        final String userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference userReference = usersCollection.document(userID);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                     /*
                        Getting document from reservation collection;
                        Book with specific title, reserved by a current user
                        and reservation has to be active
                     */
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
                                        // Changing Buttons visibility
                                        reserveButton.setVisibility(View.INVISIBLE);
                                        cancelButton.setVisibility(View.VISIBLE);
                                    }
                                }

                            });
                }
            }
        });
    }

    /**
     *   Method to write a review about book
     *   it opens a new activity
     */
    public void writeReview() {
        Intent intent = new Intent(BookPage.this, AddReview.class);
        intent.putExtra("book_id", bookIDFeed);
        intent.putExtra("title", titleFeed);
        startActivity(intent);
    }

    /**
     * Method for building RecyclerView
     * To display book reviews from other students
     */
    public void buildReviewsRecyclerView() {
        reviewRecyclerView = findViewById(R.id.bookPageRecyclerView);

        // Getting extra data from previous activity
        Intent intent = getIntent();
        bookIDFeed = intent.getStringExtra("book_id");
        titleFeed = intent.getStringExtra("book_title");

        // Referencing reviews Colleciton and building a query
        CollectionReference reviewsCollection = fStore.collection("library_book_reviews");
        Query query = reviewsCollection.whereEqualTo("book_title", titleFeed).orderBy("date_published", Query.Direction.DESCENDING);

        // Building options
        FirestoreRecyclerOptions<Review> options = new FirestoreRecyclerOptions.Builder<Review>()
                .setQuery(query, Review.class)
                .build();

        // new reviewAdapter instance
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
