package com.example.chatterboi.listeners;

import com.example.chatterboi.model.ContactModel;

import java.io.Serializable;

public interface ContactListeners {

    void initiateVideoMeeting(ContactModel contactModel);

    void initiateAudioMeeting(ContactModel contactModel);
}
