package com.example.chatterboi.afterauthenticated;

public class Requestmodel {
    String uid, SorR;
    Boolean status;

    public Requestmodel(String uid, String sorR, Boolean status) {
        this.uid = uid;
        SorR = sorR;
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
        SorR = sorR;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
