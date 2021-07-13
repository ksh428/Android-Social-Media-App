package com.ksh428.firebasedemo.Model;

public class User {
    private  String name;
    private  String username;
    private  String bio;
    private  String email;
    private  String imageurl;
    private  String id;

    public User(String name, String username, String bio, String email, String imageurl, String id) {
        this.name = name;
        this.username = username;
        this.bio = bio;
        this.email = email;
        this.imageurl = imageurl;
        this.id = id;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getEmail() {
        return email;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setId(String id) {
        this.id = id;
    }
}
