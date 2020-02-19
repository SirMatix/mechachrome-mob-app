package com.example.mechachromemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Forum extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        String[] foods = {"Topic1", "Topic2", "Topic3", "Topic4", "Topic5"};

        ListAdapter forumAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, foods);
        ListView forumListView =  findViewById(R.id.ForumListView);
        forumListView.setAdapter(forumAdapter);

        forumListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String topic = String.valueOf(parent.getItemAtPosition(position));
                        Toast.makeText(Forum.this, topic, Toast.LENGTH_LONG).show();
                    }
                }
        );





    }
}
