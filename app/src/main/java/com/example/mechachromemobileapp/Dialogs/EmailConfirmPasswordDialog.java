package com.example.mechachromemobileapp.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mechachromemobileapp.R;

import java.util.Objects;

/**
 *  Dialog for confirming password while changing user email
 */
public class EmailConfirmPasswordDialog extends DialogFragment {

    // global variables
    private TextView cancelAction, confirmAction;
    private EditText newPassword;
    private static final String TAG = "ConfirmPasswordDialog";

    public interface OnEmailConfirmPasswordListener{
        void onEmailConfirmPassword(String password);
    }

    private OnEmailConfirmPasswordListener mOnConfirmPasswordListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_password, container, false);
        Log.d(TAG, "onCreateView started");
        initViews(view);
        setOnClicks();
        return view;
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    private void initViews(View view) {
        confirmAction = view.findViewById(R.id.dialog_confirm);
        cancelAction = view.findViewById(R.id.dialog_cancel);
        newPassword = view.findViewById(R.id.dialog_password);
    }

    /**
     *  Method that sets listeners to TextView elements in Dialog
     */
    private void setOnClicks() {
        // confirm action on click
        confirmAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the dialog");
                // gets password from dialog layout field
                String password = newPassword.getText().toString().trim();
                // password can't be empty
                if(!password.equals("")){
                    // passes data to interface
                    mOnConfirmPasswordListener.onEmailConfirmPassword(password);
                    Objects.requireNonNull(getDialog()).dismiss();
                } else {
                    Toast.makeText(getActivity(), "You must enter a password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // cancel action on click
        cancelAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the dialog");
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mOnConfirmPasswordListener = (OnEmailConfirmPasswordListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
