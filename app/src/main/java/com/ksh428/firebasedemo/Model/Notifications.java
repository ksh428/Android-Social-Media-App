package com.ksh428.firebasedemo.Model;

public class Notifications {
    private String userid;
    private  String text;
    private  String postid;
    boolean ispost;

    public Notifications() {
    }

    public Notifications(String userid, String text, String postid, boolean ispost) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.ispost = ispost;
    }

    public String getUserid() {
        return userid;
    }

    public String getText() {
        return text;
    }

    public String getPostid() {
        return postid;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }
}
