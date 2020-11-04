package com.example.chatterboi.afterauthenticated;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chatterboi.Preferences;
import com.example.chatterboi.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatInterface extends AppCompatActivity {

    String Gname, Gid, time_created;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    EditText message;
    ImageButton send_message;

    Preferences preferences;

    Menu toolbarmenu;

    RecyclerView recycler_interface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_interface);

        message = findViewById(R.id.message_interface);
        send_message = findViewById(R.id.send_message_interface);
        recycler_interface = findViewById(R.id.chats_interface);

        recycler_interface.setHasFixedSize(false);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setReverseLayout(true);
        recycler_interface.setLayoutManager(manager);


        preferences = new Preferences(getApplicationContext());

        Gname = getIntent().getStringExtra("GroupName");
        Gid = getIntent().getStringExtra("GroupID");
        time_created = getIntent().getStringExtra("Time");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            setTitle(Gname);
        }


        showChatMessages();

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!message.getText().toString().isEmpty()){

                    Map<String, Object> chat = new HashMap<>();
                    chat.put("message", message.getText().toString());
                    chat.put("time", System.currentTimeMillis());
                    chat.put("sender", mUser.getUid());
                    chat.put("groupId",Gid);
                    chat.put("username",preferences.getData("username"));
                    chat.put("name",preferences.getData("usernameAdded"));
                    firestore.collection("aGroups").document(Gid).collection("Chat").add(chat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(ChatInterface.this, "Message Sent", Toast.LENGTH_SHORT).show();
                            showChatMessages();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                message.setText(null);
            }
        });
    }

    private void showChatMessages() {
        firestore.collection("aGroups").document(Gid).collection("Chat")
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.d("Check", "listen failed: " + error.getMessage());
                }

                else{
                    Log.d("Check", "Snapshot worked");
                    List<ChatModel> list = new ArrayList<>();
                    for(QueryDocumentSnapshot query : value){
                    list.add(new ChatModel(
                              query.getString("groupId")
                            , query.getId()
                            , query.getString("message")
                            , query.getString("sender")
                            , query.getLong("time")
                            , query.getString("name")
                            , query.getString("username")
                            ));
                }
                recycler_interface.setAdapter(new RealChatRecyclerInterface(mUser.getUid(),list));
            }}
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.arpit_chat_menu, menu);
        toolbarmenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}