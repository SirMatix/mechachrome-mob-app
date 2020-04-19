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

/**
 * UserSetting activity
 *
 * Handles changing variables in Firebase. Implements two interfaces
 * that handle confirmation of password, which is needed to change email
 * or password in Firebase. To do that, user has to be re-authenticated.
 *
 */
public class UserSettings extends AppCompatActivity implements EmailConfirmPasswordDialog.OnEmailConfirmPasswordListener, PasswordConfirmPasswordDialog.OnConfirmPasswordListener {

    // Global variables
    private static final String TAG = "UserSettings: ";
    private EditText changeName, changeSurname, changeEmail, changePassword, confirmPassword;
    private Button changePasswordButton, confirmPasswordButton, confirmChangesButton;
    private Switch changeNameSwitch, changeSurnameSwitch, changeEmailSwitch;
    private FirebaseFirestore fStore;
    private FirebaseUser fUser;
    private FirebaseAuth fAuth;


    /**
     * Interface to confirm user password for changing email
     *
     * @param password got from EmailConfirmPasswordDialog
     */
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
                            // Logging successful re-authentication
                            Log.d(TAG, "User re-authenticated.");
                            // checking if the email is already used inside firebase authentication
                            fAuth.fetchSignInMethodsForEmail(changeEmail.getText().toString().trim().toLowerCase()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        try {
                                            if (task.getResult().getSignInMethods().size() == 1) {
                                                // Informing user of chosen email being in use
                                                Toast.makeText(getApplicationContext(), "That email is already in use", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Updating email
                                                fUser.updateEmail(changeEmail.getText().toString().trim().toLowerCase())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                // Getting userID
                                                                String userID = fUser.getUid();
                                                                // updating data in users Collection
                                                                DocumentReference userReference = fStore.collection("users").document(userID);
                                                                userReference.update("email", changeEmail.getText().toString().trim().toLowerCase());
                                                                userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        // Creating User object from documentSnapshot
                                                                        User user = documentSnapshot.toObject(User.class);
                                                                        assert user != null;
                                                                        // Updating data in ids Collection
                                                                        DocumentReference idsReference = fStore.collection("ids").document(user.getStudentID());
                                                                        idsReference.update("email", changeEmail.getText().toString().trim().toLowerCase());
                                                                        // Message for a User
                                                                        Toast.makeText(getApplicationContext(), "User email address updated", Toast.LENGTH_SHORT).show();
                                                                        // Starting new activity
                                                                        startActivity(new Intent(getApplicationContext(), UserSettings.class));
                                                                        finish();
                                                                    }
                                                                });
                                                            }
                                                        });

                                            }
                                        } catch (NullPointerException e) {
                                            // Logging error message
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

    /**
     *  Interface to confirm user password for changing password
     *
     * @param password got from PasswordConfirmPasswordDialog
     */
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
                            // Updating user password
                            fUser.updatePassword(changePassword.getText().toString().trim());
                            // Prompting success message to user
                            Toast.makeText(getApplicationContext(), "Password successfully changed!", Toast.LENGTH_SHORT).show();
                            // Starting new activity
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

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    public void initViews() {
        // Initialization of Firebase widgets
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        // Initialization of EditText widgets
        changeName = findViewById(R.id.change_name);
        changeSurname = findViewById(R.id.change_surname);
        changeEmail = findViewById(R.id.change_email);
        changePassword = findViewById(R.id.change_password_text);
        confirmPassword = findViewById(R.id.confirm_password_text);
        // Initialization of Button widgets
        changePasswordButton = findViewById(R.id.change_password_button);
        confirmPasswordButton = findViewById(R.id.confirm_password_button);
        confirmChangesButton = findViewById(R.id.confirm_button);
        // Initialization of Switches widgets
        changeNameSwitch = findViewById(R.id.change_name_switch);
        changeSurnameSwitch = findViewById(R.id.change_surname_switch);
        changeEmailSwitch = findViewById(R.id.change_email_switch);
    }

    /**
     * setSwitches method
     *
     * switches are used for the user to chose which variables he wishes to edit.
     * If switch is on it changes the visibility of given element enabling or
     * disabling entry of data into EditText widgets
     */
    public void setSwitches() {
        // Change name switch
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

        // Change surname switch
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

        // Change email switch, prompts message to the user to verify password
        changeEmailSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeEmail.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "To change email you have to confirm your password", Toast.LENGTH_LONG).show();
                } else {
                    changeEmail.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     *  This method sets the onClickListener to buttons
     */
    public void setButtons() {
        // Button to confirm changes made to user data
        confirmChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserData();
            }
        });

        // Change password button, makes EditText widgets visible, prompts
        // message to user that he needs to confirm his password
        // in order to change it
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "To change password you have to confirm your old password", Toast.LENGTH_LONG).show();
                confirmPasswordButton.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.VISIBLE);
                confirmPassword.setVisibility(View.VISIBLE);
                changePasswordButton.setVisibility(View.GONE);
            }
        });

        // Confirms changing of user password
        confirmPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserPassword();
            }
        });

    }

    /**
     * changeUserData
     *
     * If EditText field is visible user is allowed to change his data:
     * --> First Name
     * --> Last Name
     * --> Email
     *
     */
    public void changeUserData() {
        //
        String userID = fUser.getUid();
        DocumentReference userReference = fStore.collection("users").document(userID);

        // Condition to change users first name
        if (changeName.getVisibility() == View.VISIBLE) {
            String newName = changeName.getText().toString().trim();
            if (TextUtils.isEmpty(newName)) {
                changeName.setError("New name is Required.");
                return;
            }
            // updating users name in Firebase
            userReference.update("fname", newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Your first name was changed succesfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), UserSettings.class));
                    finish();
                }
            });
        }

        // Condition to change users last name
        if (changeSurname.getVisibility() == View.VISIBLE) {
            String newSurname = changeSurname.getText().toString().trim();
            if (TextUtils.isEmpty(newSurname)) {
                changeSurname.setError("New surname is Required.");
                return;
            }
            // updating users last name in Firebase
            userReference.update("lname", newSurname).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Your last name was changed succesfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), UserSettings.class));
                    finish();
                }
            });
        }

        // Condition to change users email
        if (changeEmail.getVisibility() == View.VISIBLE) {
            final String newEmail = changeEmail.getText().toString().trim();
            if (TextUtils.isEmpty(newEmail)) {
                changeEmail.setError("New email is Required.");
                return;
            }

            // Opens dialog for user to confirm password
            EmailConfirmPasswordDialog dialog = new EmailConfirmPasswordDialog();
            dialog.show(getSupportFragmentManager(), getString(R.string.confirm_password_dialog));
        }
    }

    /**
     *  changeUserPassword method
     *
     *  if changePassword, confirmPassword EditText widgets are visible user can change password.
     */
    public void changeUserPassword()  {
        if(changePassword.getVisibility() == View.VISIBLE && confirmPassword.getVisibility() == View.VISIBLE) {
            final String newPassword = changePassword.getText().toString().trim();
            final String confirmNewPassword = confirmPassword.getText().toString().trim();

            // Data validation conditions
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

            // Opens dialog for user to confirm password
            PasswordConfirmPasswordDialog dialog = new PasswordConfirmPasswordDialog();
            dialog.show(getSupportFragmentManager(), getString(R.string.confirm_password_dialog));
        }
    }
}