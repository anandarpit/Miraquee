package com.example.chatterboi.RequestNotif;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiServe {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAABfYiQqg:APA91bE6LsoRn0sVEjjWYpMvGFIIT3VT5ydTe81Rf0YLtdJibit_5oXlDS9CD6iwL_QMJasDT-AFmpPDRPPcO5p33uyeCqKgYkEq7vPaAhR8e7lk2vdHINRsPNYpo7rNTcieDym1hupJ"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);


}
