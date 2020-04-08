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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    CollectionReference userRef = fStore.collection("users");
    CollectionReference chatRef = fStore.collection("chat_rooms");
    String groupFeed, modeFeed;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);


        groupFeed = getArguments().getString("group");
        modeFeed = getArguments().getString("mode");

        buildRecyclerView(view);


        return view;
    }


    public void buildRecyclerView(View view) {
        Query query = userRef.whereEqualTo("group", groupFeed).whereEqualTo("mode", modeFeed);


        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        userAdapter = new UserAdapter(options);
        recyclerView = view.findViewById(R.id.user_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(userAdapter);


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

    public int stringToNumber(String old_word){
        char[] word = old_word.toCharArray();
        int numberString = 0;
        for(char letter: word) {
            int a = letter;
            numberString += a;
        }
        return numberString;
    }

}
