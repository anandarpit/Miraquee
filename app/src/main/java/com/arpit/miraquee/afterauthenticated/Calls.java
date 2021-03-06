package com.arpit.miraquee.afterauthenticated;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arpit.miraquee.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Calls extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore db;
    String uid;
    RecyclerView recyclerView;

    public Calls() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls, container, false);

        auth =  FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.CallsRecyclerView);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        allList();

        return view;
    }

    private void allList() {
        List<CallsModel> allList = new ArrayList<>();
        db.collection("All Users").document(uid).collection("Calls")
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        allList.clear();
                        if (error!=null){
                            Log.d(TAG,"Error:"+error.getMessage());
                        }
                        else{
                            for (QueryDocumentSnapshot snap : value) {
                                allList.add(new CallsModel(
                                        snap.getString("uid"),
                                        snap.getString("SorR"),
                                        snap.getString("username"),
                                        snap.getString("name"),
                                        snap.getLong("time")));
                            }
                        }
                        recyclerView.setAdapter(new CallsRecyclerAdapter(allList,getContext()));
                    }
                });
    }
}