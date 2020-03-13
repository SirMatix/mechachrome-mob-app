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

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText mStudentID,mFirstName,mSurname,mEmail,mPassword,mCnfPassword;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    FirebaseDatabase db;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Data variables
        mStudentID = findViewById(R.id.studentID);
        mFirstName = findViewById(R.id.firstName);
        mSurname = findViewById(R.id.surname);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.password);
        mCnfPassword = findViewById(R.id.cnfpassword);

        // Button variables
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.loginBtn);

        // Firebase initializations
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        db = FirebaseDatabase.getInstance();

        // Progress bar
        progressBar = findViewById(R.id.progressBar);

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

    @Override
    protected void onStart() {
        super.onStart();
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void registerUser(){
        final String studentID = mStudentID.getText().toString();
        final String fname = mFirstName.getText().toString();
        final String lname = mSurname.getText().toString();
        final String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String cnf_pwd = mCnfPassword.getText().toString().trim();
        // checks email field
        if(TextUtils.isEmpty(email)){
            mStudentID.setError("Email is Required.");
            return;
        }
        // checks password field
        if(TextUtils.isEmpty(password)){
            mPassword.setError("Please enter a password.");
            return;
        }
        // compares password and confirm password field
        if(!TextUtils.equals(password, cnf_pwd)) {
            mCnfPassword.setError("Please repeat your password.");
            return;
        }
        // check password length
        if(password.length() < 6){
            mPassword.setError("The password must contains minimum 6 characters.");
            return;
        }
        // sets progressbar visibility
        progressBar.setVisibility(View.VISIBLE);
        //know we can register the user in Firebase
        fAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "Account created", Toast.LENGTH_SHORT).show();
                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference docRef = fStore.collection("users").document(userID);
                    User user = new User(studentID,fname,lname,email);
                    docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"onSuccess: user Profile is created for " + userID);
                        }
                    });
                    DocumentReference docIds = fStore.collection("ids").document(studentID);
                    Map<String,Object> ids = new HashMap<>();
                    ids.put("email",email);
                    docIds.set(ids).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"Ids document created");
                        }
                    });
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }else {
                    Toast.makeText(Register.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    }


