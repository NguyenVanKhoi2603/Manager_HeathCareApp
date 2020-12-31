package com.example.managerheathcareapp.Model;

public class Message {
    String sender, receiver,message, image, timestamp;
    Boolean isSeen;

    public Message() {

    }

    public Message(String sender, String receiver, String message, String image, String timestamp, Boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.image = image;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getSeen() {
        return isSeen;
    }

    public void setSeen(Boolean seen) {
        isSeen = seen;
    }

}
