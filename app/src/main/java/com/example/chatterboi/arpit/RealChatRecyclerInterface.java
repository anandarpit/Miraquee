package com.example.chatterboi.arpit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterboi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class RealChatRecyclerInterface extends RecyclerView.Adapter<RealChatRecyclerInterface.myInterface> {

    String userId;
    List<ChatModel> list;
    int flag = 0;

    final int SENT = 1;
    final int RECEIVED = 0;
    String currentUid, newUid, uid = "h";


    public RealChatRecyclerInterface(String userId, List<ChatModel> list) {
        this.list = list;
        this.userId = userId;

    }

    @NonNull
    @Override
    public RealChatRecyclerInterface.myInterface onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == RECEIVED){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.arpit_recieved_chat, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.arpit_sent_chat, parent, false);
        }
        return new myInterface(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RealChatRecyclerInterface.myInterface holder, int position) {
        holder.caller(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getUserId().equals(userId)){
            return SENT;
        }
        else{
            return RECEIVED;
        }
    }

    class myInterface extends RecyclerView.ViewHolder{
        TextView messageText, nameText;
        public myInterface(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.arpit_chat_message);

            try {
                nameText = itemView.findViewById(R.id.name);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void caller(ChatModel chatModel) {
            messageText.setText(chatModel.getMessage());
            currentUid = chatModel.getUserId();

            if(currentUid != uid) {
                try {
                    nameText.setText("@" + chatModel.getUsername());
                    uid = chatModel.getUserId();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
