package com.example.chatterboi.afterauthenticated;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chatterboi.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.util.FileUtil;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AddGroups extends AppCompatActivity {

    EditText roomname;
    LinearLayout view;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;
    Menu toolbarmenu;
    CircularImageView imageView;
    String room;
    Uri imageUri;

    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aactivity_create_group);

        roomname = findViewById(R.id.add_room_name);
        imageView = findViewById(R.id.addimage);


        storageReference= FirebaseStorage.getInstance().getReference();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,1000);
            }
        });


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
                        Uri urri;
                        room = roomname.getText().toString();
                        if(imageUri != null){
//                            File filePath = new File(SiliCompressor.with(this)
//                                    .compress(FileUtils.getPath(this, imageUri),
//                                            new File(this.getCacheDir(),"temp")));
//                             urri = Uri.fromFile(filePath);

                        final StorageReference fileref = storageReference.child("Groups Photo").child(room);
                        fileref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Picasso.get().load(uri).into(imageView);
                                    }
                                });
                            }
                        });
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
                    }}
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1000){
            {
                if(resultCode == Activity.RESULT_OK){
                    imageUri = data.getData();
                    imageView.setImageURI(imageUri);
                }
            }
        }
    }
}