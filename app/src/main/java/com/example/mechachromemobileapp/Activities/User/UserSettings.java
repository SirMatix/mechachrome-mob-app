package com.example.mechachromemobileapp.Activities.User;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mechachromemobileapp.Dialogs.EmailConfirmPasswordDialog;
import com.example.mechachromemobileapp.Dialogs.PasswordConfirmPasswordDialog;
import com.example.mechachromemobileapp.Models.User;
import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class UserSettings extends AppCompatActivity implements EmailConfirmPasswordDialog.OnEmailConfirmPasswordListener, PasswordConfirmPasswordDialog.OnConfirmPasswordListener {

    private static final String TAG = "UserSettings";
    private EditText changeName, changeSurname, changeEmail, changePassword, confirmPassword;
    private Button changePasswordButton, confirmPasswordButton, confirmChangesButton;
    private Switch changeNameSwitch, changeSurnameSwitch, changeEmailSwitch;
    private FirebaseFirestore fStore;
    private FirebaseUser fUser;
    private FirebaseAuth fAuth;


    @Override
    public void onEmailConfirmPassword(String password) {
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.

        AuthCredential credential = EmailAuthProvider
                .getCredential(Objects.requireNonNull(fUser.getEmail()), password);

        // Prompt the user to re-provide their sign-in credentials
        fUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User re-authenticated.");
                            fAuth.fetchSignInMethodsForEmail(changeEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        try {
                                            if (task.getResult().getSignInMethods().size() == 1) {
                                                Toast.makeText(getApplicationContext(), "That email is already in use", Toast.LENGTH_SHORT).show();
                                            } else {
                                                String userID = fUser.getUid();
                                                DocumentReference userReference = fStore.collection("users").document(userID);
                                                fUser.updateEmail(changeEmail.getText().toString().trim())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(getApplicationContext(), "User eail address updated", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                userReference.update("email", changeEmail.getText().toString().trim());
                                                userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        User user = documentSnapshot.toObject(User.class);
                                                        assert user != null;
                                                        DocumentReference idsReference = fStore.collection("ids").document(user.getStudentID());
                                                        idsReference.update("email", changeEmail.getText().toString().trim());
                                                        startActivity(new Intent(getApplicationContext(), UserSettings.class));
                                                        finish();
                                                    }
                                                });
                                            }
                                        } catch (NullPointerException e) {
                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage());
                                        }

                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "OnComplete: re-authentication failed");
                        }
                    }
                });
    }

    @Override
    public void onConfirmPassword(String password) {
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
                AuthCredential credential = EmailAuthProvider
                        .getCredential(Objects.requireNonNull(fUser.getEmail()), password);

        // Prompt the user to re-provide their sign-in credentials
        fUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            fUser.updatePassword(changePassword.getText().toString().trim());
                            Toast.makeText(getApplicationContext(), "Password succesfully changed!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), UserSettings.class));
                            finish();
                        } else {
                            Log.d(TAG, "onComplete: re-authentication failed");
                        }
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        initViews();
        setSwitches();
        setButtons();
    }

    public void initViews() {
        // initialize Firestore elements
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        // initialize EditText widgets
        changeName = findViewById(R.id.change_name);
        changeSurname = findViewById(R.id.change_surname);
        changeEmail = findViewById(R.id.change_email);
        changePassword = findViewById(R.id.change_password_text);
        confirmPassword = findViewById(R.id.confirm_password_text);
        // initialize Button widgets
        changePasswordButton = findViewById(R.id.change_password_button);
        confirmPasswordButton = findViewById(R.id.confirm_password_button);
        confirmChangesButton = findViewById(R.id.confirm_button);
        // initialize switches
        changeNameSwitch = findViewById(R.id.change_name_switch);
        changeSurnameSwitch = findViewById(R.id.change_surname_switch);
        changeEmailSwitch = findViewById(R.id.change_email_switch);
    }

    public void setSwitches() {
        changeNameSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeName.setVisibility(View.VISIBLE);
                } else {
                    changeName.setVisibility(View.GONE);
                }
            }
        });

        changeSurnameSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeSurname.setVisibility(View.VISIBLE);
                } else {
                    changeSurname.setVisibility(View.GONE);
                }
            }
        });

        changeEmailSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeEmail.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "To change email ypu have to login again to confirm credentials", Toast.LENGTH_LONG).show();
                } else {
                    changeEmail.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setButtons() {
        confirmChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserData();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPasswordButton.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.VISIBLE);
                confirmPassword.setVisibility(View.VISIBLE);
                changePasswordButton.setVisibility(View.GONE);
            }
        });

        confirmPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserPassword();
            }
        });

    }

    public void changeUserData() {
        String userID = fUser.getUid();
        DocumentReference userReference = fStore.collection("users").document(userID);
        if (changeName.getVisibility() == View.VISIBLE) {
            String newName = changeName.getText().toString().trim();
            if (TextUtils.isEmpty(newName)) {
                changeName.setError("New name is Required.");
                return;
            }
            userReference.update("fname", newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Your first name was changed succesfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), UserSettings.class));
                    finish();
                }
            });
        }
        if (changeSurname.getVisibility() == View.VISIBLE) {
            String newSurname = changeSurname.getText().toString().trim();
            if (TextUtils.isEmpty(newSurname)) {
                changeSurname.setError("New surname is Required.");
                return;
            }
            userReference.update("lname", newSurname).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Your last name was changed succesfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), UserSettings.class));
                    finish();
                }
            });
        }
        if (changeEmail.getVisibility() == View.VISIBLE) {
            final String newEmail = changeEmail.getText().toString().trim();
            if (TextUtils.isEmpty(newEmail)) {
                changeEmail.setError("New email is Required.");
                return;
            }
            EmailConfirmPasswordDialog dialog = new EmailConfirmPasswordDialog();
            dialog.show(getSupportFragmentManager(), getString(R.string.confirm_password_dialog));
        }
    }

    public void changeUserPassword()  {
        if(changePassword.getVisibility() == View.VISIBLE && confirmPassword.getVisibility() == View.VISIBLE) {
            final String newPassword = changePassword.getText().toString().trim();
            final String confirmNewPassword = confirmPassword.getText().toString().trim();
            if (newPassword.length() < 6) {
                changePassword.setError("Password has to be minimum 6 characters");
                return;
            }
            if (TextUtils.isEmpty(newPassword)) {
                changePassword.setError("Password is Required.");
                return;
            }
            if (TextUtils.isEmpty(confirmNewPassword)) {
                confirmPassword.setError("You have to confirm your password");
                return;
            }
            if (!TextUtils.equals(newPassword, confirmNewPassword)) {
                confirmPassword.setError("Passwords don't match");
                return;
            }
            PasswordConfirmPasswordDialog dialog = new PasswordConfirmPasswordDialog();
            dialog.show(getSupportFragmentManager(), getString(R.string.confirm_password_dialog));
        }
    }
}