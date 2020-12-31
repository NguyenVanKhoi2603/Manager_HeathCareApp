package com.example.managerheathcareapp.Model;

public class User {

    String createAt, age, gender, user_id, first_name, last_name, image_id, email, phone, location;

    public User() {

    }

    public User(String createAt, String age, String gender, String user_id, String first_name, String last_name, String image_id, String email, String phone, String location) {
        this.createAt = createAt;
        this.age = age;
        this.gender = gender;
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.image_id = image_id;
        this.email = email;
        this.phone = phone;
        this.location = location;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
