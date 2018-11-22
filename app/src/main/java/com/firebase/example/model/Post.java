package com.firebase.example.model;

import android.support.annotation.NonNull;

import java.io.Serializable;


public class Post implements Comparable<Post>, Serializable {

    private String ID;
    private String title;
    private String description;
    private String image_url;
    private String datetime;
    private String UID;
    private User user;

    public Post() {
    }

    public Post(String ID, String title, String description, String image_url, String datetime, String UID, User user) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.image_url = image_url;
        this.datetime = datetime;
        this.UID = UID;
        this.user = user;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public int compareTo(Post post) {
        return post.getDatetime().compareTo(getDatetime());
    }
}
