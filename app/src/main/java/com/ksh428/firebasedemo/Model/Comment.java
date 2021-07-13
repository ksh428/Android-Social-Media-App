package com.ksh428.firebasedemo.Model;

public class Comment {
    private String comment;
    private String publisher;
    private String id;

    public Comment(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
