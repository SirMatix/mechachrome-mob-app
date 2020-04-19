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
import com.example.mechachromemobileapp.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {

    private static final String TAG = "TAG";
    private EditText mStudentID,mFirstName,mSurname,mEmail,mPassword,mCnfPassword;
    private Button mRegisterBtn;
    private TextView mLoginBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private ProgressBar progressBar;
    private FirebaseDatabase db;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        setButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    private void initViews() {
        // Initialization widgets from layout
        mStudentID = findViewById(R.id.studentID);
        mFirstName = findViewById(R.id.firstName);
        mSurname = findViewById(R.id.surname);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.password);
        mCnfPassword = findViewById(R.id.confirm_password_button);

        // Initialization of Button widgets
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.loginBtn);

        // Instantiating of Firebase widgets
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        db = FirebaseDatabase.getInstance();

        // Progress bar initialization
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     *  This method sets the onClickListener to buttons
     */
    private void setButtons() {
        // When you click Register
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // When you click Login text
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }

    /**
     *  registerUser method
     *
     *  Captures data from EditText widgets in the layout, after
     *  validation saves them in local variables; based on that data
     *  creates User object, registers user in Firebase, sets User
     *  object to users Collection document Reference
     */
    private void registerUser(){
        // Local variables for holding data from layout
        final String studentID = mStudentID.getText().toString();
        final String fname = mFirstName.getText().toString();
        final String lname = mSurname.getText().toString();
        final String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String cnf_pwd = mCnfPassword.getText().toString().trim();

        /*
            Field Validation:
            1. studentID can't be empty
            2. email can't be empty
            3. password can't be empty
            4. password can't have less than 6 characters
            5. password can't be only numbers                 ---> not implemented yet
            6. password and compare password must match
         */
        // checks studentID field
        if(TextUtils.isEmpty(studentID)){
            mStudentID.setError("studentID is Required.");
            return;
        }
        // checks email field
        if(TextUtils.isEmpty(email)){
            mEmail.setError("Email is Required.");
            return;
        }
        // checks password field
        if(TextUtils.isEmpty(password)){
            mPassword.setError("Please enter a password.");
            return;
        }
        // check password length
        if(password.length() < 6){
            mPassword.setError("The password must contains minimum 6 characters.");
            return;
        }
        /* condition to check if password contains only numbers
        if(){
            mPassword.setError("The password must contains minimum 6 characters.");
            return;
        }
         */
        // compares password and confirms password field
        if(!TextUtils.equals(password, cnf_pwd)) {
            mCnfPassword.setError("Please repeat your password.");
            return;
        }

        // sets progressbar visibility
        progressBar.setVisibility(View.VISIBLE);
        // creating user with email and password
        fAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "Account created", Toast.LENGTH_SHORT).show();
                    userID = fAuth.getCurrentUser().getUid();
                    // Creating a user document in users collection in Firebase Firestore
                    DocumentReference docRef = fStore.collection("users").document(userID);
                    // instance of User class
                    User user = new User();
                    // setting user variables
                    user.setFname(fname);
                    user.setLname(lname);
                    user.setEmail(email);
                    user.setId(userID);
                    user.setImgUrl("default");
                    user.setPermission("user");
                    user.setStudentID(studentID);
                    // setting user object to users collection user document in Firestore
                    docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"onSuccess: user Profile is created for " + userID);
                        }
                    });
                    // setting ids document in Firestore, it will be used to login user with studentID
                    DocumentReference docIds = fStore.collection("ids").document(studentID);
                    Map<String,Object> ids = new HashMap<>();
                    ids.put("email",email);
                    docIds.set(ids).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"Ids document created");
                        }
                    });
                    // starting MainActivity after successful user registration
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }else {
                    // prompting error message to user
                    Toast.makeText(Register.this, "Error! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}


