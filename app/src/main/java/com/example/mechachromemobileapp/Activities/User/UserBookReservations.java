package com.example.mechachromemobileapp.Activities.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Activities.Library.BookPage;
import com.example.mechachromemobileapp.Adapters.ReservationAdapter;
import com.example.mechachromemobileapp.Models.Reservation;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * UserBookReservations activity
 *
 * Displays all the books user has reserved
 */
public class UserBookReservations extends AppCompatActivity {

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference reservationRef = fStore.collection("library_books_reservations");
    private ReservationAdapter reservationAdapter;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_book_reservations);
        buildRecyclerView();
    }


    /**
     *
     */
    private void buildRecyclerView() {
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        Query query = reservationRef.whereEqualTo("user_reserved_id", userID).orderBy("reserved_to", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Reservation> options = new FirestoreRecyclerOptions.Builder<Reservation>()
                .setQuery(query, Reservation.class)
                .build();

        reservationAdapter = new ReservationAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.reservations_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reservationAdapter);

        reservationAdapter.setOnItemClickListener(new ReservationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Reservation reservation = documentSnapshot.toObject(Reservation.class);
                String book_title = reservation.getBook_title();
                String book_id = reservation.getBook_id();
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
        reservationAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        reservationAdapter.stopListening();
    }
}
