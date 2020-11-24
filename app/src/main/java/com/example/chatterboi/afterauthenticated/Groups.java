package com.example.chatterboi.afterauthenticated;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.chatterboi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Groups extends Fragment {

    RecyclerView recyclerView;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ImageSlider imageSlider;


    public Groups() {
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
        View view = inflater.inflate(R.layout.afragment_groups, container, false);

        recyclerView = view.findViewById(R.id.recyclerGroups);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);    // This somehow makes recycler view if inside scroll view smoothly work.
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final List<ChatLists> list = new ArrayList<>();

        imageSlider = view.findViewById(R.id.imageSlider);
        List<SlideModel> lista = new ArrayList<>();
        lista.add(new SlideModel("https://cdn.dnaindia.com/sites/default/files/styles/full/public/2017/11/04/622378-cat.jpg","", ScaleTypes.FIT));
        lista.add(new SlideModel("https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/dog-puppy-on-garden-royalty-free-image-1586966191.jpg?crop=1.00xw:0.669xh;0,0.190xh&resize=1200:*","" +
                "",ScaleTypes.FIT));
        lista.add(new SlideModel("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQFTVx3o8xfUiBum_qvU-oSkRpJ0cSeBeK7AQ&usqp=CAU",ScaleTypes.FIT));
        imageSlider.setImageList(lista,ScaleTypes.FIT);
        imageSlider.startSliding(2000);

        // Remember Event Listeners are asynchronous so set the Adapter only when the data has been recieved!
        db.collection("aGroups").orderBy("time", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                list.clear();
                Log.d("Check", "List Cleared Size Current" + list.size());
                for(QueryDocumentSnapshot snap: value){
                    list.add(new ChatLists(snap.getString("group"), snap.getId(), snap.getLong("time") ));
                }
                Log.d("Check", "Recycler View Created items:" + list.size());
                recyclerView.setAdapter(new Custom_recycler_adapter(list,getContext())); // this thing
            }
        });
        return view;
    }
}