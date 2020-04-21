package com.example.mechachromemobileapp.Models;

import java.util.List;

/**
 *  ChatRoom class
 *
 *  holds information about chatRooms that are created between
 *  users when they communicate, List filter is used to help
 *  locate members specific of chat room
 *
 */
public class ChatRoom {
    private String messageSender, messageReceiver;
    private List<String> filter;

    public ChatRoom() {
    }

    public String getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender = messageSender;
    }

    public String getMessageReceiver() {
        return messageReceiver;
    }

    public void setMessageReceiver(String messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(List<String> filter) {
        this.filter = filter;
    }
}
