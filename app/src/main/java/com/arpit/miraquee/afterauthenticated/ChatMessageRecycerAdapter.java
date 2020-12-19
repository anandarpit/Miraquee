package com.arpit.miraquee.afterauthenticated;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.arpit.miraquee.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatMessageRecycerAdapter extends RecyclerView.Adapter<ChatMessageRecycerAdapter.myInterface>{

    List<ChatModel> list;
    Context context;

    final int SENT = 1;
    final int RECEIVED = 0;

    public ChatMessageRecycerAdapter(List<ChatModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatMessageRecycerAdapter.myInterface onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == RECEIVED){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.arpit_recieved_chat, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.arpit_sent_chat, parent, false);
        }
        return new ChatMessageRecycerAdapter.myInterface(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageRecycerAdapter.myInterface holder, int position) {
        holder.caller(list.get(position));
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getSorR().equals("S")){
            return SENT;
        }
        else{
            return RECEIVED;
        }
    }

    public class myInterface extends RecyclerView.ViewHolder {

        CardView imageCardview, textCardview;
        TextView message;
        ImageView image;


        public myInterface(View view) {
            super(view);
        }

        public void caller(ChatModel chatModel) {
            imageCardview = itemView.findViewById(R.id.imageCardview);
            textCardview = itemView.findViewById(R.id.textCardview);
            message = itemView.findViewById(R.id.arpit_chat_message);
            image = itemView.findViewById(R.id.messageImage);

            if(chatModel.getType().equals("text")){
                textCardview.setVisibility(View.VISIBLE);
                message.setText(chatModel.getMessage());
            }
            if(chatModel.getType().equals("image")){
                Picasso.get().load(chatModel.getMessage()).into(image);
                imageCardview.setVisibility(View.VISIBLE);
            }
        }
    }

}
