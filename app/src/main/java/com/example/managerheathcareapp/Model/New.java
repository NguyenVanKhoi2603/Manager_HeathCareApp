package com.example.managerheathcareapp.Model;

public class New {
    String id_new, title_new, content_new, url_new, img_new;

    public New(String id_new, String title_new, String content_new, String url_new, String img_new) {
        this.id_new = id_new;
        this.title_new = title_new;
        this.content_new = content_new;
        this.url_new = url_new;
        this.img_new = img_new;
    }

    public New() {

    }

    public String getId_new() {
        return id_new;
    }

    public void setId_new(String id_new) {
        this.id_new = id_new;
    }

    public String getTitle_new() {
        return title_new;
    }

    public void setTitle_new(String title_new) {
        this.title_new = title_new;
    }

    public String getContent_new() {
        return content_new;
    }

    public void setContent_new(String content_new) {
        this.content_new = content_new;
    }

    public String getUrl_new() {
        return url_new;
    }

    public void setUrl_new(String url_new) {
        this.url_new = url_new;
    }

    public String getImg_new() {
        return img_new;
    }

    public void setImg_new(String img_new) {
        this.img_new = img_new;
    }
}
