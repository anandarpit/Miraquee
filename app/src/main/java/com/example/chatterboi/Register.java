package com.example.chatterboi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class Register extends AppCompatActivity {
    MaterialEditText email, password, cnfpassword;
    Button register, signin;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    ConstraintLayout activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.email);
        password = findViewById(R.id.newpass);
        cnfpassword = findViewById(R.id.confirmpass);
        register = findViewById(R.id.button);
        signin = findViewById(R.id.signin);
        activity = findViewById(R.id.register_activity);

        mAuth= FirebaseAuth.getInstance();

        
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Email=email.getText().toString().trim();
                final String Pass=password.getText().toString().trim();
                final String cnfpass = cnfpassword.getText().toString().trim();
                if(TextUtils.isEmpty(Email)) {

                    email.setError("Email is Required");
                    return;
                }
                else if(TextUtils.isEmpty(Pass))
                {
                    password.setError("Password is Required");
                    return;
                }
                else if (Pass.length()<7)
                {
                    password.setError("Password must contain atleast 8 characters");
                    return;
                }
                else if(!Pass.equals(cnfpass)){
                    cnfpassword.setError("Passwords Do not match");
                    password.setError("Password Do not match");
                    return;
                }
                else{
                    //to Close the keyboard when the user hits the register button
                    View keyview = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                registerUser(Email,Pass);
                Snackbar.make(activity, " Register Pressed", Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }}
        });
    }

    public void registerUser(String uEmail, String uPass){
        mAuth.createUserWithEmailAndPassword(uEmail,uPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar.make(activity, "Verification Mail sent! Please Verify Your Email!", Snackbar.LENGTH_LONG).setAction("Action",null).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                        startActivity(new Intent(Register.this, sign_in.class));
                                        finish();
                                }
                            }, 2500);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(activity, "Error Occured", Snackbar.LENGTH_LONG).setAction("Action",null).show();

                        }
                    });
                    String uId = firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("UserId").child(uId);
                }

            }
        });
    }
}