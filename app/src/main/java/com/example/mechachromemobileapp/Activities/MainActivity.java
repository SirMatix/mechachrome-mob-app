package com.example.mechachromemobileapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mechachromemobileapp.Activities.BookSale.BookSale;
import com.example.mechachromemobileapp.Activities.Forum.Forum;
import com.example.mechachromemobileapp.Activities.Library.Library;
import com.example.mechachromemobileapp.Activities.Timetables.Timetables;
import com.example.mechachromemobileapp.Activities.User.Login;
import com.example.mechachromemobileapp.Activities.User.Register;
import com.example.mechachromemobileapp.Activities.User.UserAccount;
import com.example.mechachromemobileapp.Activities.User.UserInbox;
import com.example.mechachromemobileapp.Activities.User.UserSettings;
import com.example.mechachromemobileapp.Models.User;
import com.example.mechachromemobileapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private Button timetables, library, moodle, floor_plan, moreBtn, bookSaleBtn;
    private String userID;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference userRef = fStore.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(fAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), Register.class));
            finish();
        }

        initViews();
        setButtons();
    }


    public void initViews(){
        timetables = findViewById(R.id.timetables);
        library = findViewById(R.id.library);
        moodle = findViewById(R.id.moodle);
        floor_plan = findViewById(R.id.floor_plan);
        moreBtn = findViewById(R.id.moreBtn);
        bookSaleBtn = findViewById(R.id.book_sale);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.user_account:
                userAccount();
                return true;
            case R.id.user_inbox:
                userInbox();
                return true;
            case R.id.user_settings:
                userSettings();
                return true;
            case R.id.user_logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void userAccount() {
        startActivity(new Intent(getApplicationContext(), UserAccount.class));
    }

    public void userInbox() {
        String userID = fAuth.getCurrentUser().getUid();
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
    }

    public void userSettings() {
        startActivity(new Intent(getApplicationContext(), UserSettings.class));
    }

    public void logout (){
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    public void setButtons() {
        timetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timetable();
            }
        });

        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Library.class));
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

        bookSaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BookSale.class));
            }
        });
    }

    public void openMoodle() {
        String url = "https://partnerships.moodle.roehampton.ac.uk/login/index.php";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }


    public void timetable() {
        String userID = fAuth.getCurrentUser().getUid();
        userRef.document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                String group = user.getGroup();
                String mode = user.getMode();
                Intent intent = new Intent(getApplicationContext(), Timetables.class);
                intent.putExtra("group", group);
                intent.putExtra("mode", mode);
                startActivity(intent);
            }
        });
    }
}
