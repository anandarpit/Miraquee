package com.example.chatterboi.afterauthenticated;

public class Requestmodel {
    String uid, SorR , username ,name;
    Boolean status;

    public Requestmodel(String uid, String sorR, Boolean status, String username, String name) {
        this.uid = uid;
        this.SorR = sorR;
        this.username = username;
        this.name = name;
        this.status = status;
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
        this.SorR = sorR;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
