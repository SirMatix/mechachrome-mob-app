package com.example.mechachromemobileapp.Dialogs;

import android.app.Dialog;
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

public class PasswordConfirmPasswordDialog extends DialogFragment {

    private TextView cancelAction, confirmAction;
    private EditText newPassword;
    private static final String TAG = "ConfirmPasswordDialog";

    public interface OnConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }

    OnConfirmPasswordListener mOnConfirmPasswordListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_password, container, false);
        Log.d(TAG, "onCreateView started");
        initViews(view);
        setOnClicks();
        return view;
    }

    private void initViews(View view) {
        confirmAction = view.findViewById(R.id.dialog_confirm);
        cancelAction = view.findViewById(R.id.dialog_cancel);
        newPassword = view.findViewById(R.id.dialog_password);
    }

    private void setOnClicks() {
        confirmAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the dialog");
                String password = newPassword.getText().toString().trim();
                if(!password.equals("")){
                    mOnConfirmPasswordListener.onConfirmPassword(password);
                    Objects.requireNonNull(getDialog()).dismiss();
                } else {
                    Toast.makeText(getActivity(), "You must enter a password", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
            mOnConfirmPasswordListener = (OnConfirmPasswordListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}

