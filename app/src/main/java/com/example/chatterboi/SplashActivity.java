package com.example.chatterboi;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    Preferences preferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(getApplicationContext());
        String verified = preferences.getData("isVerified?");
        if(verified.isEmpty()){
            startActivity(new Intent(SplashActivity.this,Register.class));
            finish();
        }
        else{
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            finish();
        }

    }
}
