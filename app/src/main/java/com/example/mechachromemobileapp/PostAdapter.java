package com.example.mechachromemobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Map;

class PostAdapter extends ArrayAdapter<Map> {
    public PostAdapter(@NonNull Context context, ArrayList<Map> forumPosts) {
        super(context, R.layout.custom_forum_list, forumPosts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater forumInflater = LayoutInflater.from(getContext());
        View forumView = forumInflater.inflate(R.layout.custom_post_list, parent, false);

        // getting the map at appropriate position in ArrayList
        Map postData = getItem(position);

        // getting data out of Hash Map and assigning it to values
        String post_content = (String) postData.get("content");
        String author = (String) postData.get("author");
        String date_published = (String) postData.get("date_published");

        // getting data from the layout
        TextView topicText = forumView.findViewById(R.id.forumTopicView);
        TextView topicAuthor = forumView.findViewById(R.id.topicUserText);
        TextView topicPublished = forumView.findViewById(R.id.topicPublishedText);

        // setting layout data to appropriate values
        topicText.setText(post_content);
        topicAuthor.setText(author);
        topicPublished.setText(date_published);

        return forumView;
    }
}