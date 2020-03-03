package com.example.mechachromemobileapp;

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

import java.util.ArrayList;
import java.util.List;

public class LibraryAdmin extends AppCompatActivity {

    private final String TAG = "TAG";
    private RecyclerView rvBooks;
    private BooksAdapter booksAdapter;
    private List<Books> booksData;
    private Button btnAddBook, btnRemBook;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_admin);
        fStore = FirebaseFirestore.getInstance();
        initViews();
        initbooksData();
        setupBooksAdapter();
    }

    private void setupBooksAdapter() {

        booksAdapter = new BooksAdapter(booksData);
        rvBooks.setAdapter(booksAdapter);
    }

    private void initbooksData() {
        booksData = new ArrayList<>();

        booksData.add(new Books("The oceans of pleasure", "John Dick", 238, 23, 4, R.drawable.book2));
        booksData.add(new Books("Fifty shades of Grey", "E. L. James", 514, 69, 1, R.drawable.book3));
        booksData.add(new Books("Computer Programming", "Alexander Bell", 1000, 123, 5, R.drawable.book4));
        booksData.add(new Books("Coding for life", "Hacker Dude", 310, 32, 4, R.drawable.book5));


        fStore.collection("books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // getting the book variables
                                String title = document.getString("title");
                                String description = document.getString("description");
                                String author = document.getString("author");
                                String imgUrl = document.getString("imgUrl");
                                int pages = (int) document.get("pages");
                                int review = (int) document.get("review");
                                float rating = (float) document.get("rating");
                                int drawableResources = (int) document.get("drawableResources");

                                // initializing new book
                                Books book = new Books(title, description, author, imgUrl, pages, review, rating, drawableResources);

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
        btnAddBook = findViewById(R.id.btn_add);
        btnRemBook = findViewById(R.id.btn_remove);
        rvBooks = findViewById(R.id.rv_books);
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        rvBooks.setHasFixedSize(true);

        rvBooks.setItemAnimator(new CustomItemAnimation());

        btnAddBook.setOnClickListener(new View.OnClickListener() {
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
                Books book = new Books(R.drawable.book1);
                booksData.add(1,book);
                booksAdapter.notifyItemInserted(1);
            }
        });

        btnRemBook.setOnClickListener(new View.OnClickListener() {
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


    /*
    in this place is going to be a method enabling users to reserve a book, it will work on a assumptiom
     */

}
