package com.arpit.miraquee.Constants;

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
                "key=AAAA-g9wmew:APA91bE-5-rtdtXi34JbH56A4gRHhFo8nZLARWNR6MFC0TNaOcm4-sINqde12JPCHLufLzszQVKOpzHBI3tGwjPpH6qZEop78skIKXE2SoKr8nJDDGHfk6OAQaA8OSYw7KUUa-iJqbZy"
        );
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }


}
