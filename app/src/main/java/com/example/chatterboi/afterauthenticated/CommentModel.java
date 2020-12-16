package com.example.chatterboi.afterauthenticated;

public class CommentModel {
    String comment, uid, username;
    Long time;

    public CommentModel(String comment, String uid, String username, Long time) {
        this.comment = comment;
        this.uid = uid;
        this.username = username;
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
