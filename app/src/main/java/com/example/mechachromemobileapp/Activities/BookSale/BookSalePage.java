package com.example.mechachromemobileapp.Activities.BookSale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

/**
 *  Activity to display a detailed book for sale page
 *
 *  Methods:
 *
 *  onCreate(Bundle savedInstanceState)
 *  initViews()
 *  loadBookData()
 *  setButtons()
 *  buyBook()
 *  messageSeller()
 *
 */
public class BookSalePage extends AppCompatActivity {

    // global variables
    public static final String TAG = "BookSalePage";
    private FirebaseUser fUser;
    private String bookSaleIDFeed;
    private Button buyBookButton, messageSellerButton;
    private TextView bookTitle, bookAuthor, bookDescription, bookPages, bookPrice, bookCondition;
    private ImageView bookImage;
    private CollectionReference bookSaleCollection;
    private DocumentReference bookSaleReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_sale_page);

        initViews();
        loadBookData();
        setButtons();

    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    public void initViews(){
        // Instantiating of Firebase widgets
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        bookSaleCollection = fStore.collection("books_for_sale");

        // Getting intent from BookSale activity and getting extra string
        Intent intent = getIntent();
        bookSaleIDFeed = intent.getStringExtra("book_id");
        String titleFeed = intent.getStringExtra("book_title");

        // Initialization EditText widgets from layout
        bookTitle = findViewById(R.id.item_book_title);
        bookAuthor = findViewById(R.id.item_book_author);
        bookDescription = findViewById(R.id.book_description);
        bookPages = findViewById(R.id.item_book_pages);
        bookPrice = findViewById(R.id.book_price);
        bookImage = findViewById(R.id.item_book_img);
        bookCondition = findViewById(R.id.item_book_condition);

        // Initialization of Button widgets from layout
        messageSellerButton = findViewById(R.id.write_to_seller_button);
        buyBookButton = findViewById(R.id.buy_book_button);
    }

    /**
     *  Method to load book data from Firestore database
     */
    public void loadBookData() {
        // Referencing specific book sale document from Firestore
        bookSaleReference = bookSaleCollection.document(bookSaleIDFeed);
        // Getting referenced document and adding onSuccess Listener
        bookSaleReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // creating BookSaleModel instance from documentSnapshot we got from Firestore
                BookSaleModel thisBook = documentSnapshot.toObject(BookSaleModel.class);
                // try statement to catch NullPointerException
                try {
                    // Setting bookTitle field to title value from thisBook getTitle() method
                    bookTitle.setText(thisBook.getTitle());
                    // Setting bookAuthor field to author value from thisBook getAuthor() method
                    bookAuthor.setText(thisBook.getAuthor());
                    // Setting bookDescription field to title value from thisBook description value
                    bookDescription.setText(thisBook.getDescription());
                    // Getting pages integer from thisBook object and changing it to String object
                    String pages = thisBook.getPages().toString();
                    // Concatenating pages String with word Pages to create a phrase
                    String displayPages = pages + " Pages";
                    // Setting bookPages field to displayPages String
                    bookPages.setText(displayPages);
                    // Setting bookCondition field to value from thisBook getCondition() value
                    bookCondition.setText(thisBook.getCondition());
                    // Creating priceString concatenating phrase 'Price: £' with value from thisBook getPrice() method
                    String priceString = "Price: £" + thisBook.getPrice();
                    // Setting bookPrice field to String priceString
                    bookPrice.setText(priceString);

                    // Setting the book image
                    Glide.with(getApplicationContext())
                            .load(thisBook.getImgUrl()) // getting book image from thisBook objects
                            .transform(new CenterCrop(), new RoundedCorners(16))
                            .into(bookImage); // Destination path
                } catch (NullPointerException e) {
                    Log.e(TAG, "onSuccess: NullPointerException " + e.getMessage());
                }
            }
        });
    }

    /**
     *  This method sets the onClickListener to buttons
     */
    private void setButtons() {
        // Button to buy a book
        buyBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyBook();
            }
        });

        // Button to send private message to a Seller
        messageSellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageSeller();
            }
        });
    }

    /**
     * Method for buying the book
     *
     * It changes sold field in document in books_for_sale collection to True
     * and sends a private message from buyer to book seller with information
     * about buying the book
     *
     */
    public void buyBook() {
        // Referencing specific book sale document from Firestore
        bookSaleReference = bookSaleCollection.document(bookSaleIDFeed);
        // Getting referenced document and adding onSuccess Listener
        bookSaleReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Creating BookSaleModel instance from documentSnapshot we got from Firestore
                BookSaleModel bookForSale = documentSnapshot.toObject(BookSaleModel.class);
                // Getting current userID
                String userID = fUser.getUid();
                try {
                    /*
                        Condition to check if current userID isn't equal to book sellerID
                        as you can't buy book from yourself if you try the Toast will
                        be displayed that forbids you from doing that
                     */
                    if(!userID.equals(bookForSale.getSeller_id())) {
                        Toast.makeText(getApplicationContext(), "Congratulations you just bought a book", Toast.LENGTH_SHORT).show();
                        // Updating sold field in Firestore to true
                        bookSaleReference.update("sold", true, "availableBookNum", 0, "totalBookNum", 0);
                        // Setting the message String that is going to be send to book seller
                        String message = "I have just bought you book" + bookForSale.getTitle() + " by " + bookForSale.getAuthor() + " for £" + bookForSale.getPrice();
                        // Instance of UserMessage class
                        UserMessage buyMessage = new UserMessage();
                        // Sending private message to Seller
                        buyMessage.sendMessage(userID, bookForSale.getSeller_id(), message);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "You can't buy book from yourself!", Toast.LENGTH_SHORT).show();
                    }
                } catch(NullPointerException e) {
                    Log.e(TAG, "onSuccess: NullPointerException " + e.getMessage());
                }
            }
        });
    }

    /**
     * Method for messaging the Seller
     *
     * Enters Firebase Firestore books_for_sale collection to get value
     * of seller_id field and then start UserMessage activity with
     * extra intent containing sellerID String
     */
    public void messageSeller() {
        bookSaleReference = bookSaleCollection.document(bookSaleIDFeed);
        bookSaleReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                BookSaleModel bookForSale = documentSnapshot.toObject(BookSaleModel.class);
                String userID = fUser.getUid();
                try {
                    if(!userID.equals(bookForSale.getSeller_id())) {
                        String sellerID = bookForSale.getSeller_id();
                        Intent intent = new Intent(getApplicationContext(), UserMessage.class);
                        intent.putExtra("userID", sellerID);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "You can't write message to yourself!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "onSuccess: NullPointerException " + e.getMessage());
                }
            }
        });
    }
}
