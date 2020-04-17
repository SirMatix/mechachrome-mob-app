package com.example.mechachromemobileapp.Activities.Library;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mechachromemobileapp.Models.Books;
import com.example.mechachromemobileapp.R;
import com.example.mechachromemobileapp.Models.Reservation;
import com.example.mechachromemobileapp.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * ReserveBook activity
 *
 *
 */
public class ReserveBook extends AppCompatActivity {

    private static final String TAG = "ReserveBook";
    private String userID;
    private String titleAuthorFeed;
    private TextView bookTitle, bookAuthor, bookPages, availableBooks, dateReserveFrom, dateReserveTo;
    private Button reserveBook, discardReserve;
    private ImageView bookImage;
    private RatingBar bookRating;
    private DocumentReference bookReference;
    private DocumentReference reservationReference;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_book);
        initViews();
        getBookData();
        setDates();
        setButtons();
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    public void initViews() {
        // Instantiating of Firebase widgets
        fStore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        // getting intent from Library activity and getting extra string
        Intent intent = getIntent();
        titleAuthorFeed = intent.getStringExtra("title_author");

        // Initialization widgets from layout
        dateReserveFrom = findViewById(R.id.reserve_from);
        dateReserveTo = findViewById(R.id.reserve_to);
        reserveBook = findViewById(R.id.reserve_book_btn);
        discardReserve = findViewById(R.id.discard_btn);
        bookTitle = findViewById(R.id.item_book_title);
        bookAuthor = findViewById(R.id.item_book_author);
        bookPages = findViewById(R.id.item_book_pagesrev);
        availableBooks = findViewById(R.id.numAvailable);
        bookImage = findViewById(R.id.item_book_img);
        bookRating = findViewById(R.id.item_book_ratingBar);
    }

    /**
     *  Method to getBookData from Firestore
     */
    public void getBookData() {
        bookReference = fStore.collection("library_books").document(titleAuthorFeed);
        bookReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Books book = documentSnapshot.toObject(Books.class);
                assert book != null;
                bookTitle.setText(book.getTitle());
                bookAuthor.setText(book.getAuthor());
                String pages = book.getPages().toString();
                String numreviews = book.getNumReviews().toString();
                String pagesrev = pages + " Pages | " + numreviews + " reviews";
                bookPages.setText(pagesrev);
                availableBooks.setText(String.valueOf(book.getAvailableBooksNum()));
                bookRating.setRating(book.getRating());

                Glide.with(ReserveBook.this)
                        .load(book.getImgUrl()) // get imgUrl from book
                        .transform(new CenterCrop() , new RoundedCorners(16))
                        .into(bookImage); // destination path
            }
        });
    }

    /**
     * Method to set the dates
     */
    public void setDates() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");
        String today = dateFormat.format(calendar.getTime());
        // adding two weeks
        calendar.add(Calendar.WEEK_OF_YEAR,2);
        String twoweeks = dateFormat.format(calendar.getTime());
        dateReserveFrom.setText(today);
        dateReserveTo.setText(twoweeks);
    }

    /**
     *  This method sets the onClickListener to buttons
     */
    private void setButtons() {
        reserveBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveBook();
                /*
                Intent intent = new Intent(ReserveBook.this, BookPage.class);
                intent.putExtra("title_author", titleAuthorFeed);
                intent.putExtra("title", titleFeed);
                startActivity(intent);
                 */
            }
        });

        discardReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(ReserveBook.this, BookPage.class);
                intent.putExtra("title_author", titleAuthorFeed);
                intent.putExtra("title", titleFeed);
                startActivity(intent);
                 */
                finish();
            }
        });
    }

    /**
     *  Method to reserve the Book
     */
    public void reserveBook() {
        // initializing references
        bookReference = fStore.collection("library_books").document(titleAuthorFeed);
        reservationReference = fStore.collection("library_books_reservations").document();
        DocumentReference userReference = fStore.collection("users").document(userID);

        // Getting the user
        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Making User object instance from documentSnapshot
                final User user = documentSnapshot.toObject(User.class);
                bookReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // Making Book object instance from documentSnapshot
                        Books book = documentSnapshot.toObject(Books.class);
                        // Instance of Reservation class
                        Reservation reservation = new Reservation();
                        // Setting book data to reservation
                        assert book != null;
                        reservation.setBook_author(book.getAuthor());
                        reservation.setBook_title(book.getTitle());
                        // Setting user data to reservation
                        assert user != null;
                        reservation.setUser_reserved_for(user.getFname() + " " + user.getLname());
                        reservation.setUser_reserver_id(userID);
                        // Setting reservation start date
                        Calendar calendar1 = Calendar.getInstance();
                        Date today = calendar1.getTime();
                        reservation.setReserved_from(today);
                        // Setting reservation end date (2 weeks from now)
                        calendar1.add(Calendar.WEEK_OF_YEAR,2);
                        Date twoweeks = calendar1.getTime();
                        reservation.setReserved_to(twoweeks);
                        // Setting reservation boolean values
                        reservation.setIs_active(true);
                        reservation.setIs_cancelled(false);
                        reservation.setIs_done(false);
                        // Updating fields in library_books document
                        bookReference.update("numReserved", FieldValue.increment(1));
                        bookReference.update("availableBooksNum", FieldValue.increment(-1));

                        reservationReference.set(reservation).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG,"new reservation has been made");
                                Toast.makeText(ReserveBook.this,"Reservation successful", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }
                });
            }
        });
    }


}
