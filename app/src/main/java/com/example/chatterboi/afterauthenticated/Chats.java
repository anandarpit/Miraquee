package com.example.chatterboi.afterauthenticated;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.chatterboi.OutgoingInvitationActivity;
import com.example.chatterboi.R;
import com.example.chatterboi.listeners.ContactListeners;
import com.example.chatterboi.model.ContactModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Chats extends Fragment implements ContactListeners {

    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String uid;

    public Chats() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.afragment_explore, container, false);

        mAuth =  FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.chatRecycler);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        allList();

        return view;
    }

    private void allList() {
        List<ContactModel> allList = new ArrayList<>();
        db.collection("All Users").document(uid).collection("Contacts").whereEqualTo("Status",true)
//                .orderBy("timeOfFriendship", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        allList.clear();
                        if (error!=null){
                            Log.d(TAG,"Error:"+error.getMessage());
                        }
                        else{
                            for (QueryDocumentSnapshot snap : value) {
                                allList.add(new ContactModel(snap.getString("OpponentName"),
                                        snap.getString("OpponentUsername"),
                                        snap.getString("OpponentUid")));
                            }
                        }
                        recyclerView.setAdapter(new ChatRecyclerAdapter(allList,getContext(),Chats.this));
                    }
                });
    }

    @Override
    public void initiateVideoMeeting(ContactModel contactModel) {

        db.collection("All Users").document(contactModel.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult() != null && task.isSuccessful()){
                   String token = task.getResult().getString("FCM_token");
                   if(token == null || token.isEmpty()){
                       Toast.makeText(getContext(), "User is logged out of Miraquee", Toast.LENGTH_SHORT).show();
                   }
                   else{
                       Toast.makeText(getContext(), "Initiating Video Call", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(getContext(), OutgoingInvitationActivity.class);
                       intent.putExtra("contact", contactModel);
                       intent.putExtra("type","video");
                       startActivity(intent);
                   }
                }
            }
        });

    }

    @Override
    public void initiateAudioMeeting(ContactModel contactModel) {
        db.collection("All Users").document(contactModel.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult() != null && task.isSuccessful()){
                    String token = task.getResult().getString("FCM_token");
                    if(token == null || token.isEmpty()){
                        Toast.makeText(getContext(), "User is logged out of Miraquee", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Initiating Audio Call", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), OutgoingInvitationActivity.class);
                        intent.putExtra("contact", contactModel);
                        intent.putExtra("type","audio");
                        startActivity(intent);
                    }
                }
            }
        });
    }


}