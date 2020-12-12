package com.example.chatterboi.afterauthenticated;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterboi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class RequestRecyclerInterface extends RecyclerView.Adapter<RequestRecyclerInterface.myAdapter> {

    List<Requestmodel> list;
    Context context;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String mUid;

    public RequestRecyclerInterface(List<Requestmodel> list, Context context) {
        this.list = list;
        this.context = context;
        db = FirebaseFirestore.getInstance();
        mAuth =  FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public RequestRecyclerInterface.myAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_search_element,
                        parent,
                        false);
        return new RequestRecyclerInterface.myAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestRecyclerInterface.myAdapter holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myAdapter extends RecyclerView.ViewHolder {
        public myAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Requestmodel requestmodel) {
        }
    }
}
