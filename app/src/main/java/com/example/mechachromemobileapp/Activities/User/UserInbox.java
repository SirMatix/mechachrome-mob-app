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

public class UserInbox extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private CollectionReference inboxRef;
    private String userID, groupFeed, modeFeed;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_inbox);

        initViews();
        createFragments();

    }

    public void initViews() {
        // initialize Firebase
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        inboxRef = fStore.collection("chat_rooms");

        userID = fAuth.getCurrentUser().getUid();

        // initialize recycler View
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

    }

    public void createFragments(){
        Intent intent = getIntent();
        groupFeed = intent.getStringExtra("group");
        modeFeed = intent.getStringExtra("mode");

        Bundle bundle = new Bundle();
        bundle.putString("group", groupFeed);
        bundle.putString("mode", modeFeed);
        Fragment usersFragment = new UsersFragment();
        Fragment chatsFragment = new ChatsFragment();
        usersFragment.setArguments(bundle);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(chatsFragment, "Messages");
        viewPagerAdapter.addFragment(usersFragment, "My group");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

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

