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

import java.util.List;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.myAdapter>{

    List<CommentModel> list;
    Context context;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public CommentRecyclerAdapter(List<CommentModel> list, Context context) {
        this.list = list;
        this.context = context;
        mAuth =  FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CommentRecyclerAdapter.myAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_element,
                        parent,
                        false);
        return new CommentRecyclerAdapter.myAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRecyclerAdapter.myAdapter holder, int position) {
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

        public void bind(CommentModel commentModel) {
        }
    }
}
