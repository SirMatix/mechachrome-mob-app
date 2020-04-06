package com.example.mechachromemobileapp.Activities.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mechachromemobileapp.Adapters.MessageAdapter;
import com.example.mechachromemobileapp.Models.ChatMessage;
import com.example.mechachromemobileapp.Models.User;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserMessage extends AppCompatActivity {

    CircleImageView userImage;
    TextView username;
    FloatingActionButton sendMessageButton;
    EditText messageText;

    FirebaseUser fUser;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    CollectionReference userRef;
    DocumentReference userDocument;

    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message);
        initViews();
        buildRecyclerView();
    }

    public void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        userImage = findViewById(R.id.user_image);
        username = findViewById(R.id.user_name);
        sendMessageButton = findViewById(R.id.send_message_button);
        messageText = findViewById(R.id.message_input);
        messageRecyclerView = findViewById(R.id.inbox_recycler_view);

        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString();
                if (!message.equals("")) {
                    sendMessage(fUser.getUid(), userID, message);
                } else {
                    Toast.makeText(getApplicationContext(), "You cannot send empty message!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fStore = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = fStore.collection("users");
        userDocument = userRef.document(userID);

        userDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                String fullName = user.getFname() + " " + user.getLname();
                username.setText(fullName);
                if(user.getImgUrl().equals("default")) {
                    userImage.setImageResource(R.drawable.ic_account);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImgUrl()).into(userImage);
                }
            }
        });
    }

    public void sendMessage(String sender, String receiver, String message) {
        CollectionReference chatReference = FirebaseFirestore.getInstance().collection("chat_rooms");
        String chatID = sender + "_" + receiver;
        DocumentReference privateChat = chatReference.document(chatID);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        privateChat.set(hashMap);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageSender(sender);
        chatMessage.setMessageReceiver(receiver);
        chatMessage.setMessageText(message);
        chatMessage.setMessageTime(new Date().getTime());

        CollectionReference privateChatMessage = privateChat.collection("messages");
        privateChatMessage.document().set(chatMessage);
    }

    public void buildRecyclerView() {
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String chatID = fUser.getUid() + "_" + userID;
        CollectionReference chatReference = fStore.collection("chat_rooms").document(chatID).collection("messages");

        Query query = chatReference.orderBy("messageTime", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();

        messageAdapter = new MessageAdapter(options);
        messageRecyclerView.setHasFixedSize(true);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false ));
        messageRecyclerView.setAdapter(messageAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        messageAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageAdapter.stopListening();
    }
}
