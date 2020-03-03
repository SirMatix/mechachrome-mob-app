package com.example.mechachromemobileapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class BookPage extends AppCompatActivity {

    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);

    }

    public void loadBookData() {
        fStore = FirebaseFirestore.getInstance();


    }

    public void loadReviews() {

    }

    public void reserveBook() {

    }

    public void writeReview() {

    }
}
