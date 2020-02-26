package com.example.mechachromemobileapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class Library extends AppCompatActivity {

    private RecyclerView rvBooks;
    private BooksAdapter booksAdapter;
    private List<Books> mdata;
    private Button btnAddBook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        initViews();
        initmdataBooks();
        setupBooksAdapter();
    }

    private void setupBooksAdapter() {

        booksAdapter = new BooksAdapter(mdata);
        rvBooks.setAdapter(booksAdapter);
    }

    private void initmdataBooks() {
        mdata = new ArrayList<>();
        mdata.add(new Books(R.drawable.book1));
        mdata.add(new Books(R.drawable.book2));
        mdata.add(new Books(R.drawable.book3));
        mdata.add(new Books(R.drawable.book4));
        mdata.add(new Books(R.drawable.book5));
    }

    private void initViews() {
        btnAddBook = findViewById(R.id.btn_add);
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
                Books book = new Books(R.drawable.book1);
                mdata.add(1,book);
                booksAdapter.notifyItemInserted(1);

            }
        });
    }
}