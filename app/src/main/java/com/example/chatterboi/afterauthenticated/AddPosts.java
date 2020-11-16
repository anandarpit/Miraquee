
package com.example.chatterboi.afterauthenticated;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatterboi.Preferences;
import com.example.chatterboi.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

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
    TextView name,textforpost, addPhoto, X;
    String textofpost, uid;
    Preferences pref;
    Button post;
    Uri uri;
    ImageView imageSelected;
    StorageReference storageReference;
    Long currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_posts);
        pref = new Preferences(getApplicationContext());

        circularImageView = findViewById(R.id.circularImageView);
        name = findViewById(R.id.username_post);
        X = findViewById(R.id.x);
        textforpost = findViewById(R.id.textforpost);
        post = findViewById(R.id.post);
        addPhoto = findViewById(R.id.add_photo_text);
        imageSelected = findViewById(R.id.imageView3);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,1000);
            }
        });
        storageReference= FirebaseStorage.getInstance().getReference();
        name.setText(pref.getData("usernameAdded"));

        mAuth =  FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = mAuth.getCurrentUser().getUid();


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTime = System.currentTimeMillis();

                textofpost = textforpost.getText().toString();
                if(textofpost.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Text can not be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    ProgressDialog dialog = new ProgressDialog(AddPosts.this);
                    dialog.setMessage("Posting...");
                    dialog.show();

                    Map<String, Object> post = new HashMap<>();
                    post.put("Text of Post",textofpost);
                    post.put("userid",uid);
                    post.put("name",pref.getData("usernameAdded"));
                    post.put("time",currentTime);
                    db.collection("All Posts").add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Toast.makeText(getApplicationContext(), "Text Uploaded", Toast.LENGTH_SHORT).show();

                        }
                    });
                    if(uri != null){
                        final StorageReference fileref = storageReference.child("Post Photos")
                                .child(uid + currentTime.toString());
                        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(getApplicationContext(), "Photo Uploaded!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    dialog.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },2000);
                }
            }
        });
        userpic();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            if(imageUri!= null) {
                UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis() + ".jpg" )))
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(200, 200)
                        .start(AddPosts.this);
            }
        }
        else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
           uri = UCrop.getOutput(data);
            imageSelected.setImageURI(uri);
            imageSelected.setVisibility(View.VISIBLE);
        }
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