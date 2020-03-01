package com.example.mechachromemobileapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddBook extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText bookTitle, bookDescription, bookAuthor, bookPages;
    Button bAddBtn, bAddImage;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Data variables
        bookTitle = findViewById(R.id.bookTitle);
        bookDescription = findViewById(R.id.bookDescription);
        bookAuthor = findViewById(R.id.bookAuthor);
        //bookImage = findViewById(R.id.bookImage);
        bookPages = findViewById(R.id.bookPages);

        // Button variables
        bAddBtn = findViewById(R.id.addBookBtn);
        bAddImage = findViewById(R.id.addBookImageBtn);

        // Firebase initializations
        fStore = FirebaseFirestore.getInstance();
    }

    public void addBook(){
        fStore = FirebaseFirestore.getInstance();

        DocumentReference docIds = fStore.collection("ids").document();
        String title =  bookTitle.getText().toString();

        String description = bookDescription.getText().toString();
        String author =  bookAuthor.getText().toString();
        //int pages;
        //pages = (int) bookPages.getText();


        // initializing new book

        /*
        Books book = new Books(title, description, author); // imgUrl, pages, review, rating, drawableResources);


        docIds.set(book).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Ids document created");
            }
        });

         */
    }
}
