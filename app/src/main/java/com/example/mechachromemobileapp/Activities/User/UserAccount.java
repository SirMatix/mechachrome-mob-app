package com.example.mechachromemobileapp.Activities.User;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mechachromemobileapp.Activities.BookSale.BookSale;
import com.example.mechachromemobileapp.Activities.Forum.Forum;
import com.example.mechachromemobileapp.Models.User;
import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

/**
 * UserAccount activity
 *
 * This activity displays all data of a current user;
 * Buttons leading to pages showing user activity within the app
 *
 */
public class UserAccount extends AppCompatActivity {


    // Global variables
    private static final String TAG = "UserAccount: ";
    private static final int REQUEST_CODE_FOR_FILE = 1;
    private TextView userFirstName, userLastName, userEmail, userGroup, userMode, userStudentID, defaultImage, changeImageButton;
    private Uri imgUri;
    private ImageView userImage;
    private Button userReservations, userTopics, userBookSale, userReviews;
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        initViews();
        loadUserData(getUserReference());
        setButtons();
    }

    /**
     * Method for initialization widgets, fields and Firebase instances
     */
    public void initViews() {
        // Initialize Firebase Instances
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        // Initialize layout variables
        userFirstName = findViewById(R.id.user_first_name);
        userLastName = findViewById(R.id.user_last_name);
        userEmail = findViewById(R.id.user_email);
        userGroup = findViewById(R.id.user_group);
        userMode = findViewById(R.id.user_mode);
        userStudentID = findViewById(R.id.user_student_id);
        userImage = findViewById(R.id.user_image);
        // Clickable TextView
        defaultImage = findViewById(R.id.change_to_default);
        changeImageButton = findViewById(R.id.change_image_button);

        // Initialize buttons
        userReservations = findViewById(R.id.reservations_button);
        userTopics = findViewById(R.id.topics_button);
        userBookSale = findViewById(R.id.books_for_sale_button);
        userReviews = findViewById(R.id.book_reviews);

    }

    /**
     * getUserReference method
     *
     * @return a specific user reference from users Collection
     */
    public DocumentReference getUserReference() {
        String userID = fUser.getUid();
        return fStore.collection("users").document(userID);
    }

    /**
     * loadUserData method
     * <p>
     * Reads data from Firebase Document Reference
     * and displays it in appropriate fields
     *
     * @param userReference containing specific user from users Collection
     */
    public void loadUserData(DocumentReference userReference) {
        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Creating User instance from documentSnapshot
                User user = documentSnapshot.toObject(User.class);
                userFirstName.setText(Objects.requireNonNull(user).getFname());
                userLastName.setText(user.getLname());
                userEmail.setText(user.getEmail());
                userStudentID.setText(user.getStudentID());
                userGroup.setText(user.getGroup());
                userMode.setText(user.getMode());
                if (user.getImgUrl().equals("default")) {
                    userImage.setImageResource(R.drawable.ic_account);
                    defaultImage.setVisibility(View.GONE);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImgUrl()).into(userImage);
                    defaultImage.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * This method sets the onClickListener to buttons
     */
    public void setButtons() {
        final String userID = fUser.getUid();
        // Button to show all the reservations made by a user
        userReservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserBookReservations.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
        // Button to show all the posts written by a user
        userTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Forum.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
        // Button to show all the book being sold by a user
        userBookSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookSale.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
        // Button to show all reviews written by a user
        userReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserBookReviews.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        // Button to choose a picture from storage and upload to Firebase Storage
        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

        // Button to set picture to default
        defaultImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultImage();
            }
        });

    }

    /**
     * Method to choose a file from device storage
     */
    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_FOR_FILE);
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

    /**
     * Method to set image to default
     */
    private void setDefaultImage() {
        final DocumentReference user_data = getUserReference();
        user_data.update("imgUrl", "default");
        userImage.setImageResource(R.drawable.ic_account);
        defaultImage.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            userImage.setImageURI(imgUri);
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("user_images/user_"+fAuth.getUid());
            final StorageReference reference = mStorageRef.child(fAuth.getUid()+"_profileImage."+getExtension(imgUri));
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
                                    DocumentReference userRefenence = getUserReference();
                                    userRefenence.update("imgUrl", uri.toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            /*  setting the SellBookModel object sellBook to DocumentReference sell_book_data */
                                            Toast.makeText(getApplicationContext(),"Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
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
    }


}
