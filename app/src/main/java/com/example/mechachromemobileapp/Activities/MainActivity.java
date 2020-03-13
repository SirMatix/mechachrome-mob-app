package com.example.mechachromemobileapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mechachromemobileapp.Activities.Forum.Forum;
import com.example.mechachromemobileapp.Activities.Library.LibraryAdmin;
import com.example.mechachromemobileapp.Activities.Library.LibraryUser;
import com.example.mechachromemobileapp.Activities.Timetables.Timetables;
import com.example.mechachromemobileapp.Activities.User.Login;
import com.example.mechachromemobileapp.Activities.User.Register;
import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "TAG";
    Button timetables, library, moodle, floor_plan, moreBtn;
    String userID;
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
                startActivity(new Intent(getApplicationContext(), Timetables.class));
            }
        });

        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserPermission();
            }
        });

        moodle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoodle();
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
                startActivity(new Intent(getApplicationContext(), Forum.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void logout (View view){
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    public void getUserPermission() {
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference userRef = fStore.collection("users").document(userID);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot user = task.getResult();
                if (task.isSuccessful()) {
                    if (user.exists()) {
                        Log.d(TAG, "Got the user " + userID);
                        String permission = user.get("permission").toString().toLowerCase();
                        if (TextUtils.equals("admin",permission)) {
                            startActivity(new Intent(getApplicationContext(), LibraryAdmin.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), LibraryUser.class));
                        }
                    } else {
                        Log.d(TAG, "No such user");
                    }
                }
            }
        });
    }

    public void openMoodle() {
        url = "https://partnerships.moodle.roehampton.ac.uk/login/index.php";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
