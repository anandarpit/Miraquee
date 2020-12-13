package com.example.chatterboi.afterauthenticated;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatterboi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Groups extends Fragment {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

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

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::getGroups);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(
                R.color.colorPrimaryDark
        ));

//        imageSlider = view.findViewById(R.id.imageSlider);
//        List<SlideModel> lista = new ArrayList<>();
//        lista.add(new SlideModel("https://cdn.dnaindia.com/sites/default/files/styles/full/public/2017/11/04/622378-cat.jpg","", ScaleTypes.FIT));
//        lista.add(new SlideModel("https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/dog-puppy-on-garden-royalty-free-image-1586966191.jpg?crop=1.00xw:0.669xh;0,0.190xh&resize=1200:*","" +
//                "",ScaleTypes.FIT));
//        lista.add(new SlideModel("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQFTVx3o8xfUiBum_qvU-oSkRpJ0cSeBeK7AQ&usqp=CAU",ScaleTypes.FIT));
//        imageSlider.setImageList(lista,ScaleTypes.FIT);
//        imageSlider.startSliding(2000);
        // Remember Event Listeners are asynchronous so set the Adapter only when the data has been recieved!

        getGroups();
        return view;
    }

    private void getGroups() {
        swipeRefreshLayout.setRefreshing(true);
        final List<ChatLists> list = new ArrayList<>();
        db.collection("aGroups").orderBy("time", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult() != null && task.isSuccessful())
                        {
                            list.clear();
                            Log.d("Check", "List Cleared Size Current" + list.size());
                            for (QueryDocumentSnapshot snap : task.getResult()) {
                                list.add(new ChatLists(snap.getString("group"), snap.getId(), snap.getLong("time"), snap.getString("username")));
                            }
                            Log.d("Check", "Recycler View Created items:" + list.size());
                            swipeRefreshLayout.setRefreshing(false);
                            recyclerView.setAdapter(new Group_recycler_adapter(list, getContext()));
                        }// this thing
                    }
                });
    }
}