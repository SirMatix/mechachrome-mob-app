package com.example.mechachromemobileapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mechachromemobileapp.R;

import java.util.ArrayList;
import java.util.Map;

public class PostAdapter extends ArrayAdapter<Map> {
    public PostAdapter(@NonNull Context context, ArrayList<Map> forumPosts) {
        super(context, R.layout.custom_post_list, forumPosts);
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
        TextView postContent = forumView.findViewById(R.id.content);
        TextView postAuthor = forumView.findViewById(R.id.author);
        TextView postPublished = forumView.findViewById(R.id.date_published);

        // setting layout data to appropriate values
        postContent.setText(post_content);
        postAuthor.setText("author: " + author);
        postPublished.setText("date published: " + date_published);

        return forumView;
    }
}