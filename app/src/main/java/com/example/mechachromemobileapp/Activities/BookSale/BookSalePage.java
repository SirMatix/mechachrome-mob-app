package com.example.mechachromemobileapp.Activities.BookSale;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookSalePage extends AppCompatActivity {

    public static final String TAG = "TAG";
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String bookSaleIDFeed, titleFeed;
    Button buyBookButton, messageSellerButton;
    TextView bookTitle, bookAuthor, bookDescription, bookPages, bookPrice, bookCondition;
    ImageView bookImage;
    CollectionReference bookSaleCollection;
    DocumentReference bookSaleReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_sale_page);

        initViews();
        loadBookData();

    }

    public void initViews(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        bookSaleCollection = fStore.collection("library_books");

        // getting intent from Library activity and getting extra string
        Intent intent = getIntent();
        bookSaleIDFeed = intent.getStringExtra("book_id");
        titleFeed = intent.getStringExtra("book_title");

        // finding variables from layout
        bookTitle = findViewById(R.id.item_book_title);
        bookAuthor = findViewById(R.id.item_book_author);
        bookDescription = findViewById(R.id.availableBooks);
        bookPages = findViewById(R.id.item_book_pagesrev);
        bookPrice = findViewById(R.id.book_price);
        bookImage = findViewById(R.id.item_book_img);
        bookCondition = findViewById(R.id.item_book_condition);

        // buttons
        messageSellerButton = findViewById(R.id.write_to_seller_button);
        buyBookButton = findViewById(R.id.buy_book_button);
    }

    public void loadBookData() {
        bookSaleReference = bookSaleCollection.document(bookSaleIDFeed);
        bookSaleReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                bookTitle.setText(documentSnapshot.getString("title"));
                bookAuthor.setText(documentSnapshot.getString("author"));
                bookDescription.setText(documentSnapshot.getString("description"));
                String pages = documentSnapshot.get("pages").toString();
                String displayPages = pages + " Pages";
                bookPages.setText(displayPages);
                bookCondition.setText(documentSnapshot.getString("condition"));
                bookPrice.setText(documentSnapshot.getString("price"));

                Glide.with(getApplicationContext())
                        .load(documentSnapshot.get("imgUrl").toString()) //set the img book url
                        .transforms(new CenterCrop(), new RoundedCorners(16))
                        .into(bookImage); //destination path
            }
        });
    }

    public void buyBook() {

    }

    public void messageSeller() {

    }

}
