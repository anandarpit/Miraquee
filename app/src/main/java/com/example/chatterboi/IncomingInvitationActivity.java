 package com.example.chatterboi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatterboi.Constants.Constants;
import com.example.chatterboi.network.ApiClient;
import com.example.chatterboi.network.ApiService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingInvitationActivity extends AppCompatActivity {

    String type, name, username, uid;
    ImageView invitationImage, accept, reject;
    CircularImageView userImage;
    TextView Name, Username;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_invitation);

        invitationImage  = findViewById(R.id.invitationImage);
        userImage = findViewById(R.id.invitation_user_image);
        Name = findViewById(R.id.invitationName);
        Username = findViewById(R.id.invitationUserName);
        accept = findViewById(R.id.accept_invitation);
        reject = findViewById(R.id.reject_invitation);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.invitation));

        mAuth =  FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        type = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);
        name = getIntent().getStringExtra("name");
        username = getIntent().getStringExtra("username");
        uid = getIntent().getStringExtra("uid");

        if(type != null){
            if (type.equals("audio")) {
                invitationImage.setImageResource(R.drawable.ic_call_audio);
            }
            if(type.equals("video")){
                invitationImage.setImageResource(R.drawable.ic_call_video);
            }
        }

        if(name != null){
            Name.setText(name);
        }
        if(username != null){
            Username.setText(String.format("@%s", username));
        }

        getImage(uid);

        accept.setOnClickListener(view -> {
            sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED,getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
        });

        reject.setOnClickListener(view -> {
            sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED, getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
        });

    }

    private void getImage(String uid) {
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profoleRef = storageReference.child("Profile Photos").child(uid);
        profoleRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(userImage);
            }
        });
    }


    public void sendInvitationResponse(String type, String receiverToken){
        try{
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,type);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(),type );
        }
        catch(Exception e){
            Toast.makeText(this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void sendRemoteMessage(String remoteMessageBody, String type){
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getremotemessageHeaders(), remoteMessageBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()){
                    if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
//                        Toast.makeText(IncomingInvitationActivity.this,"Invitation Accepted!",Toast.LENGTH_SHORT).show();

                        try {

                            URL serverUrl = new URL("https://meet.jit.si");
                            JitsiMeetConferenceOptions conferenceOptions = new JitsiMeetConferenceOptions.Builder()
                                    .setServerURL(serverUrl)
                                    .setWelcomePageEnabled(false)
                                    .setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))
                                    .build();
                            JitsiMeetActivity.launch(IncomingInvitationActivity.this,conferenceOptions);
                            finish();

                        }catch (Exception e){
                            Toast.makeText(IncomingInvitationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                    else{
                        Toast.makeText(IncomingInvitationActivity.this, "Invitation Rejected", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else{
                    Toast.makeText(IncomingInvitationActivity.this, "Invitation Failed: "+ response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull  Throwable t) {
                Toast.makeText(IncomingInvitationActivity.this, "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
}
    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null){
                if(type.equals(Constants.REMOTE_MSG_INVITATION_CANCALLED)){
                    Toast.makeText(IncomingInvitationActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }
}
