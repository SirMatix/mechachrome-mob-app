package com.example.mechachromemobileapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Activities.User.UserMessage;
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
import java.util.Objects;

/**
 *  ChatFragments handles list of all the chats the user has started with other users
 */
public class ChatsFragment extends Fragment {

    // global variables
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference userRef = fStore.collection("users");
    private CollectionReference chatRef = fStore.collection("chat_rooms");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        readUsers(view);
        return view;
    }

    /**
     * buildRecyclerView method
     *
     * builds RecyclerView with list of users
     *
     * @param view
     * @param userList list of all users, user chats with
     */
    private void buildRecyclerView(View view, final ArrayList<User> userList) {
        RecyclerView recyclerView = view.findViewById(R.id.chats_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        UserInboxAdapter userAdapter = new UserInboxAdapter(userList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(userAdapter);

        userAdapter.setOnItemClickListener(new UserInboxAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String userID = userList.get(position).getId();
                Intent intent = new Intent(getContext(), UserMessage.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }

    /**
     * this method read all the users, current user is chatting with
     * and the calls buildRecyclerView method to fill recycler view
     *
     * @param view with layout in which we operate
     */
    private void readUsers(final View view) {
        readChats(new usersIDListCallback() {
            @Override
            public void onUsersIDList(ArrayList<String> userIDList) {
                // getting all the user data which id matches that from userIDList
                Query query = userRef.whereIn("id",userIDList);
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        // generating chatUser List from queryDocumentSnapshots
                        List<User> chatUsers = Objects.requireNonNull(queryDocumentSnapshots).toObjects(User.class);
                        // creating ArrayList with chatUsers objects
                        ArrayList<User> userList = new ArrayList<>(chatUsers);
                        // building recyclerview
                        buildRecyclerView(view, userList);
                    }
                });
            }
        });
    }

    /**
     * This method goes through all the chatRoom and grabs matching userID data
     *
     * @param callback to return list of users that user chats with
     */
    private void readChats(final usersIDListCallback callback) {

        // getting user information
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        assert fUser != null;
        final String userID = fUser.getUid();

        // forming query based on 'filter' field
        Query query = chatRef.whereArrayContains("filter", userID);

        // listens to query snapshots
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    return;
                }
                // converting query document snapshots to list of ChatRoom class objects
                List<ChatRoom> chatRooms = Objects.requireNonNull(queryDocumentSnapshots).toObjects(ChatRoom.class);
                // creating user ID list
                ArrayList<String> usersIDList = new ArrayList<>();
                // adding 'default' to userID list
                usersIDList.add("default");
                // iteration through chatRooms to get chat members id numbers
                for(ChatRoom chatRoom: chatRooms) {
                    if(chatRoom.getMessageSender().equals(userID)){
                        usersIDList.add(chatRoom.getMessageReceiver());
                    }
                    if(chatRoom.getMessageReceiver().equals(userID)){
                        usersIDList.add(chatRoom.getMessageSender());
                    }
                }
                // saving list on callback
                callback.onUsersIDList(usersIDList);
            }
        });
    }

    /**
     *  interface to pass list on callback
     */
    private interface usersIDListCallback{
        void onUsersIDList(ArrayList<String> userIDList);
    }

}
