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

/**
 * Application main activity
 *
 * Here is located menu to get to all the other activities within the app.
 * At first it checks if user is signed it, if true it shows menu if not
 * opens, login activity
 */
public class MainActivity extends AppCompatActivity {

    // global variables
    private final String TAG = "MainActivity: ";
    private Button timetables, library, moodle, floor_plan, forum, bookSaleBtn;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference userRef = fStore.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If user isn't logged in take him to login page
        if(fAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }

        initViews();
        setButtons();
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    public void initViews(){
        // layout variables initialization
        timetables = findViewById(R.id.timetables);
        library = findViewById(R.id.library);
        moodle = findViewById(R.id.moodle);
        floor_plan = findViewById(R.id.floor_plan);
        forum = findViewById(R.id.forumBtn);
        bookSaleBtn = findViewById(R.id.book_sale);
    }

    /**
     * onCreateOptionMenu() method
     *
     * specifies the options menu for an activity, this
     * is the menu available on top of the Activity
     *
     * @param menu
     * @return constructed menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * onOptionsItemSelected()
     *
     * handles selection of item in inflated menu layout.
     *
     * @param item object being clicked on according to its id
     * @return selected item function
     */
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

    /**
     * starts UserAccount activity
     */
    public void userAccount() {
        startActivity(new Intent(getApplicationContext(), UserAccount.class));
    }

    /**
     * userInbox()
     *
     * gets FirebaseFirestore user document, reads the data and passes
     * it to intent and starts activity with that intent
     */
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

    /**
     * Opens UserSettings activity
     */
    public void userSettings() {
        startActivity(new Intent(getApplicationContext(), UserSettings.class));
    }

    /**
     * Logs out user
     */
    public void logout() {
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    /**
     *  This method sets the onClickListener to buttons
     */
    public void setButtons() {
        // button that opens timetables
        timetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timetable();
            }
        });
        // button that opens library
        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Library.class));
            }
        });
        // button that opens moodle in external browser
        moodle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoodle();
            }
        });
        // button that opens floor plan
        floor_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FloorPlan.class));
            }
        });
        // button that opens forum
        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Forum.class));
            }
        });
        // button that opens book sale
        bookSaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BookSale.class));
            }
        });
    }

    /**
     * method to open moodle in external browser
     */
    public void openMoodle() {
        String url = "https://partnerships.moodle.roehampton.ac.uk/login/index.php";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    /**
     * timetable()
     *
     * reads user data from FirebaseFirestore users Collection,
     * saves data in variables and then passes it to Timetable activity
     * using intent.
     */
    public void timetable() {
        String userID = fAuth.getCurrentUser().getUid();
        userRef.document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Getting User class instance from documentSnapshot
                User user = documentSnapshot.toObject(User.class);
                // get user data
                String group = user.getGroup();
                String mode = user.getMode();
                // passing user data and starting new activity
                Intent intent = new Intent(getApplicationContext(), Timetables.class);
                intent.putExtra("group", group);
                intent.putExtra("mode", mode);
                startActivity(intent);
            }
        });
    }
}
