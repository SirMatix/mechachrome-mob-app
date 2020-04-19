package com.example.mechachromemobileapp.Activities.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mechachromemobileapp.Activities.BookSale.BookSale;
import com.example.mechachromemobileapp.Activities.Forum.Forum;
import com.example.mechachromemobileapp.Models.User;
import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private TextView userFirstName, userLastName, userEmail, userGroup, userMode, userStudentID;
    private Button userReservations, userPosts, userBookSale, userReviews;
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
     *  Method for initialization widgets, fields and Firebase instances
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

        // Initialize buttons
        userReservations = findViewById(R.id.reservations_button);
        userPosts = findViewById(R.id.posts_button);
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
     *  loadUserData method
     *
     *  Reads data from Firebase Document Reference
     *  and displays it in appropriate fields
     *
     * @param userReference containing specific user from users Collection
     */
    public void loadUserData(DocumentReference userReference){
        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Creating User instance from documentSnapshot
                User user = documentSnapshot.toObject(User.class);
                String name = "First name: " + Objects.requireNonNull(user).getFname();
                userFirstName.setText(name);
                String last_name = "Last name: " + user.getLname();
                userLastName.setText(last_name);
                String email = "Email: " + user.getEmail();
                userEmail.setText(email);
                String student_id = "Student ID: " + user.getStudentID();
                userStudentID.setText(student_id);
                String group = "Your course: " + user.getGroup();
                userGroup.setText(group);
                String mode = "Your study mode: " + user.getMode();
                userMode.setText(mode);
            }
        });
    }

    /**
     *  This method sets the onClickListener to buttons
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
        userPosts.setOnClickListener(new View.OnClickListener() {
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
    }
}
