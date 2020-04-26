package com.example.mechachromemobileapp.Activities.Timetables;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Activities.FloorPlan;
import com.example.mechachromemobileapp.Adapters.DayAdapter;
import com.example.mechachromemobileapp.Models.Day;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Timetables extends AppCompatActivity {

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference dayRef = fStore.collection("timetables");
    private DayAdapter adapter;
    private String groupFeed, modeFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetables);
        getIntentData();
        buildRecyclerView();
    }

    /**
     * Method to get data from intent
     */
    private void getIntentData() {
        // getting intent from Forum activity and getting extra string
        Intent intent = getIntent();
        groupFeed = intent.getStringExtra("group");
        modeFeed = intent.getStringExtra("mode");
    }

    /**
     * Method that builds RecyclerView
     */
    private void buildRecyclerView() {
        // Query to get specific group and mode days to display in timetables sorted by order field
        Query query = dayRef.whereEqualTo("group", groupFeed).whereEqualTo("mode", modeFeed).orderBy("order", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Day> options = new FirestoreRecyclerOptions.Builder<Day>()
                .setQuery(query, Day.class)
                .build();

        adapter = new DayAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        /*
            setOnItemClickListener that listens to click on specific RecyclerView objects, we
            pass a timeframe String thought that interface and by that we can read data that correlate with Firestore
            If a user clicks on a field with a class assigned to it, user will be transferred to a
            floor plan activity on a floor where the class is, if user clicks on a field where lunch
            is message will be prompted to a user, if a user clicks on an empty field, message
            will be prompted to a user that he has no classes at that time.
         */
        adapter.setOnItemClickListener(new DayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, String timeframe) {
                String timetableString = documentSnapshot.getString(timeframe);
                if (!TextUtils.equals(timetableString,"")){
                    if(TextUtils.equals(timetableString, "Lunch")) {
                        Toast.makeText(Timetables.this, "It's lunch time!", Toast.LENGTH_SHORT).show();
                    } else {
                        assert timetableString != null;
                        // Getting indexes of room: and teacher: to localize the room number
                        int indexOfRoom= timetableString.indexOf("room:");
                        int indexOfTeacher = timetableString.indexOf("teacher:");
                        String roomNumber = timetableString.substring(indexOfRoom + 6, indexOfTeacher - 1);
                        // Starting a new intent and passing a room number to FloorPlan activity
                        Intent intent = new Intent(getApplicationContext(), FloorPlan.class);
                        intent.putExtra("roomNumber", roomNumber);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(Timetables.this, "No classes here!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}