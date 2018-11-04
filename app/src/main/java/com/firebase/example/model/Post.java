package com.firebase.example.model;

import java.io.Serializable;


public class Post implements Serializable {

    private String title;
    private String description;
    private String image_url;
    private String  uid;

    public Post() {
    }

    public Post(String title, String description, String image_url, String uid) {
        this.title = title;
        this.description = description;
        this.image_url = image_url;
        this.uid = uid;
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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
