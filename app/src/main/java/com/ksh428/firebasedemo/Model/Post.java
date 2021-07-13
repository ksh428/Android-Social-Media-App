package com.ksh428.firebasedemo.Model;

public class Post {
    private String description;
    private  String imageurl;
    private  String postId;
    private  String publisher;

    public Post(String description, String imageurl, String postId, String publisher) {
        this.description = description;
        this.imageurl = imageurl;
        this.postId = postId;
        this.publisher = publisher;
    }

    public Post() {
    }

    public String getDescription() {
        return description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getPostId() {
        return postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
