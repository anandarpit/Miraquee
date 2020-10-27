package com.example.chatterboi.arpit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chatterboi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddGroups extends AppCompatActivity {

    EditText roomname;
    LinearLayout view;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;
    Menu toolbarmenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aactivity_create_group);

        roomname = findViewById(R.id.room_name);



        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_24);
            setTitle("Add Public Group");
        }

        mAuth = FirebaseAuth.getInstance();

        mUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acreate_groups_menu, menu);
        toolbarmenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                return true;

            case R.id.arpitmenu:

                Log.d("Add", "Tick button pressed");

                if (roomname.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Room name should not be empty", Toast.LENGTH_SHORT).show();
                }
                else
                    {
                        Map<String, Object> group = new HashMap<>();
                        group.put("group",roomname.getText().toString());
                        group.put("time",System.currentTimeMillis());
                        db.collection("aGroups").add(group)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("Check", "Group Added : " + roomname.getText().toString());
                                toolbarmenu.findItem(R.id.arpitmenu).setEnabled(false);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //TODO: Add an intent here to the updated Groups Fragment
                                        finish();
                                    }
                                },1000);
                            }
                        });
                    }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}