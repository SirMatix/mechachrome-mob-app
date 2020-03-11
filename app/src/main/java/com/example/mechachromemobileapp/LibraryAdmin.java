package com.example.mechachromemobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class LibraryAdmin extends AppCompatActivity {

    private final String TAG = "TAG";
    private RecyclerView booksRecyclerView;
    private BooksAdapter booksAdapter;
    private FloatingActionButton addBookButton;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_admin);

        initViews();
        buildBooksRecyclerView();

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
    }

    private void buildBooksRecyclerView() {
        CollectionReference booksReference = fStore.collection("library_books");
        // query to display newest books in library first
        Query query = booksReference.orderBy("add_date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Books> options = new FirestoreRecyclerOptions.Builder<Books>()
                .setQuery(query, Books.class)
                .build();

        booksAdapter = new BooksAdapter(options);
        booksRecyclerView.setHasFixedSize(true);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        booksRecyclerView.setAdapter(booksAdapter);

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

    /* old code for getting book data
    private void initBooksData() {
        booksData = new ArrayList<>();
        fStore.collection("library_books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Books book = document.toObject(Books.class);
                                Log.d(TAG, "Got the book title: " + document.getString("title"));
                                // adding book to the ArrayList
                                booksData.add(book);
                            }
                            // notifying the booksAdapter with data change
                            booksAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
     */

    private void initViews() {
        // Firebase init
        fStore = FirebaseFirestore.getInstance();

        // layout elements init
        addBookButton = findViewById(R.id.button_add_book);

        // recyclerView init
        booksRecyclerView = findViewById(R.id.libraryAdminRecyclerView);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        booksRecyclerView.setHasFixedSize(true);
        booksRecyclerView.setItemAnimator(new CustomItemAnimation());

        // buttons on click listeners
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();
            }

            private void addBook() {
                /*
                adding the book will work on principle that new activity is opened on which you add
                information about a book and that data is being send to the firestore to store it
                after successful operation admin will go back to previous activity and get a toast
                about successful adding of a book (this is admin only functionality)
                 */
                startActivity(new Intent(getApplicationContext(), AddBook.class));
                finish();
            }
        });
    }
}
