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
    private CollectionReference dayRef = fStore.collection("Timetables");

    private DayAdapter adapter;
    String groupFeed, modeFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetables);

        // getting intent from Forum activity and getting extra string
        Intent intent = getIntent();
        groupFeed = intent.getStringExtra("group");
        modeFeed = intent.getStringExtra("mode");


        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = dayRef.whereEqualTo("group", groupFeed).whereEqualTo("mode", modeFeed).orderBy("order", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Day> options = new FirestoreRecyclerOptions.Builder<Day>()
                .setQuery(query, Day.class)
                .build();

        adapter = new DayAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new DayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, String timeframe) {
                String timetableString = documentSnapshot.getString(timeframe);
                if (!TextUtils.equals(timetableString,"")){
                    if(TextUtils.equals(timetableString, "Lunch")) {
                        Toast.makeText(Timetables.this, "It's lunch time!", Toast.LENGTH_SHORT).show();
                    } else {
                        int indexOfRoom= timetableString.indexOf("room:");
                        int indexOfTeacher = timetableString.indexOf("teacher:");
                        String roomNumber = timetableString.substring(indexOfRoom + 6, indexOfTeacher - 1);
                        Intent intent = new Intent(getApplicationContext(), FloorPlan.class);
                        intent.putExtra("roomNumber", roomNumber);
                        Toast.makeText(Timetables.this, "Room number is: " + roomNumber, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Timetables.this, "No lesson here!", Toast.LENGTH_SHORT).show();
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


/*


LinearLayoutManager layoutManager
    = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

RecyclerView myList = (RecyclerView) findViewById(R.id.my_recycler_view);
myList.setLayoutManager(layoutManager);






 */
