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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.myAdapter> {
    List<SearchModel> list;
    Context context;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String uid;
    Preferences prefs;
    StorageReference storageReference;


    public ChatRecyclerAdapter(List<SearchModel> list, Context context) {
        this.list = list;
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
        ImageView openChat;
        ConstraintLayout constraintLayout;

        public myAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(SearchModel searchModel) {
            chatCiv = itemView.findViewById(R.id.ChatCiv);
            contactname = itemView.findViewById(R.id.contactname);
            username =itemView.findViewById(R.id.username);
            openChat =itemView.findViewById(R.id.OpenChat);
            constraintLayout = itemView.findViewById(R.id.constraint);

            StorageReference profoleRef = storageReference.child("Profile Photos").child(searchModel.getUid());
            profoleRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("Check", "Uri has been received");
                    Picasso.get().load(uri).into(chatCiv);
                }
            });

            username.setText("@"+searchModel.username);
            contactname.setText(searchModel.getName());

            openChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Toast", Toast.LENGTH_SHORT).show();
                }
            });

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatInterface.class);
                    intent.putExtra("OpponentUid",searchModel.getUid());
                    intent.putExtra("OpponentName",searchModel.getName());
                    intent.putExtra("OpponentUsername",searchModel.getUsername());
                    context.startActivity(intent);
                }
            });

        }
    }
}
