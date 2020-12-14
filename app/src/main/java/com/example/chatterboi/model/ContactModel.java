package com.example.chatterboi.model;

import java.io.Serializable;

public class ContactModel implements Serializable {
    String name, username, uid;

    public ContactModel(String name, String username, String uid) {
        this.name = name;
        this.uid = uid;
        this.username= username;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
