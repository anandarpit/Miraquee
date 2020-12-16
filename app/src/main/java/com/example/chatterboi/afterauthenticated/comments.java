package com.example.chatterboi.afterauthenticated;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.FtsOptions;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatterboi.R;
import com.example.chatterboi.SharedPreferences.Preferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class comments extends AppCompatActivity {

     RecyclerView recyclerView;
     ImageView postComment;
     EditText commentInputText;
     String uid, postId;
     Preferences prefs;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        postComment = findViewById(R.id.send_img);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.comment));
        commentInputText = findViewById(R.id.comment_input);

        prefs = new Preferences(getApplicationContext());

        mAuth =  FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        postId = getIntent().getStringExtra("postId");
        postComment.setOnClickListener(view -> {postComment(postId);});

        recyclerView = findViewById(R.id.comment_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);

        showChatMessages();
    }

    private void showChatMessages() {
        List<CommentModel> list = new ArrayList<>();
        db.collection("All Posts").document(postId).collection("Comments")
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.d("comments", "listen failed: " + error.getMessage());
                        }
                        else{
                            list.clear();
                            for(QueryDocumentSnapshot snap: value){
                                list.add(new CommentModel(snap.getString("comment"),
                                        snap.getString("uid"),
                                        snap.getString("username"),
                                        snap.getLong("time")));
                            }
                            recyclerView.setAdapter(new CommentRecyclerAdapter(list,getApplicationContext()));
                        }

                    }
                });

    }

    private void postComment(String postId) {
        String commentText = commentInputText.getText().toString();

        if(!commentText.isEmpty()){
            Long time = System.currentTimeMillis();
            HashMap<String, Object> comment = new HashMap<>();
            comment.put("time",time);
            comment.put("comment",commentText);
            comment.put("uid",uid);
            comment.put("username", prefs.getData("username"));
            db.collection("All Posts").document(postId).collection("Comments").add(comment).addOnSuccessListener(documentReference -> {
                Log.d("Comment","Message added")       ;
            }).addOnFailureListener(e -> {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
        else{
            Toast.makeText(this, "Empty!", Toast.LENGTH_SHORT).show();
        }
        commentInputText.setText("");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_in_top);
    }
}