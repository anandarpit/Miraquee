package com.example.chatterboi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;
//a
public class Log_in extends AppCompatActivity {

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    TextView forgotpassword;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Button log;
    MaterialEditText email, pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        firebaseAuth = firebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        forgotpassword = findViewById(R.id.forgot_password);
       log =  findViewById(R.id.login);
       email= findViewById(R.id.login_email);
        pass= findViewById(R.id.login_password);
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
                if (Pass.length()<7)
                {
                    pass.setError("Password must contain atleast 8 characters");
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser users=firebaseAuth.getCurrentUser();
                            if (users.isEmailVerified()) {
                                Toast.makeText(Log_in.this, "Login Sucessful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Log_in.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(Log_in.this, "Email not Verified", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                            Toast.makeText(Log_in.this, "Login Failed!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
            }
}