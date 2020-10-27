package com.example.chatterboi.arpit;

public class ChatLists {
    String groupname;
    Long time;
    String id;

    public ChatLists(String groupname, String id, Long time) {
        this.groupname = groupname;
        this.time = time;
        this.id = id;
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
