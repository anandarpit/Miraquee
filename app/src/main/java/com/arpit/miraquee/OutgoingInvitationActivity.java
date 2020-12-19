package com.arpit.miraquee;

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

import com.arpit.miraquee.Constants.Constants;
import com.arpit.miraquee.SharedPreferences.Preferences;
import com.arpit.miraquee.model.ContactModel;
import com.arpit.miraquee.network.ApiClient;
import com.arpit.miraquee.network.ApiService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitationActivity extends AppCompatActivity {

    ImageView typeOfMeeting , stop;
    CircularImageView contactImage;
    TextView name, username;
    StorageReference storageReference;
    Preferences prefs;
    String inviterToken = null;
    ContactModel contactModel;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String uid;
    String meetingRoom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.invitation));

        mAuth =  FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        typeOfMeeting = findViewById(R.id.invitationImage);
        name = findViewById(R.id.invitationName);
        username = findViewById(R.id.invitationUserName);
        stop = findViewById(R.id.back_invitation);
        contactImage = findViewById(R.id.invitation_user_image);

        prefs = new Preferences(getApplicationContext());


        String token = getIntent().getStringExtra("token");
        String type = getIntent().getStringExtra("type");
        contactModel =(ContactModel) getIntent().getSerializableExtra("contact");

        if(type != null){
            if (type.equals("audio")) {
                typeOfMeeting.setImageResource(R.drawable.ic_call_audio);
            }
            if(type.equals("video")){
                typeOfMeeting.setImageResource(R.drawable.ic_call_video);
            }


        }


        if(contactModel != null){
            name.setText(contactModel.getName());
            username.setText(String.format("@%s", contactModel.getUsername()));
        }


        getUserImage(contactModel.getUid());

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    inviterToken = task.getResult().getToken();
                    if(type != null && token!= null){
                        initiateMeeting(type, token); // audio or video , my token
                    }
                }
            }
        });
        stop.setOnClickListener(view -> {
           CancelledResponse(token);
        });


    }

    private void getUserImage(String uid) {
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profoleRef = storageReference.child("Profile Photos").child(uid);
        profoleRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(contactImage);
            }
        });
    }

    private void initiateMeeting(String meetingType, String receiverToken){

        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();




            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION); // yes! you are being invited, not inviting
            data.put(Constants.REMOTE_MSG_MEETING_TYPE,meetingType);    // audio or video which type to be initiated
            data.put("name",prefs.getData("usernameAdded")); // inviter name(i.e my)
            data.put("username",prefs.getData("username"));  // inviter username(i.e. my)
            data.put("uid", uid);                                // inviter uid(i.e. my)
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken); // inviter token(i.e my token)

            meetingRoom = uid+ "_" + UUID.randomUUID().toString().substring(0,5);
            data.put(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom);


            body.put(Constants.REMOTE_MSG_DATA,data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);

        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void sendRemoteMessage(String remoteMessageBody, String type){
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getremotemessageHeaders(), remoteMessageBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull  Call<String> call,@NonNull Response<String> response) {
                if(response.isSuccessful()){
                    if(type.equals(Constants.REMOTE_MSG_INVITATION)){
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitation Has been sent", Toast.LENGTH_SHORT).show();
                    }
                    else if(type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitation Cancelled", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitation Failed: "+ response.message(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull  Throwable t) {
                Toast.makeText(OutgoingInvitationActivity.this, "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void CancelledResponse(String receiverToken){
        try{
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,Constants.REMOTE_MSG_INVITATION_CANCALLED);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(),Constants.REMOTE_MSG_INVITATION_RESPONSE );
        }
        catch(Exception e){
            Toast.makeText(this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null){
                if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                    try {

                        URL serverUrl = new URL("https://meet.jit.si");
                        JitsiMeetConferenceOptions.Builder conferenceOptions = new JitsiMeetConferenceOptions.Builder()
                                .setServerURL(serverUrl)
                                .setWelcomePageEnabled(false)
                                .setRoom(meetingRoom);
                        if(type.equals("audio")){
                            conferenceOptions.setVideoMuted(true);
                        }

                        JitsiMeetActivity.launch(OutgoingInvitationActivity.this,conferenceOptions.build());
                        finish();

                    }catch (Exception e){
                        Toast.makeText(OutgoingInvitationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else if(type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)){
                    Toast.makeText(OutgoingInvitationActivity.this, "Rejected", Toast.LENGTH_SHORT).show();
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
    
}