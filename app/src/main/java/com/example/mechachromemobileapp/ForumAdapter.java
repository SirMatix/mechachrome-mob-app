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

class ForumAdapter extends ArrayAdapter<String> {

    public ForumAdapter(@NonNull Context context, ArrayList<String> forumTopics) {
        super(context, R.layout.custom_forum_list, forumTopics);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater forumInflater = LayoutInflater.from(getContext());
        View forumView = forumInflater.inflate(R.layout.custom_forum_list, parent, false);

        String forumTopic =  getItem(position);
        TextView topicText = forumView.findViewById(R.id.forumTopicView);

        topicText.setText(forumTopic);

        return forumView;
    }
}
