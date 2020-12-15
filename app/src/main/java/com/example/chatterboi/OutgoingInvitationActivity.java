package com.example.chatterboi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatterboi.Constants.Constants;
import com.example.chatterboi.SharedPreferences.Preferences;
import com.example.chatterboi.model.ContactModel;
import com.example.chatterboi.network.ApiClient;
import com.example.chatterboi.network.ApiService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

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
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    inviterToken = task.getResult().getToken();
                }
            }
        });

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

        stop.setOnClickListener(view -> onBackPressed());
        getUserImage(contactModel.getUid());

        if(type != null && contactModel!= null){
            initiateMeeting(type, token); // audio or video , my token
        }

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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
    
}