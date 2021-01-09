package com.example.managerheathcareapp.Model;

public class ChatList {
    String id, timestamp;

    public ChatList(String id, String timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public ChatList() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
