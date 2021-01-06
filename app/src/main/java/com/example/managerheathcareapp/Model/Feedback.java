package com.example.managerheathcareapp.Model;

public class Feedback {
    String id_feedback, user_id, counselor_id, comment, timestamp;
    Float ratting;

    public Feedback(String id_feedback, String user_id, String counselor_id, String comment, String timestamp, Float ratting) {
        this.id_feedback = id_feedback;
        this.user_id = user_id;
        this.counselor_id = counselor_id;
        this.comment = comment;
        this.timestamp = timestamp;
        this.ratting = ratting;
    }

    public Feedback() {

    }

    public String getId_feedback() {
        return id_feedback;
    }

    public void setId_feedback(String id_feedback) {
        this.id_feedback = id_feedback;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCounselor_id() {
        return counselor_id;
    }

    public void setCounselor_id(String counselor_id) {
        this.counselor_id = counselor_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Float getRatting() {
        return ratting;
    }

    public void setRatting(Float ratting) {
        this.ratting = ratting;
    }
}
