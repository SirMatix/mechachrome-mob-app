package com.example.mechachromemobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

public class LibraryUser extends AppCompatActivity {

    private final String TAG = "TAG";
    private RecyclerView bookRecyclerView;
    private BooksAdapter booksAdapter;
    private List<Books> booksData;
    private FirebaseFirestore fStore;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_user);

        initViews();
        initBooksData();
        setupBooksAdapter();
    }

    private void setupBooksAdapter() {
        booksAdapter = new BooksAdapter(booksData);
        bookRecyclerView.setAdapter(booksAdapter);

        booksAdapter.setOnItemClickListener(new BooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Books book = booksData.get(position);
                Intent intent = new Intent(LibraryUser.this, BookPage.class);
                intent.putExtra("book_title", book.getTitle());
                startActivity(intent);
            }
        });
    }

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

    private void initViews() {
        // firebase init
        mStorageRef = FirebaseStorage.getInstance().getReference("library_books_image");
        fStore = FirebaseFirestore.getInstance();

        // Recycler view init
        bookRecyclerView = findViewById(R.id.libraryUserRecyclerView);
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookRecyclerView.setHasFixedSize(true);
        bookRecyclerView.setItemAnimator(new CustomItemAnimation());
    }

}
