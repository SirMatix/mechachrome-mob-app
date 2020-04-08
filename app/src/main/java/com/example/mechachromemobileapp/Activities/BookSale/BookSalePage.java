package com.example.mechachromemobileapp.Activities.BookSale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mechachromemobileapp.Activities.User.UserMessage;
import com.example.mechachromemobileapp.Models.BookSaleModel;
import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookSalePage extends AppCompatActivity {

    public static final String TAG = "TAG";
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
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

        buyBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyBook();
            }
        });

        messageSellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageSeller();
            }
        });

    }

    public void initViews(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        bookSaleCollection = fStore.collection("books_for_sale");

        // getting intent from Library activity and getting extra string
        Intent intent = getIntent();
        bookSaleIDFeed = intent.getStringExtra("book_id");
        titleFeed = intent.getStringExtra("book_title");

        // finding variables from layout
        bookTitle = findViewById(R.id.item_book_title);
        bookAuthor = findViewById(R.id.item_book_author);
        bookDescription = findViewById(R.id.book_description);
        bookPages = findViewById(R.id.item_book_pages);
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
                String priceString = "Price: £" + documentSnapshot.get("price").toString();
                bookPrice.setText(priceString);

                Glide.with(getApplicationContext())
                        .load(documentSnapshot.getString("imgUrl")) //set the img book url
                        .transforms(new CenterCrop(), new RoundedCorners(16))
                        .into(bookImage); //destination path
            }
        });
    }

    public void buyBook() {
        bookSaleReference = bookSaleCollection.document(bookSaleIDFeed);
        bookSaleReference.update("sold", true);
        Toast.makeText(getApplicationContext(), "Congratulations you just bought a book", Toast.LENGTH_SHORT).show();
        bookSaleReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                BookSaleModel bookForSale = documentSnapshot.toObject(BookSaleModel.class);
                fUser = FirebaseAuth.getInstance().getCurrentUser();
                String userID = fUser.getUid();
                String message = "I have just bought you book" + bookForSale.getTitle() + " by " + bookForSale.getAuthor() + " for £" + bookForSale.getPrice();
                UserMessage buyMessage = new UserMessage();
                buyMessage.sendMessage(userID, bookForSale.getSeller_id(), message);
                finish();
            }
        });
    }

    public void messageSeller() {
        bookSaleReference = bookSaleCollection.document(bookSaleIDFeed);
        bookSaleReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                BookSaleModel bookForSale = documentSnapshot.toObject(BookSaleModel.class);
                String sellerID = bookForSale.getSeller_id();
                Intent intent = new Intent(getApplicationContext(), UserMessage.class);
                intent.putExtra("userID", sellerID);
                startActivity(intent);
            }
        });
    }

}
