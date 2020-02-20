package com.example.mechachromemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class ForumPostReply extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText editTopic, editContent;
    Button addTopicBtn, discardTopicBtn;
    FirebaseFirestore fStore;
    SimpleDateFormat date;
    Long date_published;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_post_topic);

        editTopic = findViewById(R.id.editTopic);
        editContent = findViewById(R.id.editContent);
        fStore = FirebaseFirestore.getInstance();
        date_published = System.currentTimeMillis()/1000;
        //String ts = date_published.toString();


        addTopicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = editTopic.getText().toString().trim();
                String content = editContent.getText().toString().trim();

                DocumentReference postRef = fStore.collection("forum_topics").document();
                Map<String, Object> addTopic = new HashMap<>();
                addTopic.put("topic",topic);
                addTopic.put("content",content);
                addTopic.put("date published", date_published);

                postRef.set(addTopic).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Ids document created");
                    }
                });

            }
        });

    }
}
