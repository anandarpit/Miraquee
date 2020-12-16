package com.example.chatterboi.Constants;

import java.util.HashMap;

public class Constants {
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static  final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";  // not Content_Type LOL
    public static  final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static  final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static  final  String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";

    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCALLED = "cancelled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";


    public static HashMap<String, String> getremotemessageHeaders(){
        HashMap<String, String> headers = new HashMap<>();
        headers.put(Constants.REMOTE_MSG_AUTHORIZATION,
                "key=AAAABfYiQqg:APA91bE6LsoRn0sVEjjWYpMvGFIIT3VT5ydTe81Rf0YLtdJibit_5oXlDS9CD6iwL_QMJasDT-AFmpPDRPPcO5p33uyeCqKgYkEq7vPaAhR8e7lk2vdHINRsPNYpo7rNTcieDym1hupJ"
        );
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }


}
