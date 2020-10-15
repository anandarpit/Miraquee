package com.example.chatterboi.shivam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatterboi.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class screateRoomActivity extends AppCompatActivity {
    private EditText roomName;
    private sChatRoomRepository chatRoomRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screate_room);
        chatRoomRepository = new sChatRoomRepository(FirebaseFirestore.getInstance());
        roomName = findViewById(R.id.room_name);

        setTitle(getString(R.string.create_room));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_24);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_room_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.create_room:
                // TODO: Check if roomName is empty show error
                if (roomName.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.error_empty_room), Toast.LENGTH_SHORT).show();
                } else {
                    // TODO: If is not empty, save room name to Firestore
                    createRoom();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void createRoom() {
        chatRoomRepository.createRoom(
                roomName.getText().toString(),
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(screateRoomActivity.this, schatRoomActivity.class);
                        intent.putExtra(schatRoomActivity.CHAT_ROOM_ID, documentReference.getId());
                        intent.putExtra(schatRoomActivity.CHAT_ROOM_NAME, roomName.getText().toString());
                        startActivity(intent);
                        finish();
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(screateRoomActivity.this, getString(R.string.error_empty_room), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}