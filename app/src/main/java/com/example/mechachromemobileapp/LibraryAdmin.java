package com.example.mechachromemobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class LibraryAdmin extends AppCompatActivity {

    private final String TAG = "TAG";
    private RecyclerView booksRecyclerView;
    private BooksAdapter booksAdapter;
    private List<Books> booksData;
    private Button addBookButton, removeBookButton;
    private FirebaseFirestore fStore;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_admin);


        initViews();
        initBooksData();
        setupBooksAdapter();
    }

    private void setupBooksAdapter() {

        booksAdapter = new BooksAdapter(booksData);
        booksRecyclerView.setAdapter(booksAdapter);

        booksAdapter.setOnItemClickListener(new BooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Books book = booksData.get(position);
                Intent intent = new Intent(LibraryAdmin.this, BookPage.class);
                intent.putExtra("title_author", book.getTitle()+book.getAuthor());
                startActivity(intent);
            }
        });
    }

    private void initBooksData() {
        fStore = FirebaseFirestore.getInstance();
        booksData = new ArrayList<>();
        /*String title = "The oceans of pleasure";
        String author = "John Dick";
        String category = "Adventure";
        String imgUrl = "https://firebasestorage.googleapis.com/v0/b/mechachromemobileapp-963dc.appspot.com/o/library_books_image%2F123123.jpg?alt=media&token=eaa76b0a-c610-4c8c-aac5-177e02919cdc";
        int pages = 238;
        int numReviews = 23;
        int rating = 4;
        int numRatings = 100;

        booksData.add(new Books(title,author,category,imgUrl,pages,numReviews,rating,numRatings));

         */


        fStore.collection("library_books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // getting the book variables
                                String title = document.getString("title");
                                String author = document.getString("author");
                                String description = document.getString("description");
                                String category = document.getString("category");
                                String imgUrl = document.getString("imgUrl");
                                long pages = (long) document.get("pages");
                                long numReviews = (long) document.get("numReviews");
                                double rating = (double) document.get("rating");
                                long numRatings = (long) document.get("numRatings");

                                // initializing new book
                                Books book = new Books(title, author, description, category, imgUrl, pages, numReviews, rating, numRatings);

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

    private void initViews() {
        mStorageRef = FirebaseStorage.getInstance().getReference("library_books_image");

        addBookButton = findViewById(R.id.addBookBtn);
        removeBookButton = findViewById(R.id.removeBookBtn);
        booksRecyclerView = findViewById(R.id.libraryAdminRecyclerView);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        booksRecyclerView.setHasFixedSize(true);

        booksRecyclerView.setItemAnimator(new CustomItemAnimation());

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
                about succesful adding of a book (this is admin only functionality)
                 */
                startActivity(new Intent(getApplicationContext(), AddBook.class));
                finish();
            }
        });

        removeBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBook();
            }

            private void removeBook() {
                /*
                removing book will work on the principle to ask which book is going to be removed
                then the user will pick the book by clicking on it and the book position in the list of
                books will be passed to remove function in order to remove the book at that index
                (this is admin only functionality)
                 */
                booksData.remove(1);
                booksAdapter.notifyItemRemoved(1);
            }
        });
    }
}
