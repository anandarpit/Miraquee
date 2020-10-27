package com.example.chatterboi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.chatterboi.arpit.HomePageArpit;

public class SplashActivity extends AppCompatActivity {
    Preferences preferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(getApplicationContext());
        String Registered = preferences.getData("Registered");
        String LoggedIn = preferences.getData("LoggedIn");
        String UsernameAdded = preferences.getData("usernameAdded");


        if(Registered.isEmpty() && LoggedIn.isEmpty()){
            startActivity(new Intent(SplashActivity.this,Register.class));
            finish();
            Log.d("Check", "No Records");
        }
        else if(!Registered.isEmpty() && LoggedIn.isEmpty() && UsernameAdded.isEmpty() ){
            startActivity(new Intent(SplashActivity.this, username_page.class));
            finish();
            Log.d("Check","The user has completed registration but not entered the Name, so name page");
        }
        else if(!Registered.isEmpty() && LoggedIn.isEmpty() && !UsernameAdded.isEmpty()){
            startActivity(new Intent(SplashActivity.this, Log_in.class));
            finish();
            Log.d("Check", "The user has completed the whole process but needs to sign in");
        }
        else if(!LoggedIn.isEmpty()){
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
            Log.d("Check", "The user is already logged in");
        }
    }
}
