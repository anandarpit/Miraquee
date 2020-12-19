package com.arpit.miraquee.listeners;

import com.arpit.miraquee.model.ContactModel;

public interface ContactListeners {

    void initiateVideoMeeting(ContactModel contactModel);

    void initiateAudioMeeting(ContactModel contactModel);
}
