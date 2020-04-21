package com.example.mechachromemobileapp.Models;

/**
 *  Child class of ChatRooms class
 *
 *  holds information about each chat message
 */
public class ChatMessage extends ChatRoom{
    private String messageText;
    private long messageTime;

    public ChatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
