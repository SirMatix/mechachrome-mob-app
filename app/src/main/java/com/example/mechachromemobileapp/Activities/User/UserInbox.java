package com.example.mechachromemobileapp.Activities.User;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.example.mechachromemobileapp.Fragments.ChatsFragment;
import com.example.mechachromemobileapp.Fragments.UsersFragment;
import com.example.mechachromemobileapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

/**
 * UserInbox Activity
 */
public class UserInbox extends AppCompatActivity {

    // Global variables
    private String userID;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_inbox);
        initViews();
        createFragments();

    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    public void initViews() {
        // Initialization of Firebase widgets
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        CollectionReference inboxRef = fStore.collection("chat_rooms");

        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        // RecyclerView initialization
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }

    /**
     * createFragments method
     *
     * Creates Fragments which divide page.
     */
    public void createFragments(){
        // Getting data from previous activity
        Intent intent = getIntent();
        String groupFeed = intent.getStringExtra("group");
        String modeFeed = intent.getStringExtra("mode");

        // Creating Bundle to pass data between fragments
        Bundle bundle = new Bundle();
        bundle.putString("group", groupFeed);
        bundle.putString("mode", modeFeed);
        Fragment usersFragment = new UsersFragment();
        Fragment chatsFragment = new ChatsFragment();
        usersFragment.setArguments(bundle);

        // ViewPages adapter to display Fragments
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(chatsFragment, "My Inbox");
        viewPagerAdapter.addFragment(usersFragment, "My Course Group");

        // Setting the adapter
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     *  ViewPagerAdapter class
     *
     *  Handles displaying of Fragments
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}

