package com.example.chatterboi.afterauthenticated;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class GroupChatList {
    String groupname;
    Long time;
    String id;
    String username;

    public GroupChatList(String groupname, String id, Long time, String uid) {
        this.groupname = groupname;
        this.time = time;
        this.id = id;
        this.username = uid;
    }

    public String getUsername() {
        return username;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
