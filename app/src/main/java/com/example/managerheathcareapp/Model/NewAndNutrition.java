package com.example.managerheathcareapp.Model;

public class NewAndNutrition {
    String id_post, title, description, url, image_id, category, timestamp;

    public NewAndNutrition(String id_post, String title, String description, String url, String image_id, String category, String timestamp) {
        this.id_post = id_post;
        this.title = title;
        this.description = description;
        this.url = url;
        this.image_id = image_id;
        this.category = category;
        this.timestamp = timestamp;
    }

    public NewAndNutrition() {

    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
