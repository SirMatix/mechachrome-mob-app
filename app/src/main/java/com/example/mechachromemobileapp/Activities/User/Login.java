package com.example.mechachromemobileapp.Activities.User;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mechachromemobileapp.Activities.MainActivity;
import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 *  Login Activity
 */
public class Login extends AppCompatActivity {

    public static final String TAG = "Login:";
    private EditText mStudentID,mPassword;
    private Button mLoginBtn;
    private TextView mCreateBtn,mForgotPassword;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        // checks if user is already logged in
        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        setButtons();
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    public void initViews() {
        // Initialization widgets from layout
        mStudentID = findViewById(R.id.studentID);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.login_button);
        mForgotPassword = findViewById(R.id.forgotPassword);

        // Instantiating of Firebase widgets
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        // Initialization of Spinner and Button widgets
        progressBar = findViewById(R.id.progressBar);
        mCreateBtn = findViewById(R.id.registerBtn);
    }
    /**
     *  This method sets the onClickListener to buttons
     */
    public void setButtons() {
        // Button to Login
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Button to open Register User activity
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ResetPassword.class));
            }
        });

    }

    /**
     *  loginUser method
     *
     *  This method login user. To get around email login there is a Collection ids
     *  in Firestore that stores userID as ID and email as field, email is retrieved
     *  from that Collection and used to  login a user
     */
    public void loginUser() {
        // Getting studentID from mStudentID EditText widget
        String studentID = mStudentID.getText().toString().trim();
        // Getting password from mPassword EditText widget
        final String password = mPassword.getText().toString().trim();

        /*
            Field Validation:
            1. studentID - can't be empty
            2. password - can't be empty
            3. password - has to be longer than 6 characters
         */
        if(TextUtils.isEmpty(studentID)){
            mStudentID.setError("studentID is Required.");
            return;
        }

        if(TextUtils.isEmpty(password)){
            mPassword.setError("Password is Required.");
            return;
        }

        if(password.length() < 6){
            mPassword.setError("Password must have minimum 6 characters");
            return;
        }

        // setting progressBar visibility
        progressBar.setVisibility(View.VISIBLE);

        // Getting document from ids collection identified by studentID
        DocumentReference documentReference = fStore.collection("ids").document(studentID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if(document.exists()) {
                        // Getting email from documentSnapshot
                        final String email = Objects.requireNonNull(document.get("email")).toString();
                        //Signing user in with email and password
                        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    // Prompting a message to user about successful login
                                    Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }else {
                                    Toast.makeText(Login.this, "Error:  " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "No such studentID");
                    }
                } else {
                    Log.d(TAG, "onComplete failed message: ", task.getException());
                }
            }
        });
    }

    private void forgotPassword(){

    }
}

