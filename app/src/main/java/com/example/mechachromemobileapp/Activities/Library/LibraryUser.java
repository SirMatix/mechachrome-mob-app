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

public class LibraryUser extends AppCompatActivity {
    private EditText searchBookBar;
    private BooksAdapter booksAdapter;
    private FloatingActionButton searchBookButton;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    final private CollectionReference booksReference = fStore.collection("library_books");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_user);
        initViews();
        buildBooksRecyclerView(booksAdapter);
        setButtons();
    }

    private void initViews() {
        // initialize recycler view adapter
        booksAdapter = getAdapter();

        // layout elements initialization
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
        RecyclerView booksRecyclerView = findViewById(R.id.libraryUserRecyclerView);
        booksRecyclerView.setHasFixedSize(true);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        booksRecyclerView.setItemAnimator(new CustomItemAnimation());
        booksRecyclerView.setAdapter(adapter);
    }

    private void setButtons() {
        booksAdapter.setOnItemClickListener(new BooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Books book = documentSnapshot.toObject(Books.class);
                Intent intent = new Intent(getApplicationContext(), BookPage.class);
                intent.putExtra("book_title", book.getTitle());
                intent.putExtra("book_id", documentSnapshot.getId());
                startActivity(intent);
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
