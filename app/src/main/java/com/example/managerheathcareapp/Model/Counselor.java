package com.example.managerheathcareapp.Model;

public class Counselor {
    String id_counselor, user_id, category, introduce, position_counselor;
    int total_ratting;

    public Counselor() {

    }

    public Counselor(String id_counselor, String user_id, String category, String introduce, String position_counselor, int total_ratting) {
        this.id_counselor = id_counselor;
        this.user_id = user_id;
        this.category = category;
        this.introduce = introduce;
        this.position_counselor = position_counselor;
        this.total_ratting = total_ratting;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId_counselor() {
        return id_counselor;
    }

    public void setId_counselor(String id_counselor) {
        this.id_counselor = id_counselor;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getPosition_counselor() {
        return position_counselor;
    }

    public void setPosition_counselor(String position_counselor) {
        this.position_counselor = position_counselor;
    }

    public int getTotal_ratting() {
        return total_ratting;
    }

    public void setTotal_ratting(int total_ratting) {
        this.total_ratting = total_ratting;
    }
}
