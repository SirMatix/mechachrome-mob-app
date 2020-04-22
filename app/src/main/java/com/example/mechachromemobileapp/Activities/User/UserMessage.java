package com.example.mechachromemobileapp.Activities.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.mechachromemobileapp.Models.ChatRoom;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * UserMessage Activity
 *
 * Handles displaying of messages between Users
 */
public class UserMessage extends AppCompatActivity {

    // Global variables
    private final static String TAG = "UserMessage: ";
    private CircleImageView userImage;
    private TextView username;
    private EditText messageText;
    private FirebaseUser fUser;
    private FirebaseFirestore fStore;
    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message);
        initViews();
        buildRecyclerView();
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    public void initViews() {
        // Toolbar initialization
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialization of layout widgets
        userImage = findViewById(R.id.user_image);
        username = findViewById(R.id.user_full_name);
        FloatingActionButton sendMessageButton = findViewById(R.id.send_message_button);
        messageText = findViewById(R.id.message_input);
        messageRecyclerView = findViewById(R.id.inbox_recycler_view);

        // Getting user info from previous activity
        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");

        // Setting onClickListener to sendMessageButton and forbidding user to send empty message
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString();
                if (!message.equals("")) {
                    sendMessage(fUser.getUid(), userID, message);
                    messageText.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "You cannot send empty message!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Firebase widgets initialization
        fStore = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference userRef = fStore.collection("users");
        DocumentReference userDocument = userRef.document(Objects.requireNonNull(userID));

        userDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                String fullName = Objects.requireNonNull(user).getFname() + " " + user.getLname();
                username.setText(fullName);
                if(user.getImgUrl().equals("default")) {
                    userImage.setImageResource(R.drawable.ic_account);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImgUrl()).into(userImage);
                }
            }
        });
    }

    /**
     * sendMessage method
     *
     * Converts sender and receiver ID to integer, multiplies those integers to create a chat room ID,
     *
     * @param sender FirebaseID of a message sender
     * @param receiver FirebaseID of a message receiver
     * @param message Content of message
     */
    public void sendMessage(String sender, String receiver, String message) {
        CollectionReference chatReference = FirebaseFirestore.getInstance().collection("chat_rooms");

        // Logging successful change of Firebase IDs to numbers
        Log.d(TAG, "sender to number: " + stringToNumber(sender));
        Log.d(TAG, "receiver to number: " + stringToNumber(receiver));

        // Creating chatRoomID
        String chatID = "chat_room" + stringToNumber(sender) * stringToNumber(receiver);
        DocumentReference privateChat = chatReference.document(chatID);

        // Instance of chatRoom class and variables
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setMessageReceiver(receiver);
        chatRoom.setMessageSender(sender);
        String[] chatters = {sender, receiver};
        List<String> filter = Arrays.asList(chatters);
        chatRoom.setFilter(filter);
        privateChat.set(chatRoom);

        // Instance of ChatMessage class and variables
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageSender(sender);
        chatMessage.setMessageReceiver(receiver);
        chatMessage.setMessageText(message);
        chatMessage.setMessageTime(new Date().getTime());

        CollectionReference privateChatMessage = privateChat.collection("messages");
        privateChatMessage.document().set(chatMessage);
    }

    /**
     * buildRecyclerView method
     */
    public void buildRecyclerView() {
        // Getting data from previous activity
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        // Creating chat_room ID
        String chatID = "chat_room" + stringToNumber(fUser.getUid()) * stringToNumber(Objects.requireNonNull(userID));
        CollectionReference chatReference = fStore.collection("chat_rooms").document(chatID).collection("messages");

        // Ordering specific user chat_rooms by messageTime
        Query query = chatReference.orderBy("messageTime", Query.Direction.ASCENDING);

        // Setting query to RecyclerOptions
        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();

        // Initialization of MessageAdapter
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

    /**
     * stringToNumber method
     *
     * Converts String to a number, based on ASCII
     * table number representation of each character
     * adds all the characters values and returns
     * a number
     *
     * @param old_word word to be converted
     *
     * @return an integer that is a sum of all characters in a String
     */
    public int stringToNumber(String old_word){
        // Array of all the characters in a word
        char[] word = old_word.toCharArray();
        // Integer representation initialization
        int numberString = 0;
        // loops through a list and match char with int and add to main numberString
        for(char letter: word) {
            int a = letter;
            numberString += a;
        }
        return numberString;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getCurrentUser().getUid();
        CollectionReference userRef = fStore.collection("users");
        userRef.document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                String group = user.getGroup();
                String mode = user.getMode();
                Intent intent = new Intent(getApplicationContext(), UserInbox.class);
                intent.putExtra("group", group);
                intent.putExtra("mode", mode);
                startActivity(intent);
            }
        });
        Intent intent = new Intent(getApplicationContext(), UserInbox.class);
        startActivity(intent);
        finish();
    }
}
