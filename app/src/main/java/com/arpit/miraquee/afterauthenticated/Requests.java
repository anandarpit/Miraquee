package com.arpit.miraquee.afterauthenticated;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.arpit.miraquee.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Requests extends AppCompatActivity {

    RecyclerView recyclerView;
    Button all, received, sent;
    FirebaseFirestore db;
    ImageView goBack , info;
    FirebaseAuth mAuth;
    String uid;
    float scale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_and_sents);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.statusbacground));

        all = findViewById(R.id.all);
        received = findViewById(R.id.recieved);
        sent = findViewById(R.id.sent);
        goBack = findViewById(R.id.goBack);
        info = findViewById(R.id.info);
        recyclerView = findViewById(R.id.addrequestrecyclerView);

        recyclerView.setHasFixedSize(false);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);

        mAuth =  FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        scale = getApplicationContext().getResources().getDisplayMetrics().density;

        inflateButton(all);
        collapseButton(sent);
        collapseButton(received);

        allList();

        all.setOnClickListener(view -> allList());
        received.setOnClickListener(view -> receivedList());
        sent.setOnClickListener(view -> sentList());

        goBack.setOnClickListener(view -> finish());

        info.setOnClickListener(view -> showInfo());
    }

    private void showInfo() {
        AlertDialog.Builder builder;
        AlertDialog dialog;
        builder = new AlertDialog.Builder(Requests.this,R.style.myDialog);
        LayoutInflater inflater = (LayoutInflater) Requests.this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.popup_info,null);

        builder.setView(view);
        dialog = builder.create();
        dialog.show();

        Button dismiss = view.findViewById(R.id.confirm_logout);
        dismiss.setOnClickListener(view1 -> dialog.dismiss());
    }

    private void sentList() {

        inflateButton(sent);
        collapseButton(all);
        collapseButton(received);

        List<Requestmodel> sentList = new ArrayList<>();
        db.collection("All Users").document(uid).collection("Contacts")
                .whereEqualTo("Status",false)
                .whereEqualTo("SentOrRecieved","S")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        sentList.clear();
                        for(QueryDocumentSnapshot snap: value){
                            sentList.add(new Requestmodel(snap.getString("OpponentUid"),
                                    snap.getString("SentOrRecieved"),
                                    snap.getBoolean("Status"),
                                    snap.getString("OpponentUsername"),
                                    snap.getString("OpponentName")));
                        }
                        recyclerView.setAdapter(new RequestRecyclerInterface(sentList,getApplicationContext()));
                    }
                });
    }

    private void receivedList() {
        inflateButton(received);
        collapseButton(all);
        collapseButton(sent);
        List<Requestmodel> receivedList = new ArrayList<>();
        db.collection("All Users").document(uid).collection("Contacts")
                .whereEqualTo("Status",false)
                .whereEqualTo("SentOrRecieved","R")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        receivedList.clear();
                        for(QueryDocumentSnapshot snap: value){
                            receivedList.add(new Requestmodel(snap.getString("OpponentUid"),
                                    snap.getString("SentOrRecieved"),
                                    snap.getBoolean("Status"),
                                    snap.getString("OpponentUsername"),
                                    snap.getString("OpponentName")));
                        }
                        recyclerView.setAdapter(new RequestRecyclerInterface(receivedList,getApplicationContext()));
                    }
                });
    }

    private void allList() {
        inflateButton(all);
        collapseButton(sent);
        collapseButton(received);
        List<Requestmodel> allList = new ArrayList<>();
        db.collection("All Users").document(uid).collection("Contacts").whereEqualTo("Status",false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        allList.clear();
                        for(QueryDocumentSnapshot snap: value){
                            allList.add(new Requestmodel(snap.getString("OpponentUid"),
                                    snap.getString("SentOrRecieved"),
                                    snap.getBoolean("Status"),
                                    snap.getString("OpponentUsername"),
                                    snap.getString("OpponentName")));
                        }
                        recyclerView.setAdapter(new RequestRecyclerInterface(allList,getApplicationContext()));
                    }
                });
    }

    private void inflateButton(Button button) {
        button.setBackgroundResource(R.drawable.button_new_pressed);
        button.setTextColor(Color.parseColor("#000000"));
        LayoutParams params = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, (int) (30 * scale + 0.5f));
        params.setMargins(0, 0, (int) (8 * scale + 0.5f), 0);
        button.setLayoutParams(params);
    }

    private void collapseButton(Button button) {
        button.setBackgroundResource(R.drawable.button_new_unpressed);
        button.setTextColor(Color.parseColor("#2196F3"));
        LayoutParams params = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, (int) (26 * scale + 0.5f));
        params.setMargins(0, 0, (int) (8 * scale + 0.5f), 0);
        button.setLayoutParams(params);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }

}