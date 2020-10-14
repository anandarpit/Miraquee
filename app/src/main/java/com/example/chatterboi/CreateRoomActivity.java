package com.example.chatterboi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class CreateRoomActivity extends AppCompatActivity {
    private EditText roomName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        roomName = findViewById(R.id.room_name);

    }
}