package com.example.chatterboi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.chatterboi.arpit.HomePageArpit;
import com.example.chatterboi.shivam.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    Button arpit , shivam;

    String userId, Name, Email, Pass;

    ImageView logout;

    popupLogout popupLogout;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arpit = findViewById(R.id.arpit);
        shivam = findViewById(R.id.shivam);
        logout = findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();
        popupLogout = new popupLogout(getApplicationContext());

        Name = getIntent().getStringExtra("Name");
        Email = getIntent().getStringExtra("Email");
        Pass = getIntent().getStringExtra("Pass");
        Intent intent = new Intent(MainActivity.this, HomePageArpit.class);
        startActivity(intent);
        finish();

        arpit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomePageArpit.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupLogout.showPopupDialog();
            }
        });

        shivam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}