
package com.example.chatterboi.afterauthenticated;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatterboi.Preferences;
import com.example.chatterboi.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPosts extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    CircularImageView circularImageView;
    TextView name,textforpost;
    String textofpost, uid;
    Preferences pref;
    Button post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_posts);
        pref = new Preferences(getApplicationContext());

        circularImageView = findViewById(R.id.circularImageView);
        name = findViewById(R.id.username_post);
        textforpost = findViewById(R.id.textforpost);
        post = findViewById(R.id.post);

        name.setText(pref.getData("usernameAdded"));

        mAuth =  FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = mAuth.getCurrentUser().getUid();


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textofpost = textforpost.getText().toString();
                if(textofpost.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Text can not be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Map<String, Object> post = new HashMap<>();
                    post.put("Text of Post",textofpost);
                    post.put("userid",uid);
                    post.put("name",pref.getData("usernameAdded"));
                    post.put("time",System.currentTimeMillis());
                    db.collection("All Posts").add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "Text Uploaded", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
        userpic();
    }

    private void userpic() {
        List<File> files = new ArrayList<>(Arrays.asList(getCacheDir().listFiles()));
        for(File file : files){
            if(file.getName().equals(uid + ".jpg")){
                Toast.makeText(getApplicationContext(), "Uri loaded through file", Toast.LENGTH_SHORT).show();
                Picasso
                        .get() // if file found then load it
                        .load(file)
                        .into(circularImageView);
            }
        }
    }
}