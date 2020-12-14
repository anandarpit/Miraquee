package com.example.chatterboi.afterauthenticated;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.chatterboi.R;

public class comments extends AppCompatActivity {

    private RecyclerView CommentList;
    private ImageButton PostCommentButton;
    private EditText CommentInputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        CommentList = findViewById(R.id.comment_list);
        CommentList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentList.setLayoutManager(linearLayoutManager);


        CommentInputText = findViewById(R.id.comment_input);
        ImageButton send_image= findViewById(R.id.send_img);
    }
}