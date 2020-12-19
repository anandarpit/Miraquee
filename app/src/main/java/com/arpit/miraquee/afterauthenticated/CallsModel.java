package com.arpit.miraquee.afterauthenticated;

public class CallsModel {
    String uid, SorR, username, name;
    Long time;

    public CallsModel(String uid, String sorR, String username, String name, Long time) {
        this.uid = uid;
        this.SorR = sorR;
        this.username = username;
        this.name = name;
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSorR() {
        return SorR;
    }

    public void setSorR(String sorR) {
        SorR = sorR;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
