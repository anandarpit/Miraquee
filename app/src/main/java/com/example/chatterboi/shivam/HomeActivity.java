package com.example.chatterboi.shivam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.chatterboi.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private FloatingActionButton createRoom;
    sChatRoomRepository chatRoomRepository;
    private RecyclerView chatRooms;
    private ChatRoomsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sactivity_home);
           // authentication = new AuthenticationRepository(FirebaseFirestore.getInstance());
            createRoom = findViewById(R.id.create_room);
        chatRoomRepository = new sChatRoomRepository(FirebaseFirestore.getInstance());
            initUI();
        getChatRooms();
        //recsetsview.setLayoutManager(new GridLayoutManager(Setspage.this,1));


        // authenticate();
        }
        private void initUI() {
            chatRooms = findViewById(R.id.rooms);
            chatRooms.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
            createRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("MainActivity", "Launch create a room screen");
                    Intent intent = new Intent(HomeActivity.this, screateRoomActivity.class);
                    startActivity(intent);
                }
            });
        }
    private void getChatRooms() {
        chatRoomRepository.getRooms(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("homeActivity", "Listen failed.", e);
                    return;
                }

                List<ChatRoom> rooms = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    rooms.add(new ChatRoom(doc.getId(), doc.getString("name")));
                }

                adapter = new ChatRoomsAdapter(rooms,listener);
                chatRooms.setAdapter(adapter);
            }
        });
    }
    ChatRoomsAdapter.OnChatRoomClickListener listener = new ChatRoomsAdapter.OnChatRoomClickListener() {
        @Override
        public void onClick(ChatRoom chatRoom) {
            Intent intent = new Intent(HomeActivity.this, schatRoomActivity.class);
            intent.putExtra(schatRoomActivity.CHAT_ROOM_ID, chatRoom.getId());
            intent.putExtra(schatRoomActivity.CHAT_ROOM_NAME, chatRoom.getName());
            startActivity(intent);
        }
    };
    }