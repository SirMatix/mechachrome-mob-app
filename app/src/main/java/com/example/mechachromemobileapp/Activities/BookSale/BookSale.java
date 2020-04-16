package com.example.mechachromemobileapp.Activities.BookSale;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Adapters.BookSaleAdapter;
import com.example.mechachromemobileapp.Models.BookSaleModel;
import com.example.mechachromemobileapp.Models.CustomItemAnimation;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 *  Method for displaying books for sale
 */
public class BookSale extends AppCompatActivity {

    // global variables
    private final String TAG = "BookSale";
    private BookSaleAdapter bookSaleAdapter;
    private FloatingActionButton addBookButton;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference booksReference = fStore.collection("books_for_sale");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_sale);
        initViews();
        buildBookSaleRecyclerView(bookSaleAdapter);
        setButtons();
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    private void initViews() {
        // Adapter initialization
        bookSaleAdapter = getAdapter();

        // Layout buttons initialization
        addBookButton = findViewById(R.id.button_add_book);
    }

    /**
     *  Method opening a new activity to add a book
     */
    private void addBook() {
        startActivity(new Intent(getApplicationContext(), AddSaleBook.class));
        finish();
    }

    /**
     *  This method sets the onClickListener to buttons
     */
    private void setButtons() {
        // Button to add book
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();
            }
        });

        // attaching OnItemClickListener to bookSaleAdapter, this enables clicking on each book for sale element
        bookSaleAdapter.setOnItemClickListener(new BookSaleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                BookSaleModel sellBook = documentSnapshot.toObject(BookSaleModel.class);
                if(!sellBook.isSold()){
                    Intent intent = new Intent(getApplicationContext(), BookSalePage.class);
                    intent.putExtra("book_title", sellBook.getTitle());
                    intent.putExtra("book_id", documentSnapshot.getId());
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Method to get BookSaleAdapter with different options
     *
     * Option 1: when user enters this activity from MainActivity --> display all books for sale
     * Option 2: when user enters this activity from UserAccount activity --> display this user books for sale
     *
     * @return BookSaleAdapter with specific option build on different query
     */
    private BookSaleAdapter getAdapter() {
        // gets intent from previous activity
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        // condition to check if userID has been passed
        if(userID == null) {
            // query to get books for sale sorted by add date
            Query query = booksReference.orderBy("addDate", Query.Direction.ASCENDING);
            FirestoreRecyclerOptions<BookSaleModel> options = new FirestoreRecyclerOptions.Builder<BookSaleModel>()
                    .setQuery(query, BookSaleModel.class)
                    .build();
            return new BookSaleAdapter(options);
        } else {
            // query to get books for sale added by user sorted by add date
            Query query = booksReference.whereEqualTo("seller_id", userID).orderBy("addDate", Query.Direction.ASCENDING);
            FirestoreRecyclerOptions<BookSaleModel> options = new FirestoreRecyclerOptions.Builder<BookSaleModel>()
                    .setQuery(query, BookSaleModel.class)
                    .build();
            return new BookSaleAdapter(options);
        }
    }

    /**
     * Method for building BookSale RecyclerView
     *
     * @param adapter contains a query that contains elements to be displayed in RecyclerView
     */
    private void buildBookSaleRecyclerView(final BookSaleAdapter adapter) {
        RecyclerView bookSaleRecyclerView;
        // Getting the RecyclerView widget from layout
        bookSaleRecyclerView = findViewById(R.id.booksale_recycler_view);
        // Setting RecyclerView layout manager
        bookSaleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Setting RecyclerView to FixedSize this is done for performance
        bookSaleRecyclerView.setHasFixedSize(true);
        // Setting RecyclerView animator to CustomItemAnimator
        bookSaleRecyclerView.setItemAnimator(new CustomItemAnimation());
        // Setting RecyclerView adapter to adapter variable from parameter
        bookSaleRecyclerView.setAdapter(adapter);

        /*
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(bookSaleRecyclerView);

         */
    }

    @Override
    protected void onStart() {
        super.onStart();
        bookSaleAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bookSaleAdapter.stopListening();
    }
}
