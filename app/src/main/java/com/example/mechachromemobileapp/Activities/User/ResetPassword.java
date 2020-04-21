package com.example.mechachromemobileapp.Activities.User;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity for resetting users password
 */
public class ResetPassword extends AppCompatActivity {

    // Global variables
    private EditText userEmail;
    private TextView backButton;
    private Button resetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initViews();
        setButtons();
    }

    /**
     * Method for initialization widgets, fields and Firebase instances
     */
    private void initViews() {
        userEmail = findViewById(R.id.input_email);
        backButton = findViewById(R.id.back_button);
        resetPassword = findViewById(R.id.reset_button);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
    }
    /**
     *  This method sets the onClickListener to buttons
     */
    private void setButtons() {
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * resetPassword()
     *
     * This method takes in user email and using FIREBASE AUTH
     * sends email with instructions on how to reset password
     * to email specified by user
     *
     */
    private void resetPassword() {
        // local variable to hold inputted email
        String email = userEmail.getText().toString().trim();

        // email validation condition
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        // using FirebaseAuth to send reset password email to user
        fAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // message to user of successful email sent
                            Toast.makeText(getApplicationContext(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            // message if sending failed
                            Toast.makeText(getApplicationContext(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
