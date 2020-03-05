package com.example.mechachromemobileapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddBook extends AppCompatActivity {

    public static final String TAG = "TAG";
    public Uri imgUri;
    private EditText bookTitle, bookDescription, bookAuthor, bookPages;
    private ImageView bookImage;
    private Button bAddBtn, bAddImage;
    private Spinner bCategorySpinner;
    private FirebaseFirestore fStore;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        initElements();

        initSpinner();

        // getting the image
        bAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

        bAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();
            }

        });
    }

    public void addBook(){
        // getting data from layout variables
        String title =  bookTitle.getText().toString();
        String description = bookDescription.getText().toString();
        String author =  bookAuthor.getText().toString();
        String category = bCategorySpinner.getSelectedItem().toString();
        int pages = Integer.parseInt(bookPages.getText().toString());
        int numReviews = 0;
        int rating = 0;
        int numRatings = 0;

        // setting document Reference for book object in collection library_books
        DocumentReference book_data = fStore.collection("library_books").document(title+author);

        // initializing book object
        Books book = new Books(title, author, description, category, pages, numReviews, rating, numRatings);

        book_data.set(book).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"book document created");
                Toast.makeText(AddBook.this,"Added Book data", Toast.LENGTH_SHORT).show();
            }
        });

        uploadFile(title,author);
    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    private void uploadFile(String title, String author) {
        StorageReference reference = mStorageRef.child(title+author+'.'+getExtension(imgUri));

        reference.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        Toast.makeText(AddBook.this,"Image uploaded successfully", Toast.LENGTH_SHORT).show();
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
        bookDescription = findViewById(R.id.numberOfBooks);
        bookAuthor = findViewById(R.id.bookAuthor);
        bookPages = findViewById(R.id.bookPages);
        bookImage = findViewById(R.id.bookImage);

        // Spinner
        bCategorySpinner = findViewById(R.id.categorySpinner);

        // Button variables
        bAddBtn = findViewById(R.id.addBookBtn);
        bAddImage = findViewById(R.id.addBookImageBtn);

        // Firestore initialization
        fStore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("book_images");
    }

    public void initSpinner() {
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
}
