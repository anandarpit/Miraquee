 package com.example.chatterboi.afterauthenticated;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatterboi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Posts extends Fragment {


    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    RecyclerView recyclerView;

    int flag;
    public Posts() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth =  FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.afragment_posts, container, false);

        recyclerView = view.findViewById(R.id.post_recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        final List<PostModel> list = new ArrayList<>();
        db.collection("All Posts").orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        list.clear();
                        for(QueryDocumentSnapshot snap: value) {
                            String profilePic, postPic;
                            String name,text,time,uid;
                            String docId = snap.getId();
                            name = snap.getString("name");
                            text = snap.getString("Text of Post");
                            time = snap.getLong("time").toString();
                            uid = snap.getString("userid");
                            profilePic = snap.getString("profileUrl");
                            postPic = snap.getString("postUrl");


                            list.add(new PostModel(
                                    name,
                                    text,
                                    time,
                                    profilePic,
                                    postPic,
                                    uid,
                                    docId
                            ));
                        }
                        Log.d("XXX", "Recycler View Created items:" + list.size());
                    recyclerView.setAdapter(new Custom_post_adapter(list,getContext()));
                    }
                });
        return  view;
    }

}