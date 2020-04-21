package com.example.mechachromemobileapp.Models;

/**
 * Child of ForumTopic Class
 */
public class ForumPost extends ForumTopic {

    private String content;

    public ForumPost() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
