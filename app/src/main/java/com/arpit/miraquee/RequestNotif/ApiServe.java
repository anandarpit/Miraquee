package com.arpit.miraquee.RequestNotif;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiServe {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA-g9wmew:APA91bE-5-rtdtXi34JbH56A4gRHhFo8nZLARWNR6MFC0TNaOcm4-sINqde12JPCHLufLzszQVKOpzHBI3tGwjPpH6qZEop78skIKXE2SoKr8nJDDGHfk6OAQaA8OSYw7KUUa-iJqbZy"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);


}
