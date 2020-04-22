package com.example.mechachromemobileapp.Activities.BookSale;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mechachromemobileapp.Models.BookSaleModel;
import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Objects;

/**
 *  Activity for adding a new book for sale by a user
 */
public class AddSaleBook extends AppCompatActivity {

    // Global variables
    private static final String TAG = "AddSaleBook";
    private Uri imgUri;
    private int REQUEST_CODE_FOR_FILE = 1;
    private int REQUEST_CODE_FOR_IMAGE = 1000;
    private Bitmap capturedPhoto;
    private EditText bookTitle, bookDescription, bookAuthor, bookPages, bookPrice;
    private ImageView bookImage;
    private Button bAddBtn, bAddImage, bTakePhoto;
    private Spinner bCategorySpinner, bConditionSpinner;
    private FirebaseFirestore fStore;
    private StorageReference mStorageRef;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_sale);
        initViews();
        initCategorySpinner();
        initConditionSpinner();
        setButtons();
    }


    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    public void initViews() {
        // Initialization EditText widgets from layout
        bookTitle = findViewById(R.id.bookTitle);
        bookDescription = findViewById(R.id.bookDescription);
        bookAuthor = findViewById(R.id.bookAuthor);
        bookPages = findViewById(R.id.bookPages);
        bookImage = findViewById(R.id.bookImage);
        bookPrice = findViewById(R.id.bookPrice);

        // Initialization of Spinner widgets from layout
        bCategorySpinner = findViewById(R.id.categorySpinner);
        bConditionSpinner = findViewById(R.id.conditionSpinner);

        // Initialization of Button widgets from layout
        bAddBtn = findViewById(R.id.addBookBtn);
        bAddImage = findViewById(R.id.chooseBookFileButton);
        bTakePhoto = findViewById(R.id.takeBookPhotoButton);

        // Instantiating of Firebase widgets
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("books_for_sale/"+fAuth.getUid()+" user books");
    }

    /**
     *  Method initializing a Spinner widget that
     *  lets user choose categories of book for sale
     */
    public void initCategorySpinner() {
        // List of available book categories
        String[] CATEGORY_LIST = {"Pick book category", "Science", "Maths", "Economics", "Business", "Computing"};

        // ArrayAdapter for showing conditions from constant condition list it utilizes custom_spinner_item layout
        final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, CATEGORY_LIST) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.argb(128,148,49,49));
                } else {
                    tv.setTextColor(Color.argb(205, 148, 49, 49));
                }
                return view;
            }
        };
        categoryAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        bCategorySpinner.setAdapter(categoryAdapter);
    }

    /**
     *  Method initializing a Spinner widget that
     *  lets user choose condition of book for sale
     */
    public void initConditionSpinner() {
        // List of available book conditions
        String[] CONDITION_LIST = {"Select book condition", "New", "Used - good", "Used - medium", "Used - bad"};

        // ArrayAdapter for showing conditions from constant condition list it utilizes custom_spinner_item layout
        final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, CONDITION_LIST) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.argb(128,148,49,49));
                } else {
                    // Set other elements color
                    tv.setTextColor(Color.argb(255, 0, 0, 0));
                }
                return view;
            }
        };
        categoryAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        bConditionSpinner.setAdapter(categoryAdapter);
    }

    /**
     *  This method sets the onClickListener to buttons
     */
    public void setButtons() {
        // Button for adding book image
        bAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

        // Button for taking picture of a book
        bTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        // Button for adding book for sale to the Firebase
        bAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    condition that checks if book image is present
                    if there is no book image toast a message that
                    image is required if image is present
                    add book for sale and start a new activity
                 */
                if(imgUri != null || capturedPhoto != null) {
                    addBookSale();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please add book image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     *  Method for adding a book for sale
     *  utilizes fieldValidation method to check the fields,
     *  before setting the variables to
     *
     */
    public void addBookSale(){
        // local method variables
        final BookSaleModel sellBook = new BookSaleModel();
        final String title = bookTitle.getText().toString();
        final String author = bookAuthor.getText().toString();

        ////////// FIELD VALIDATION SECTION //////////

        // checking if bookTitle field is empty
        if(TextUtils.isEmpty(bookTitle.getText().toString())){
            // sets error message on bookTitle field
            bookTitle.setError("Title is required");
            return;
        }
        // checking if bookAuthor field is empty
        if(TextUtils.isEmpty(bookAuthor.getText().toString())){
            // sets error message on bookAuthor field
            bookAuthor.setError("Author is required");
            return;
        }
        // checking if bookDescription field is empty
        if(TextUtils.isEmpty(bookDescription.getText().toString())){
            // sets error message on bookDescription field
            bookDescription.setError("Description is required");
            return;
        }
        // checking if bConditionSpinner field is empty
        if(TextUtils.equals(bConditionSpinner.getSelectedItem().toString(), "Select book condition")){
            // Toasts message to choose book condition before adding book
            Toast.makeText(getApplicationContext(),"Please choose book condition", Toast.LENGTH_SHORT).show();
            return;
        }
        // checking if bCategorySpinner field is empty
        if(TextUtils.equals(bCategorySpinner.getSelectedItem().toString(), "Pick book category")){
            // Toasts message to choose book category before adding book
            Toast.makeText(getApplicationContext(),"Please choose book category", Toast.LENGTH_SHORT).show();
            return;
        }
        // checking if bookPages field is empty
        if(TextUtils.isEmpty(bookPages.getText().toString())) {
            // sets error message on bookPages field
            bookPages.setError("Number of pages is required");
            return;
        }
        // checking if bookPrice field is empty
        if(TextUtils.isEmpty(bookPrice.getText().toString())) {
            // sets error message on bookPages field
            bookPrice.setError("Book price is required");
            return;
        }

        ////////// SETTING VALUES SECTION //////////

        /* setting BookSaleModel object sellBook title variable to content from bookTitle EditText widget saved in String title */
        sellBook.setTitle(title);
        /* setting BookSaleModel object sellBook author variable to content from bookAuthor EditText widget saved in String author */
        sellBook.setAuthor(author);
        /* setting BookSaleModel object sellBook description variable to content from bookDescription EditText widget */
        sellBook.setDescription(bookDescription.getText().toString());
        /* setting BookSaleModel object sellBook category variable to content from element selected from bCategorySpinner widget */
        sellBook.setCategory(bCategorySpinner.getSelectedItem().toString());
        /* setting BookSaleModel object sellBook condition variable to content from element selected from bConditionSpinner widget */
        sellBook.setCondition(bConditionSpinner.getSelectedItem().toString());
        /* setting BookSaleModel object sellBook pages variable to content from bookPages EditText widget */
        sellBook.setPages(Integer.parseInt(bookPages.getText().toString()));
        /* setting BookSaleModel object sellBook addDate variable to actual date timestamp */
        sellBook.setAddDate(Calendar.getInstance().getTime());
        /* setting BookSaleModel object sellBook price variable to content from bookPrice EditText widget */
        sellBook.setPrice(Float.parseFloat(bookPrice.getText().toString()));
        /*
            setting BookSaleModel object sellBook rating element to Float 0,
            because BookSale is a child element of Book class we can't
            leave this element as null because Firestore will return error
            the rule is the same for all elements below
         */
        sellBook.setRating(0f);
        sellBook.setNumReviews(0);
        sellBook.setNumReserved(0);
        sellBook.setTotalBooksNum(1);
        sellBook.setAvailableBooksNum(1);
        sellBook.setISBN("ISBN NUMBER");
        /* setting BookSaleModel object sellBook seller_id variable to ID of the current user who is adding the book */
        sellBook.setSeller_id(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
        /* setting BookSaleModel object sellBook sold variable to false, after user buys book this variable changes to true */
        sellBook.setSold(false);


        // setting document Reference for book object in collection books_for_sale
        final DocumentReference sale_book_data = fStore.collection("books_for_sale").document();

        ////////// IMAGE LOADING SECTION //////////

        // loading file image
        if (imgUri != null) {
            // the uploading image function
            final StorageReference reference = mStorageRef.child(title+"_photo."+getExtension(imgUri));
            reference.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                /*
                                    setting BookSaleModel object sellBook imgUrl variable
                                    to content of uri variable that is passed from onSuccess method
                                 */
                                    sellBook.setImgUrl(uri.toString());
                                    /*  setting the SellBookModel object sellBook to DocumentReference sell_book_data */
                                    sale_book_data.set(sellBook).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG,"onSuccess: sellBook object added to Firestore books_for_sale Collection");
                                            Toast.makeText(getApplicationContext(),"Congratulations! You have successfully added book for sale", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), com.example.mechachromemobileapp.Activities.BookSale.BookSale.class));
                                            finish();
                                        }
                                    });
                                    Toast.makeText(getApplicationContext(),"Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e(TAG, "onFailure: Failed to upload picture, error: " + exception.getMessage());
                        }
                    });
        }
        // loading taken picture
        if (capturedPhoto != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            capturedPhoto.compress(Bitmap.CompressFormat.JPEG, 100,byteArrayOutputStream);

            final StorageReference reference = mStorageRef.child(title+"_photo.jpeg");
            reference.putBytes(byteArrayOutputStream.toByteArray())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            /*
                                    setting BookSaleModel object sellBook imgUrl variable
                                    to content of uri variable that is passed from onSuccess method
                                 */
                            sellBook.setImgUrl(uri.toString());
                            /*  setting the SellBookModel object sellBook to DocumentReference sell_book_data */
                            sale_book_data.set(sellBook).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"onSuccess: sellBook object added to Firestore books_for_sale Collection");
                                    Toast.makeText(getApplicationContext(),"Congratulations! You have successfully added book for sale", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), com.example.mechachromemobileapp.Activities.BookSale.BookSale.class));
                                    finish();
                                }
                            });
                            Toast.makeText(getApplicationContext(),"Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e.getCause());

                }
            });
        }
    }

    /**
     *  Method to choose a file from device storage
     */
    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQUEST_CODE_FOR_FILE);
    }

    /**
     * Method to take photo using camera
     */
    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE);
        }
    }

    /**
     * Method to get file extension
     *
     * @param uri uniform resource identifier parameter
     * @return extension of uri
     */
    private String getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        Log.d(TAG, "Mime type check: " + mimeTypeMap.getExtensionFromMimeType(contentResolver.getType((uri))));
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType((uri)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_FOR_FILE && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imgUri = data.getData();
            bookImage.setImageURI(imgUri);
            bookImage.setVisibility(View.VISIBLE);
            // this stops user from adding two photos for one book, sets captured photo to null
            capturedPhoto = null;
        }
        if (requestCode == REQUEST_CODE_FOR_IMAGE) {
            switch(resultCode) {
                case RESULT_OK:
                    Log.i(TAG, "onActivityResult: RESULT OK");
                    capturedPhoto = (Bitmap) Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).get("data");
                    bookImage.setImageBitmap(capturedPhoto);
                    bookImage.setVisibility(View.VISIBLE);
                    // this stops user from adding two photos for one book, sets imgUri to null
                    imgUri = null;
                    break;
                case RESULT_CANCELED:
                    Log.i(TAG, "onActivityResult: RESULT CANCELLED");
                    break;
                default:
            }
        }
    }


}
