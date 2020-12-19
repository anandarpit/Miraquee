package com.arpit.miraquee.afterauthenticated;

public class ChatModel {
    String message, mUid, oUid, type, SorR, oUsername, oName;
    Long time;

    public ChatModel(String message, String mUid, String oUid, String type, String sorR, String oUsername, String oName, Long time) {
        this.message = message;
        this.mUid = mUid;
        this.oUid = oUid;
        this.type = type;
        this.SorR = sorR;
        this.oUsername = oUsername;
        this.oName = oName;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getoUid() {
        return oUid;
    }

    public void setoUid(String oUid) {
        this.oUid = oUid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSorR() {
        return SorR;
    }

    public void setSorR(String sorR) {
        SorR = sorR;
    }

    public String getoUsername() {
        return oUsername;
    }

    public void setoUsername(String oUsername) {
        this.oUsername = oUsername;
    }

    public String getoName() {
        return oName;
    }

    public void setoName(String oName) {
        this.oName = oName;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
