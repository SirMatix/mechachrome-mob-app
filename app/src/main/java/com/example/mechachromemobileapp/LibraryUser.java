package com.example.mechachromemobileapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LibraryUser extends AppCompatActivity {

    private final String TAG = "TAG";
    private RecyclerView rvBooks;
    private BooksAdapter booksAdapter;
    private List<Books> booksData;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_user);

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
        fStore = FirebaseFirestore.getInstance();
        booksData = new ArrayList<>();

        booksData.add(new Books(R.drawable.book1));
        booksData.add(new Books(R.drawable.book2));
        booksData.add(new Books(R.drawable.book3));
        booksData.add(new Books(R.drawable.book4));
        booksData.add(new Books(R.drawable.book5));


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
        rvBooks = findViewById(R.id.rv_books);
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        rvBooks.setHasFixedSize(true);
        rvBooks.setItemAnimator(new CustomItemAnimation());

    }


    /*
    in this place is going to be a method enabling users to reserve a book, it will work on a assumptiom
     */

}
