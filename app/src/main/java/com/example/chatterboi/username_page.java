package com.example.chatterboi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class username_page extends AppCompatActivity {
    ImageView back;
    Button join;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    String Email,Name, Pass, userId;
    ConstraintLayout username_page;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    MaterialEditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_page);

        back = findViewById(R.id.back);
        join = findViewById(R.id.button);
        name = findViewById(R.id.name_edittext);
        username_page = findViewById(R.id.username_page);

        mAuth = FirebaseAuth.getInstance();

        Email = getIntent().getStringExtra("Email");
        Pass = getIntent().getStringExtra("Pass");

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View keyview = getCurrentFocus();
                if (keyview != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                Name = name.getText().toString();
                if (Name.isEmpty()) {
                    name.setError("Name cannot be Empty");
                    return;
                }
                userId = mAuth.getCurrentUser().getUid();
                db = FirebaseFirestore.getInstance();
                DocumentReference documentReference = db.collection("All Users").document(userId);
                Map<String, Object> user = new HashMap<>();
                user.put("Name", Name);
                documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.v("Tag", "On Success: User Profile Created for" + userId);
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(username_page.this, Log_in.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("Name", Name);
                        intent.putExtra("Email", Email);
                        intent.putExtra("Pass", Pass);
                        startActivity(intent);
                        finish();
                    }},1500);
            }
        });
    }
}