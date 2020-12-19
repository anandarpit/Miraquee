package com.example.chatterboi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.chatterboi.Constants.Constants;
import com.example.chatterboi.afterauthenticated.HomePageArpit;
import com.example.chatterboi.afterauthenticated.Requests;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    String title= null, message=null;

    String CHANNEL_ID = "MESSAGE";
    String CHANNEL_NAME = "MESSAGE";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM","Token:"+ token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);




        try {
            title = remoteMessage.getData().get("Title");
            message = remoteMessage.getData().get("Message");
        }catch (Exception e){

        }
//
        if(title!=null && message!= null) {
            sendNotification(remoteMessage);
        }
//
////            NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplicationContext())
////                  .setSmallIcon(R.drawable.ic_launcher_foreground)
////                    .setContentTitle(title)
////                    .setAutoCancel(false)
////                    .setContentText(message);
////            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
////            manager.notify(22, builder.build());

//        }





        String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);
        if(type != null){
            if(type.equals(Constants.REMOTE_MSG_INVITATION)){

                Intent intent = new Intent(getApplicationContext(),IncomingInvitationActivity.class);
                intent.putExtra(
                        Constants.REMOTE_MSG_MEETING_TYPE,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_TYPE)
                );
                intent.putExtra(
                        "name",remoteMessage.getData().get("name")
                );
                intent.putExtra(
                        "username",remoteMessage.getData().get("username")
                );
                intent.putExtra(
                        "uid", remoteMessage.getData().get("uid")
                );
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITER_TOKEN,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITER_TOKEN)
                );
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITER_TOKEN,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITER_TOKEN)
                );
                intent.putExtra(
                        Constants.REMOTE_MSG_MEETING_ROOM,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_ROOM)                );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else if(type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
                Intent intent = new Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE);
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITATION_RESPONSE,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                );
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }

    }

        private void sendNotification(RemoteMessage remoteMessage) {
            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
//
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
//            Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notifsy);
//            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.splashpic)
                    .setContentTitle(title)
                    .setAutoCancel(false)
                    .setContentText(message)
                    .build();
            notification.sound = Uri.parse("android.resource://"
                    + getPackageName() + "/" + R.raw.notifsy);
            manager.notify(22, notification);

        }


    }

