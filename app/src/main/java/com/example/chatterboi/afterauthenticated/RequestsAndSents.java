package com.example.chatterboi.afterauthenticated;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;

import com.example.chatterboi.R;

public class RequestsAndSents extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_and_sents);

        if (Build.VERSION.SDK_INT >= 21) { //To change the color of the status bar
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        }
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}