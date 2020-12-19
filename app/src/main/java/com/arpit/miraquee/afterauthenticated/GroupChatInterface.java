package com.arpit.miraquee.afterauthenticated;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.arpit.miraquee.SharedPreferences.Preferences;
import com.arpit.miraquee.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChatInterface extends AppCompatActivity {

    String Gname, Gid, time_created;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    EditText message;
    StorageReference storageReference;
    ImageView send_message,attach;
    String uid;

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
        attach = findViewById(R.id.attachFiles);

        recycler_interface.setHasFixedSize(false);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setReverseLayout(true);
        recycler_interface.setLayoutManager(manager);

        preferences = new Preferences(getApplicationContext());
        storageReference= FirebaseStorage.getInstance().getReference();

        Gname = getIntent().getStringExtra("GroupName");
        Gid = getIntent().getStringExtra("GroupID");
        time_created = getIntent().getStringExtra("Time");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        uid = mUser.getUid();
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_green);
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
                    chat.put("type","text");
                    chat.put("username",preferences.getData("username"));
                    chat.put("name",preferences.getData("usernameAdded"));
                    firestore.collection("aGroups").document(Gid).collection("Chat")
                            .add(chat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(GroupChatInterface.this, "Message Sent", Toast.LENGTH_SHORT).show();
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

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[]{
                        "Image",
                        "Pdf",
                        "Document"
                };
//                AlertDialog.Builder builder= new AlertDialog.Builder(GroupChatInterface.this);
//                builder.setTitle("Choose the type");
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if(i == 0){
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Select One Image"),1000);
//                        }
//                    }
//                });
//                builder.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data.getData() != null){
            Uri imageUri = data.getData();
            UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis() + ".jpg" )))
                    .withAspectRatio(200, 230)
                    .withMaxResultSize(200, 230)
                    .start(GroupChatInterface.this);
        }
        if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            Uri uri = UCrop.getOutput(data);
            final ProgressDialog dialog = new ProgressDialog(GroupChatInterface.this);
            dialog.setMessage("Posting...");
            dialog.show();
            final Long currentTime = System.currentTimeMillis();
            final String time = currentTime + "";
            final StorageReference fileref = storageReference.child("Image Messages")
                    .child(uid + time);
            StorageTask uploadTask = fileref.putFile(uri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        String myUrl = downloadUrl.toString();

                        Map<String, Object> chat = new HashMap<>();
                        chat.put("message", myUrl);
                        chat.put("time", currentTime);
                        chat.put("sender", mUser.getUid());
                        chat.put("groupId",Gid);
                        chat.put("type","image");
                        chat.put("username",preferences.getData("username"));
                        chat.put("name",preferences.getData("usernameAdded"));
                        firestore.collection("aGroups").document(Gid).collection("Chat")
                                .add(chat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Message Sent", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
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
                    List<GroupModel> list = new ArrayList<>();
                    list.clear();
                    for(QueryDocumentSnapshot query : value){
                    list.add(new GroupModel(
                              query.getString("groupId")
                            , query.getId()
                            , query.getString("message")
                            , query.getString("sender")
                            , query.getLong("time")
                            , query.getString("name")
                            , query.getString("username")
                            , query.getString("type")
                    ));
                }
                    for(GroupModel list1: list) {
                        Log.d("Sexy", "elements:" + list1.getMessage());
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