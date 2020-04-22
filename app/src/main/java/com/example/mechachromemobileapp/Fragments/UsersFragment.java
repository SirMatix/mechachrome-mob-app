package com.example.mechachromemobileapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Activities.User.UserMessage;
import com.example.mechachromemobileapp.Adapters.UserAdapter;
import com.example.mechachromemobileapp.Models.User;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 *  UsersFragment handles displaying users from same course and mode
 */
public class UsersFragment extends Fragment {

    // Global variables
    private UserAdapter userAdapter;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference userRef = fStore.collection("users");
    private String groupFeed, modeFeed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        // getting arguments from bundle
        assert getArguments() != null;
        groupFeed = getArguments().getString("group");
        modeFeed = getArguments().getString("mode");

        buildRecyclerView(view);
        return view;
    }

    /**
     * buildRecyclerView - builds recycler view to display list of users
     * from the same group and mode that currently logged in user
     *
     * @param view current layout
     */
    private void buildRecyclerView(View view) {
        // query all the users from the same group and mode
        Query query = userRef.whereEqualTo("group", groupFeed).whereEqualTo("mode", modeFeed);

        // build FirestoreRecyclerOptions with query
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        // create adapter and set it on recycler view
        userAdapter = new UserAdapter(options);
        RecyclerView recyclerView = view.findViewById(R.id.user_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(userAdapter);

        // setting on click listener on each recyclerview
        // element to enable starting chat with that user
        userAdapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                // User user = documentSnapshot.toObject(User.class);
                String userID = documentSnapshot.getId();
                Intent intent = new Intent(getContext(), UserMessage.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        userAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        userAdapter.stopListening();
    }
}
