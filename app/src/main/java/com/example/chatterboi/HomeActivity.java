package com.example.chatterboi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    private FloatingActionButton createRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


           // authentication = new AuthenticationRepository(FirebaseFirestore.getInstance());

            createRoom = findViewById(R.id.create_room);

            initUI();

           // authenticate();
        }
        private void initUI() {
            createRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainActivity", "Launch create a room screen");
                }
            });
        }
    }