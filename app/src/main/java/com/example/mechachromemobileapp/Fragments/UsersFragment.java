package com.example.mechachromemobileapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Adapters.UserAdapter;
import com.example.mechachromemobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    CollectionReference userRef;
    String userID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);



        return view;
    }

    public void initViews(){
        // initialize Firebase elements
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userRef = fStore.collection("users");
        userID = fAuth.getCurrentUser().getUid();

        // user data


    }

    public void buildRecyclerView() {
        Query query = userRef.whereEqualTo("group", "group").whereEqualTo("mode", "mode");


    }

}
