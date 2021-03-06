package com.arpit.miraquee.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arpit.miraquee.SharedPreferences.Preferences;
import com.arpit.miraquee.afterauthenticated.HomePageArpit;
import com.arpit.miraquee.Auth.Log_in;
import com.arpit.miraquee.Auth.Register;
import com.arpit.miraquee.Auth.username_page;

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
            startActivity(new Intent(SplashActivity.this, Register.class));
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
            startActivity(new Intent(SplashActivity.this, HomePageArpit.class));
            finish();
            Log.d("Check", "The user is already logged in");
        }
    }
}
