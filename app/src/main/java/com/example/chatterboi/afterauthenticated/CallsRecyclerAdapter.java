package com.example.chatterboi.afterauthenticated;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterboi.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CallsRecyclerAdapter extends RecyclerView.Adapter<CallsRecyclerAdapter.myAdapter> {
    List<CallsModel> list;
    Context context;
    StorageReference storageReference;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String uid;
    
    public CallsRecyclerAdapter(List<CallsModel> allList, Context context) {
        list = allList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth =  FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public CallsRecyclerAdapter.myAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calls_element,
                        parent,
                        false);
        return new CallsRecyclerAdapter.myAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallsRecyclerAdapter.myAdapter holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myAdapter extends RecyclerView.ViewHolder {

        CircularImageView civ;
        TextView name, username;
        ImageView callsDirection;

        public myAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(CallsModel callsModel) {
            civ = itemView.findViewById(R.id.CallsCiv);
            name = itemView.findViewById(R.id.contactname);
            username = itemView.findViewById(R.id.username);
            callsDirection = itemView.findViewById(R.id.callsdirection);


            storageReference.child("Profile Photos").child(callsModel.getUid())
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(civ);
                }
            });
            name.setText(callsModel.getName());
            username.setText(callsModel.getUsername());
            if(callsModel.getSorR().equals("S")){
                callsDirection.setImageResource(R.drawable.ic_call_made);
            }
            if(callsModel.getSorR().equals("R")){
                callsDirection.setImageResource(R.drawable.ic_call_received);
            }
        }
    }
}
