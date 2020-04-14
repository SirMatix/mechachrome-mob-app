package com.example.mechachromemobileapp.Activities.Library;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
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

public class LibraryAdmin extends AppCompatActivity {

    private EditText searchBookBar;
    private BooksAdapter booksAdapter;
    private FloatingActionButton addBookButton, searchBookButton;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    final private CollectionReference booksReference = fStore.collection("library_books");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_admin);
        initViews();
        buildBooksRecyclerView(booksAdapter);
        setButtons();
    }

    private void initViews() {
        // initialize recycler view adapter
        booksAdapter = getAdapter();

        // layout elements init
        addBookButton = findViewById(R.id.button_add_book);
        searchBookButton = findViewById(R.id.button_search);
        searchBookBar = findViewById(R.id.book_search_bar);
    }

    private BooksAdapter getAdapter() {
        Query query = booksReference.orderBy("addDate", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Books> options = new FirestoreRecyclerOptions.Builder<Books>()
                .setQuery(query, Books.class)
                .build();
        return new BooksAdapter(options);
    }

    private void buildBooksRecyclerView(BooksAdapter adapter) {
        RecyclerView booksRecyclerView = findViewById(R.id.libraryAdminRecyclerView);
        booksRecyclerView.setHasFixedSize(true);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        booksRecyclerView.setItemAnimator(new CustomItemAnimation());
        booksRecyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                booksAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(booksRecyclerView);
    }

    private void setButtons() {
        booksAdapter.setOnItemClickListener(new BooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Books book = documentSnapshot.toObject(Books.class);
                Intent intent = new Intent(LibraryAdmin.this, BookPage.class);
                intent.putExtra("book_title", book.getTitle());
                intent.putExtra("book_id", documentSnapshot.getId());
                startActivity(intent);
            }
        });

        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();
            }
        });

        searchBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchBookBar.getText().toString().equals("")){
                    Query query = booksReference.orderBy("addDate", Query.Direction.ASCENDING);
                    FirestoreRecyclerOptions<Books> options = new FirestoreRecyclerOptions.Builder<Books>()
                            .setQuery(query, Books.class)
                            .build();
                    booksAdapter.updateOptions(options);

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

    /*
        adding the book will work on principle that new activity is opened on which you add
        information about a book and that data is being send to the firestore to store it
        after successful operation admin will go back to previous activity and get a toast
        about successful adding of a book (this is admin only functionality)
    */
    private void addBook() {
        startActivity(new Intent(getApplicationContext(), AddBook.class));
        finish();
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
