package com.example.mechachromemobileapp.Activities.Library;

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

import com.example.mechachromemobileapp.Models.Books;
import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class AddBook extends AppCompatActivity {

    public static final String TAG = "TAG";
    public Uri imgUri;
    private EditText bookTitle, bookDescription, bookAuthor, bookPages, availableBooks;
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
                    addBook();
                    startActivity(new Intent(AddBook.this, LibraryAdmin.class));
                    finish();
                }
                else {
                    Toast.makeText(AddBook.this,"Please add book image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddBook.this, LibraryAdmin.class);
        startActivity(intent);
        finish();
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
            Toast.makeText(AddBook.this,"Please chose book category", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(availableBooks.getText().toString())){
            availableBooks.setError("Number of available books is required");
            return;
        }
        if(TextUtils.isEmpty(bookPages.getText().toString())) {
            bookPages.setError("Number of pages is required");
            return;
        }
    }

    public void addBook(){
        final Books book = new Books();
        final String title = bookTitle.getText().toString();
        final String author = bookAuthor.getText().toString();

        fieldValidation();

        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(bookDescription.getText().toString());
        book.setCategory(bCategorySpinner.getSelectedItem().toString());
        book.setAvailableBooksNum(Integer.parseInt(availableBooks.getText().toString()));
        book.setTotalBooksNum(Integer.parseInt(availableBooks.getText().toString()));
        book.setPages(Integer.parseInt(bookPages.getText().toString()));
        book.setNumReserved(0);
        book.setNumReviews(0);
        book.setRating(0f);
        book.setAddDate(Calendar.getInstance().getTime());

        // setting document Reference for book object in collection library_books
        final DocumentReference book_data = fStore.collection("library_books").document(title + author);

        // the uploading image function
        final StorageReference reference = mStorageRef.child(title+author+'.'+getExtension(imgUri));
        reference.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                book.setImgUrl(uri.toString());
                                book_data.set(book).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG,"book document created");
                                        Toast.makeText(AddBook.this,"Added Book data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Toast.makeText(AddBook.this,"Image uploaded successfully", Toast.LENGTH_SHORT).show();
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
        bookDescription = findViewById(R.id.numberOfBooks);
        bookAuthor = findViewById(R.id.bookAuthor);
        bookPages = findViewById(R.id.bookPages);
        bookImage = findViewById(R.id.bookImage);
        availableBooks = findViewById(R.id.numberOfBooks);

        // Spinner
        bCategorySpinner = findViewById(R.id.categorySpinner);

        // Button variables
        bAddBtn = findViewById(R.id.addBookBtn);
        bAddImage = findViewById(R.id.addBookImageBtn);

        // Firestore initialization
        fStore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("library_books_image");
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
