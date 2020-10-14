package com.example.chatterboi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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
    FirebaseFirestore db;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    String Email,Name, Pass;
    ConstraintLayout username_page;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
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
                Name= name.getText().toString();
                mAuth.signInWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            firebaseUser = mAuth.getCurrentUser();

                            if(firebaseUser.isEmailVerified()){
                                Intent intent = new Intent(username_page.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("Name",Name);
                                intent.putExtra("Email",Email);
                                intent.putExtra("Pass",Pass);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                popupDialog();
                            }
                        }else{
                            Snackbar.make(username_page, "Error! Not Signed In", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void popupDialog() {
        final Animation animation = new RotateAnimation(0.0f, 360.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            animation.setFillAfter(true);
            animation.setRepeatCount(-1);
            animation.setDuration(2000);
       //Animation for refresh image
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup_verification,null);

        builder.setView(view);
        dialog = builder.create();
        dialog.show();

        Button button_resend;
        button_resend = view.findViewById(R.id.button_resend);
        final ImageView resendimg;
        resendimg = view.findViewById(R.id.refresh);
        button_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                resendimg.setAnimation(animation); //This doesnot work, need to find a better alternative to rotate

                mAuth.sendPasswordResetEmail(Email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(view, "Email has been sent again!", Snackbar.LENGTH_LONG).show();new Handler().postDelayed(new Runnable() {
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
            }});
    }
}