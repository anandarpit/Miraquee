package com.example.chatterboi.afterauthenticated;

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

import com.example.chatterboi.R;
import com.example.chatterboi.RequestNotif.ApiServe;
import com.example.chatterboi.RequestNotif.Client;
import com.example.chatterboi.RequestNotif.Data;
import com.example.chatterboi.RequestNotif.MyResponse;
import com.example.chatterboi.RequestNotif.NotificationSender;
import com.example.chatterboi.SharedPreferences.Preferences;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatInterface extends AppCompatActivity {

    String OName, Oid, OUsername;
    ApiServe apiServe;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String type;
    String message;
    EditText messageEditText;
    StorageReference storageReference;
    ImageView send_message,attach;
    String uid;
    Long time;

    Preferences preferences;

    Menu toolbarmenu;

    RecyclerView recycler_interface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat);

        messageEditText = findViewById(R.id.message_interface);
        send_message = findViewById(R.id.send_message_interface);
        recycler_interface = findViewById(R.id.chats_interface);
        attach = findViewById(R.id.attachFiles);

        apiServe = Client.getClient("https://fcm.googleapis.com/").create(ApiServe.class);
        recycler_interface.setHasFixedSize(false);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setReverseLayout(true);
        recycler_interface.setLayoutManager(manager);

        preferences = new Preferences(getApplicationContext());
        storageReference= FirebaseStorage.getInstance().getReference();

        Oid = getIntent().getStringExtra("OpponentUid");
        OName = getIntent().getStringExtra("OpponentName");
        OUsername = getIntent().getStringExtra("OpponentUsername");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        uid = mUser.getUid();

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_green);
            setTitle(OName);
        }


        showChatMessages();

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "text";
                message = messageEditText.getText().toString();
                time = System.currentTimeMillis();
                sendmessage();
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
//                AlertDialog.Builder builder= new AlertDialog.Builder(ChatInterface.this);
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

    private void sendmessage() {

        if(!message.isEmpty()){

            Map<String, Object> chat = new HashMap<>();
            chat.put("message", message);
            chat.put("time", time);
            chat.put("myUid", mUser.getUid());
            chat.put("OpponentUid",Oid);
            chat.put("type",type);
            chat.put("SorR","S");
            chat.put("Ousername",OUsername);
            chat.put("Oname",OName);
            firestore.collection("All Users").document(mUser.getUid()).collection("Contacts")
                    .document(Oid).collection("Chats")
                    .add(chat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    sendMessagetoOpponentDatabase();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendMessagetoOpponentDatabase() {
        Map<String, Object> chat = new HashMap<>();
        chat.put("message", message);
        chat.put("time",time);
        chat.put("myUid", Oid);
        chat.put("OpponentUid",mUser.getUid());
        chat.put("type",type);
        chat.put("SorR","R");
        chat.put("Ousername",preferences.getData("username"));
        chat.put("Oname",preferences.getData("usernameAdded"));
        firestore.collection("All Users").document(Oid).collection("Contacts")
                .document(mUser.getUid()).collection("Chats")
                .add(chat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
//                Toast.makeText(ChatInterface.this, "Message Sent", Toast.LENGTH_SHORT).show();
                showChatMessages();
                messageEditText.setText(null);
                sendCloudNotification(message, Oid, OUsername);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void sendCloudNotification(String message, String uid, String username) {
        //get opponents token
        firestore.collection("All Users").document(Oid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String opponentsToken;
                opponentsToken = documentSnapshot.getString("FCM_token");
                if(opponentsToken!= null){
                    String Title = "@"+preferences.getData("username");
                    String Message = message;
                    Data data = new Data(Title, Message);
                    NotificationSender sender= new NotificationSender(data, opponentsToken);
                    apiServe.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code() == 200){
                                if(response.body().success != 1){
                                    Toast.makeText(getApplicationContext(), "Failed to send notification!", Toast.LENGTH_SHORT).show();
                                }
                                else{
//                                    Toast.makeText(getApplicationContext(), "FCM sent", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Failed!"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
                    .start(ChatInterface.this);
        }
        if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            Uri uri = UCrop.getOutput(data);
            final ProgressDialog dialog = new ProgressDialog(ChatInterface.this);
            dialog.setMessage("Posting...");
            dialog.show();
            final Long currentTime = System.currentTimeMillis();
            final String atime = currentTime + "";
            final StorageReference fileref = storageReference.child("Image Personal Messages").child(uid)
                    .child(uid + Oid + atime);
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
                        message = myUrl;
                        time = currentTime;
                        type = "image";
                        sendmessage();
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    private void showChatMessages() {
        firestore.collection("All Users").document(mUser.getUid()).collection("Contacts")
                .document(Oid).collection("Chats")
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.d("Boom", "listen failed: " + error.getMessage());
                        }

                        else{
                            Log.d("Boom", "Snapshot worked");
                            List<ChatModel> list = new ArrayList<>();
                            list.clear();
                            for(QueryDocumentSnapshot query : value){
                                list.add(new ChatModel(
                                        query.getString("message")
                                        , query.getString("myUid")
                                        , query.getString("OpponentUid")
                                        , query.getString("type")
                                        , query.getString("SorR")
                                        , query.getString("Ousername")
                                        , query.getString("Oname")
                                        , query.getLong("time")
                                ));
                            }
                            recycler_interface.setAdapter(new ChatMessageRecycerAdapter(list,getApplicationContext()));
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}