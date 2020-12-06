package com.example.chatterboi.afterauthenticated;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterboi.R;
import com.squareup.picasso.Picasso;

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
        holder.setIsRecyclable(false);  // very important
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
        TextView messageText, nameText, nameOverImage;
        CardView cardView ,textCardview;
        ImageView image;

        public myInterface(@NonNull View itemView) {
            super(itemView);
        }

        public void caller(ChatModel chatModel) {

            try {
                nameText = itemView.findViewById(R.id.name);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try{
                nameOverImage = itemView.findViewById(R.id.nameOverImage);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            messageText = itemView.findViewById(R.id.arpit_chat_message);
            cardView = itemView.findViewById(R.id.imageCardview);
            textCardview = itemView.findViewById(R.id.textCardview);
            image = itemView.findViewById(R.id.messageImage);

            if(chatModel.getType().equals("text")) {
                    messageText.setText(chatModel.getMessage());
                    textCardview.setVisibility(View.VISIBLE);
                try {
                    nameText.setText("@" + chatModel.getUsername());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            if(chatModel.getType().equals("image")){

                try {
                    nameText.setText("@" + chatModel.getUsername());
                } catch (Exception e){
                    e.printStackTrace();
                }
                cardView.setVisibility(View.VISIBLE);
                Picasso.get().load(Uri.parse(chatModel.getMessage())).into(image);

            }
        }
    }
}
