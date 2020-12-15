package com.example.chatterboi.afterauthenticated;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterboi.R;
import com.example.chatterboi.SharedPreferences.Preferences;
import com.example.chatterboi.listeners.ContactListeners;
import com.example.chatterboi.model.ContactModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.myAdapter> {
    List<ContactModel> list;
    ContactListeners contactListeners;
    Context context;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String uid;
    Preferences prefs;
    StorageReference storageReference;


    public ChatRecyclerAdapter(List<ContactModel> list, Context context, ContactListeners contactListeners) {
        this.list = list;
        this.contactListeners = contactListeners;
        this.context = context;
        prefs = new Preferences(context);
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth =  FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ChatRecyclerAdapter.myAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_element_layout,
                        parent,
                        false);
        return new ChatRecyclerAdapter.myAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerAdapter.myAdapter holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myAdapter extends RecyclerView.ViewHolder {

        CircularImageView chatCiv;
        TextView contactname;
        TextView username;
        ImageView audioCall, videoCall;
        ConstraintLayout constraintLayout;

        public myAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(ContactModel contactModel) {
            chatCiv = itemView.findViewById(R.id.ChatCiv);
            contactname = itemView.findViewById(R.id.contactname);
            username =itemView.findViewById(R.id.username);
            audioCall =itemView.findViewById(R.id.audioCall);
            videoCall = itemView.findViewById(R.id.VideoCall);
            constraintLayout = itemView.findViewById(R.id.constraint);
            StorageReference profoleRef = storageReference.child("Profile Photos").child(contactModel.getUid());
            profoleRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(chatCiv);
                }
            });

            username.setText("@"+contactModel.getUsername());
            contactname.setText(contactModel.getName());

            audioCall.setOnClickListener(view -> {
                contactListeners.initiateAudioMeeting(contactModel);
            });

            videoCall.setOnClickListener( view -> {
                contactListeners.initiateVideoMeeting(contactModel);
            });



            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatInterface.class);
                    intent.putExtra("OpponentUid",contactModel.getUid());
                    intent.putExtra("OpponentName",contactModel.getName());
                    intent.putExtra("OpponentUsername",contactModel.getUsername());
                    context.startActivity(intent);
                }
            });
        }
    }
}
