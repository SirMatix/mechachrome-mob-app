package com.example.mechachromemobileapp;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReserveBook extends AppCompatActivity {

    public static final String TAG = "TAG";
    String userID, titleAuthorFeed, titleFeed;
    TextView bookTitle, bookAuthor, bookPages, availableBooks, dateReserveFrom, dateReserveTo;
    Button reserveBook, discardReserve;
    ImageView bookImage;
    RatingBar bookRating;
    DocumentReference bookReference, userReference, reservationReference;
    String today, twoweeks;
    Calendar calendar;
    SimpleDateFormat dateFormat;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_book);
        initViews();
        getBookdata();
        setDates();

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

    public void initViews() {
        // init firebase elements
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        // getting intent from Library activity and getting extra string
        Intent intent = getIntent();
        titleAuthorFeed = intent.getStringExtra("title_author");
        titleFeed = intent.getStringExtra(("title"));

        // init layout elements
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

    public void getBookdata() {
        bookReference = fStore.collection("library_books").document(titleAuthorFeed);
        bookReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                bookTitle.setText(documentSnapshot.getString("title"));
                bookAuthor.setText(documentSnapshot.getString("author"));
                String pages = documentSnapshot.get("pages").toString();
                String numreviews = documentSnapshot.get("numReviews").toString();
                String pagesrev = pages + " Pages | " + numreviews + " reviews";
                bookPages.setText(pagesrev);
                availableBooks.setText(documentSnapshot.get("availableBooksNum").toString());
                bookRating.setRating(Float.parseFloat(documentSnapshot.get("rating").toString()));

                Glide.with(ReserveBook.this)
                        .load(documentSnapshot.get("imgUrl").toString()) //set the img book url
                        .transforms(new CenterCrop() , new RoundedCorners(16))
                        .into(bookImage); //destination path
            }
        });
    }

    public void setDates() {
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyy");
        today = dateFormat.format(calendar.getTime());
        // adding two weeks
        calendar.add(Calendar.WEEK_OF_YEAR,2);
        twoweeks = dateFormat.format(calendar.getTime());
        dateReserveFrom.setText(today);
        dateReserveTo.setText(twoweeks);
    }

    public void reserveBook() {
        // initializing references
        bookReference = fStore.collection("library_books").document(titleAuthorFeed);
        reservationReference = fStore.collection("library_books_reservations").document();
        userReference = fStore.collection("users").document(userID);

        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final User user = documentSnapshot.toObject(User.class);
                bookReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Books book = documentSnapshot.toObject(Books.class);
                        Reservation reservation = new Reservation();
                        reservation.setBook_author(book.getAuthor());
                        reservation.setBook_title(book.getTitle());
                        reservation.setUser_reserved_for(user.getFname() + " " + user.getLname());
                        reservation.setUser_reserver_id(userID);
                        Calendar calendar1 = Calendar.getInstance();
                        Date today = calendar1.getTime();
                        reservation.setReserved_from(today);
                        calendar1.add(Calendar.WEEK_OF_YEAR,2);
                        Date twoweeks = calendar1.getTime();
                        reservation.setReserved_to(twoweeks);
                        reservation.setIs_active(true);
                        reservation.setIs_cancelled(false);
                        reservation.setIs_done(false);
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
