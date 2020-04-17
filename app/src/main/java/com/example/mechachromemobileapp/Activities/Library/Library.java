package com.example.mechachromemobileapp.Activities.Library;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Adapters.BooksAdapter;
import com.example.mechachromemobileapp.Models.Books;
import com.example.mechachromemobileapp.Models.CustomItemAnimation;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Library activity
 *
 * Displays books for library_books collection onCreating of application
 * it displays all the books if user writes category in a search bar and
 * presses search button it updates adapter option and shows book
 * from a specific category. When user clicks on a book item
 * new activity starts with a detailed information about a book.
 *
 */
public class Library extends AppCompatActivity {
    private EditText searchBookBar;
    private BooksAdapter booksAdapter;
    private FloatingActionButton searchBookButton;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    final private CollectionReference booksReference = fStore.collection("library_books");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        initViews();
        buildBooksRecyclerView(booksAdapter);
        setButtons();
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    private void initViews() {
        // getting the bookAdapter
        booksAdapter = getAdapter();

        // Initialization widgets from layout
        searchBookButton = findViewById(R.id.button_search);
        searchBookBar = findViewById(R.id.book_search_bar);
    }

    /**
     * Method to get BooksAdapter
     *
     * @return BooksAdapter with all books from firestore
     */
    private BooksAdapter getAdapter() {
        Query query = booksReference.orderBy("addDate", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Books> options = new FirestoreRecyclerOptions.Builder<Books>()
                .setQuery(query, Books.class)
                .build();
        return new BooksAdapter(options);
    }

    /**
     *  Method to buildBooksRecyclerView
     *
     *  builds book recycler view
     *
     * @param adapter with a list of books
     */
    private void buildBooksRecyclerView(BooksAdapter adapter) {
        RecyclerView booksRecyclerView = findViewById(R.id.libraryRecyclerView);
        booksRecyclerView.setHasFixedSize(true);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        booksRecyclerView.setItemAnimator(new CustomItemAnimation());
        booksRecyclerView.setAdapter(adapter);
    }
    /**
     *  This method sets the onClickListener to buttons
     */
    private void setButtons() {
        // attaching OnItemClickListener to booksAdapter, this enables clicking on each book for sale element
        booksAdapter.setOnItemClickListener(new BooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent intent = new Intent(getApplicationContext(), BookPage.class);
                intent.putExtra("book_title", documentSnapshot.getString("title"));
                intent.putExtra("book_id", documentSnapshot.getId());
                startActivity(intent);
            }
        });

        // Button for searching book
        searchBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if text from serachBookBar is an empty String display all the books
                if(searchBookBar.getText().toString().equals("")){
                    Query query = booksReference.orderBy("addDate", Query.Direction.ASCENDING);
                    FirestoreRecyclerOptions<Books> options = new FirestoreRecyclerOptions.Builder<Books>()
                            .setQuery(query, Books.class)
                            .build();
                    booksAdapter.updateOptions(options);
                // display library books from a specific category
                } else {
                    String searchCategory = searchBookBar.getText().toString().trim();
                    Query query = booksReference.whereEqualTo("category", searchCategory).orderBy("addDate", Query.Direction.ASCENDING);;
                    FirestoreRecyclerOptions<Books> options = new FirestoreRecyclerOptions.Builder<Books>()
                            .setQuery(query, Books.class)
                            .build();
                    booksAdapter.updateOptions(options);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        booksAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        booksAdapter.stopListening();
    }
}
