package com.example.mechachromemobileapp.Activities.Library;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Models.Books;
import com.example.mechachromemobileapp.Adapters.BooksAdapter;
import com.example.mechachromemobileapp.Adapters.CustomItemAnimation;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class LibraryUser extends AppCompatActivity {

    private RecyclerView booksRecyclerView;
    private BooksAdapter booksAdapter;
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
                Intent intent = new Intent(LibraryUser.this, BookPage.class);
                intent.putExtra("book_title", book.getTitle());
                intent.putExtra("book_id", documentSnapshot.getId());
                startActivity(intent);
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

    private void initViews() {
        // Firebase init
        fStore = FirebaseFirestore.getInstance();

        // recyclerView init
        booksRecyclerView = findViewById(R.id.libraryAdminRecyclerView);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        booksRecyclerView.setHasFixedSize(true);
        booksRecyclerView.setItemAnimator(new CustomItemAnimation());
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
    }
}
