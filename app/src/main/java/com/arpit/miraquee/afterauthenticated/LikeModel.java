package com.arpit.miraquee.afterauthenticated;

public class LikeModel {
    String likerId;

    public LikeModel(String likerId) {
        this.likerId = likerId;
    }

    public String getLikerId() {
        return likerId;
    }

    public void setLikerId(String likerId) {
        this.likerId = likerId;
    }
}
