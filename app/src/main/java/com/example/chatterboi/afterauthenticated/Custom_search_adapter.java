package com.example.chatterboi.afterauthenticated;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterboi.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Custom_search_adapter extends RecyclerView.Adapter<Custom_search_adapter.myAdapter> {
    List<SearchModel> list;
    Context context;

    StorageReference storageReference;

    public Custom_search_adapter(List<SearchModel> list, Context context) {
        this.list = list;
        this.context = context;
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public Custom_search_adapter.myAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_search_element,
                        parent,
                        false);
        return new Custom_search_adapter.myAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Custom_search_adapter.myAdapter holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myAdapter extends RecyclerView.ViewHolder {
        TextView name, username;
        CircularImageView image;
        public myAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(SearchModel searchModel) {
            name =itemView.findViewById(R.id.searchname);
            username = itemView.findViewById(R.id.searchusername);
            image = itemView.findViewById(R.id.searchChatCiv);

            name.setText(searchModel.getName());
            username.setText("@"+searchModel.getUsername());

            StorageReference profoleRef = storageReference.child("Profile Photos").child(searchModel.getUid());
            profoleRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("Check", "Uri has been received");
                    Picasso.get().load(uri).into(image);
                }
            });
        }
    }
}
