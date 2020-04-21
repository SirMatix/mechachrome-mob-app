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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_book_reservations);
        buildRecyclerView();
    }


    private void buildRecyclerView() {
        // getting data from previous activity
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        // querrying the collection reference to get reservation for specific user
        Query query = reservationRef.whereEqualTo("user_reserved_id", userID).orderBy("reserved_to", Query.Direction.ASCENDING);

        // building firebase options
        FirestoreRecyclerOptions<Reservation> options = new FirestoreRecyclerOptions.Builder<Reservation>()
                .setQuery(query, Reservation.class)
                .build();

        // instantiating reservation adapter
        reservationAdapter = new ReservationAdapter(options);

        // creating recycler view
        RecyclerView recyclerView = findViewById(R.id.reservations_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reservationAdapter);

        // implementing onItemClockListnere interface to
        // add ability to click on reservation and that will lead to a book page
        reservationAdapter.setOnItemClickListener(new ReservationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Reservation reservation = documentSnapshot.toObject(Reservation.class);
                assert reservation != null;
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
