package com.example.chatterboi.afterauthenticated;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Spinner;

import com.example.chatterboi.R;

public class Requests extends AppCompatActivity {

    Spinner spinner;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_and_sents);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.statusbacground));
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }

}