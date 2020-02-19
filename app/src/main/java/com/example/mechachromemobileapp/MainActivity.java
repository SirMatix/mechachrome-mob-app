package com.example.mechachromemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    Button timetables, library, moodle, floor_plan, moreBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), Register.class));
            finish();
        }

        timetables = findViewById(R.id.timetables);
        library = findViewById(R.id.library);
        moodle = findViewById(R.id.moodle);
        floor_plan = findViewById(R.id.floor_plan);
        moreBtn = findViewById(R.id.moreBtn);

        timetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Timetables.class));
            }
        });
        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Library.class));
            }
        });

        moodle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "https://partnerships.moodle.roehampton.ac.uk/login/index.php";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        floor_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FloorPlan.class));
            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Forum.class));
            }
        });

    }
    public void logout (View view){
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}
