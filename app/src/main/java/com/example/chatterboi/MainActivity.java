package com.example.chatterboi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button arpit , shivam;

    String userId, Name, Email, Pass;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arpit = findViewById(R.id.arpit);
        shivam = findViewById(R.id.shivam);
        mAuth = FirebaseAuth.getInstance();

        Name = getIntent().getStringExtra("Name");
        Email = getIntent().getStringExtra("Email");
        Pass = getIntent().getStringExtra("Pass");

        arpit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userId= mAuth.getCurrentUser().getUid();
                db = FirebaseFirestore.getInstance();
                DocumentReference documentReference=db.collection("Arpit").document(userId);
                Map<String,Object> user=new HashMap<>();
                user.put("Full_Name",Name);
                user.put("Email_Id",Email);
                user.put("Pass",Pass);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.v("Tag","On Success: User Profile Created for"+userId);
                    }
                });
                Intent intent = new Intent(MainActivity.this, HomeActivityArpit.class);
                startActivity(intent);
            }
        });



        shivam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userId= mAuth.getCurrentUser().getUid();
                db = FirebaseFirestore.getInstance();
                DocumentReference documentReference=db.collection("Shivam").document(userId);
                Map<String,Object> user=new HashMap<>();
                user.put("Full_Name",Name);
                user.put("Email_Id",Email);
                user.put("Pass",Pass);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.v("Tag","On Success: User Profile Created for"+userId);
                    }
                });
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}