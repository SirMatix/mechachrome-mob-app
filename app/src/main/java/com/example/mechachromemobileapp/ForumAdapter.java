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

class ForumAdapter extends ArrayAdapter<Map> {
    public ForumAdapter(@NonNull Context context, ArrayList<Map> forumTopics) {
        super(context, R.layout.custom_forum_list, forumTopics);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater forumInflater = LayoutInflater.from(getContext());
        View forumView = forumInflater.inflate(R.layout.custom_forum_list, parent, false);

        // getting the map at appropriate position in ArrayList
        Map topicData = getItem(position);

        // getting data out of Hash Map and assigning it to values
        String forumTopic = (String) topicData.get("topic_name");
        String author = (String) topicData.get("author");
        String date_published = (String) topicData.get("date_published");
        String post_num = (String) topicData.get("post_num");

        // getting data from the layout
        TextView topicText = forumView.findViewById(R.id.forumTopicView);
        TextView topicAuthor = forumView.findViewById(R.id.topicUserText);
        TextView topicPublished = forumView.findViewById(R.id.topicPublishedText);
        TextView numReplies = forumView.findViewById(R.id.numReplies);

        // setting layout data to appropriate values
        topicText.setText(forumTopic);
        topicAuthor.setText(author);
        topicPublished.setText(date_published);
        numReplies.setText(post_num);

        return forumView;
    }
}
