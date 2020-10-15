package com.example.chatterboi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatterboi.shivam.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class Log_in extends AppCompatActivity {
//a
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    TextView forgotpassword;
    FirebaseAuth firebaseAuth;
    FirebaseUser users;
    FirebaseFirestore db;
    Button log;
    Preferences pref;
    String userId;
    ConstraintLayout log_in;
    MaterialEditText email, pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        forgotpassword = findViewById(R.id.forgot_password);
        log =  findViewById(R.id.login);
        email= findViewById(R.id.login_email);
        pass= findViewById(R.id.login_password);
        log_in = findViewById(R.id.log_in);

        pref = new Preferences(getApplicationContext());

        firebaseAuth = firebaseAuth.getInstance();

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mview) {
                builder = new AlertDialog.Builder(Log_in.this);
                final View view = getLayoutInflater().inflate(R.layout.popup_forgotpassword,null);

                builder.setView(view);
                dialog = builder.create();
                dialog.show();

                final MaterialEditText email = view.findViewById(R.id.email_forgot_enter);
                Button resetEmail = view.findViewById(R.id.button_resend);
                resetEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        View keyview = getCurrentFocus();
                        if (keyview != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        final String Email = email.getText().toString().trim();
                        if (!Email.isEmpty()){
                            Snackbar.make(view, "Sending Email...", Snackbar.LENGTH_LONG).show();
                            firebaseAuth.sendPasswordResetEmail(Email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar.make(view, "Reset email has been sent to your email", Snackbar.LENGTH_LONG).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                        }
                                    }, 2500);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(view, "Error Occured!", Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                        else{
                            Snackbar.make(view, "Email is Empty!", Snackbar.LENGTH_LONG).show();
                        }

                    }
                });


            }});

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View keyview = getCurrentFocus();
                if (keyview != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                final String Email=email.getText().toString().trim();
                final String Pass=pass.getText().toString().trim();
                if(TextUtils.isEmpty(Email)) {

                    email.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(Pass))
                {
                    pass.setError("Password is Required");
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {

                            users =firebaseAuth.getCurrentUser();
                            if (users.isEmailVerified()) {
                                Snackbar.make(log_in, "Successful!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                userId= firebaseAuth.getCurrentUser().getUid();
                                db = FirebaseFirestore.getInstance();
                                DocumentReference documentReference=db.collection("All Users").document(userId);
                                Map<String,Object> user=new HashMap<>();
                                user.put("Pass",Pass);
                                documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.v("Tag","On Success: User Profile Created for"+userId);
                                    }
                                });
                                pref.setData("isVerified?","true");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        Intent intent = new Intent(Log_in.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 1500);
                            }
                            else {
                                    builder = new AlertDialog.Builder(Log_in.this);
                                    View view = getLayoutInflater().inflate(R.layout.popup_verification,null);
                                    builder.setView(view);
                                    dialog = builder.create();
                                    dialog.show();
                                }
                        }
                        else
                            Snackbar.make(log_in, "Login Failed!"+task.getException().getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });
            }
        });
            }
}