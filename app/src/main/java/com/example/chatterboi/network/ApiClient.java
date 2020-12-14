package com.example.chatterboi.network;

import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;

    private static Retrofit getClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl("https://fcm.googleapis.com/fcm/")
                    .addConverterFactory(ScalarsConverterFactory.create()).build();
        }
        return retrofit;
    }
}
