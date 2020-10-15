package com.example.chatterboi.shivam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterboi.R;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {
    private static final int SENT = 0;
    private static final int RECEIVED = 1;

    private String userId;
    private List<Chat> chats;

    public ChatsAdapter(List<Chat> chats, String userId) {
        this.chats = chats;
        this.userId = userId;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_chat_sent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_chat_received, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        holder.bind(chats.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (chats.get(position).getSenderId().contentEquals(userId)) {
            return SENT;
        } else {
            return RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public ChatViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message);
        }

        public void bind(Chat chat) {
            message.setText(chat.getMessage());
        }
    }
}