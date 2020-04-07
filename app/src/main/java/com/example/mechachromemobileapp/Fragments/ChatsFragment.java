package com.example.mechachromemobileapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Adapters.UserInboxAdapter;
import com.example.mechachromemobileapp.Models.ChatRoom;
import com.example.mechachromemobileapp.Models.User;
import com.example.mechachromemobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter userAdapter;
    private RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    CollectionReference userRef = fStore.collection("users");
    CollectionReference chatRef = fStore.collection("chat_rooms");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        readUsers(view);
        return view;
    }

    public void buildRecyclerView(View view, ArrayList<User> userList) {
        recyclerView = view.findViewById(R.id.chats_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        userAdapter = new UserInboxAdapter(userList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(userAdapter);
    }

    private void readUsers(final View view) {
        readChats(new usersIDListCallback() {
            @Override
            public void onUsersIDList(ArrayList<String> userIDList) {
                Query query = userRef.whereIn("ID",userIDList);
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        List<User> chatUsers = queryDocumentSnapshots.toObjects(User.class);
                        ArrayList<User> userList = new ArrayList<>();
                        userList.addAll(chatUsers);
                        buildRecyclerView(view, userList);
                    }
                });
            }
        });
    }

    private void readChats(final usersIDListCallback callback) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userID = fUser.getUid();
        Query query = chatRef.whereArrayContains("filter", userID);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    return;
                }
                List<ChatRoom> chatRooms = queryDocumentSnapshots.toObjects(ChatRoom.class);
                ArrayList<String> usersIDList = new ArrayList<>();
                usersIDList.add("default");
                for(ChatRoom chatRoom: chatRooms) {
                    if(chatRoom.getMessageSender().equals(userID)){
                        usersIDList.add(chatRoom.getMessageReceiver());
                    }
                    if(chatRoom.getMessageReceiver().equals(userID)){
                        usersIDList.add(chatRoom.getMessageSender());
                    }
                }
                callback.onUsersIDList(usersIDList);
            }
        });
    }

    interface usersIDListCallback{
        void onUsersIDList(ArrayList<String> userIDList);
    }

}
