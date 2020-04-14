package com.example.mechachromemobileapp.Activities.BookSale;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.Calendar;

public class AddSaleBook extends AppCompatActivity {

    public static final String TAG = "TAG";
    public Uri imgUri;
    private EditText bookTitle, bookDescription, bookAuthor, bookPages, bookPrice;
    private ImageView bookImage;
    private Button bAddBtn, bAddImage;
    private Spinner bCategorySpinner, bConditionSpinner;
    private FirebaseFirestore fStore;
    private StorageReference mStorageRef;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_sale);

        initElements();

        initCategorySpinner();
        initConditionSpinner();

        bAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

        bAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgUri != null) {
                    addBookSale();
                    startActivity(new Intent(getApplicationContext(), com.example.mechachromemobileapp.Activities.BookSale.BookSale.class));
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please add book image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fieldValidation() {
        if(TextUtils.isEmpty(bookTitle.getText().toString())){
            bookTitle.setError("Title is required");
            return;
        }
        if(TextUtils.isEmpty(bookAuthor.getText().toString())){
            bookAuthor.setError("Author is required");
            return;
        }
        if(TextUtils.isEmpty(bookDescription.getText().toString())){
            bookDescription.setError("Description is required");
            return;
        }
        if(TextUtils.isEmpty(bCategorySpinner.getSelectedItem().toString())){
            Toast.makeText(getApplicationContext(),"Please chose book category", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(bookPages.getText().toString())) {
            bookPages.setError("Number of pages is required");
            return;
        }
    }

    public void addBookSale(){
        final BookSaleModel sellBook = new BookSaleModel();
        final String title = bookTitle.getText().toString();
        final String author = bookAuthor.getText().toString();

        fieldValidation();

        sellBook.setTitle(title);
        sellBook.setAuthor(author);
        sellBook.setDescription(bookDescription.getText().toString());
        sellBook.setCategory(bCategorySpinner.getSelectedItem().toString());
        sellBook.setCondition(bConditionSpinner.getSelectedItem().toString());
        sellBook.setPages(Integer.parseInt(bookPages.getText().toString()));
        sellBook.setAddDate(Calendar.getInstance().getTime());
        sellBook.setPrice(Float.parseFloat(bookPrice.getText().toString()));
        sellBook.setRating(0f);
        sellBook.setNumReviews(0);
        sellBook.setNumReserved(0);
        sellBook.setTotalBooksNum(1);
        sellBook.setAvailableBooksNum(1);
        sellBook.setSeller_id(fAuth.getCurrentUser().getUid());
        sellBook.setSold(false);


        // setting document Reference for book object in collection books_for_sale
        final DocumentReference sale_book_data = fStore.collection("books_for_sale").document();

        // the uploading image function
        final StorageReference reference = mStorageRef.child(title+'.'+getExtension(imgUri));
        reference.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                sellBook.setImgUrl(uri.toString());
                                sale_book_data.set(sellBook).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG,"book sale document created");
                                        Toast.makeText(getApplicationContext(),"Added Book data", Toast.LENGTH_SHORT).show();
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
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    private String getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType((uri)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imgUri = data.getData();
            bookImage.setImageURI(imgUri);
            bookImage.setVisibility(View.VISIBLE);
        }
    }

    public void initElements() {
        // Data variables
        bookTitle = findViewById(R.id.bookTitle);
        bookDescription = findViewById(R.id.bookDescription);
        bookAuthor = findViewById(R.id.bookAuthor);
        bookPages = findViewById(R.id.bookPages);
        bookImage = findViewById(R.id.bookImage);
        bookPrice = findViewById(R.id.bookPrice);

        // Spinner
        bCategorySpinner = findViewById(R.id.categorySpinner);
        bConditionSpinner = findViewById(R.id.conditionSpinner);

        // Button variables
        bAddBtn = findViewById(R.id.addBookBtn);
        bAddImage = findViewById(R.id.addBookImageBtn);

        // Firestore initialization
        fStore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("books_for_sale");
        fAuth =FirebaseAuth.getInstance();
    }

    public void initCategorySpinner() {
        String[] CATEGORY_LIST = {"Pick book category", "Science", "Maths", "Economics", "Business", "Computing"};

        final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, CATEGORY_LIST) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
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

    public void initConditionSpinner() {
        String[] CONDITION_LIST = {"Select book condition", "New", "Used - great", "Used - good", "Used - medium", "Used - bad"};

        final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, CONDITION_LIST) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
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
        bConditionSpinner.setAdapter(categoryAdapter);
    }

}
