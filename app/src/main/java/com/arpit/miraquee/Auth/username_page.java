package com.arpit.miraquee.Auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.arpit.miraquee.SharedPreferences.Preferences;
import com.arpit.miraquee.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class username_page extends AppCompatActivity {
    ImageView back;
    Button join;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    String Email,Name, Pass, userId, username;
    ConstraintLayout username_page;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    Preferences prefs;
    MaterialEditText name, usernameE;
    Boolean ok = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_page);

        back = findViewById(R.id.back);
        join = findViewById(R.id.button);
        name = findViewById(R.id.name_edittext);
        usernameE = findViewById(R.id.username_edittext);

        username_page = findViewById(R.id.username_page);

        prefs = new Preferences(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

        Email = getIntent().getStringExtra("Email");
        Pass = getIntent().getStringExtra("Pass");

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(username_page, "Wait...", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                username = usernameE.getText().toString().trim();
                Name = name.getText().toString();
                userId = mAuth.getCurrentUser().getUid();
                db = FirebaseFirestore.getInstance();

                if (Name.isEmpty()) {
                    name.setError("Name cannot be Empty");
                    return;
                }
                if(username.isEmpty()){
                    usernameE.setError("username cannot be empty");
                    return;
                }
                if(!username.equals(username.toLowerCase())){
                    usernameE.setError("No Caps Allowed");
                    return;
                }
                if (username.contains(" ")) {
                    usernameE.setError("No Spaces Allowed");
                    return;
                }

               db.collection("All Users").whereEqualTo("username", username)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null){
                                    Log.d("Check", "listen failed: " + error.getMessage());
                                }
                                List list = new ArrayList<>();
                                for(QueryDocumentSnapshot query : value){
                                    list.add(query.getString("username"));
                                }
                                if(list.isEmpty()){
                                    ok = true;
                                }
                                else{
                                    usernameE.setError("Username Taken");
                                }
                            }
                        });
                if(ok){
                    View keyview = getCurrentFocus();
                    if (keyview != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    Snackbar.make(username_page, "Successful", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    DocumentReference documentReference = db.collection("All Users").document(userId);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Name", Name);
                    user.put("username",username);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.v("Tag", "On Success: User Profile Created for" + userId);
                            prefs.setData("usernameAdded",Name);
                            prefs.setData("username",username);
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
                            intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);

                            finish();
                        }},1500);
                }
            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}