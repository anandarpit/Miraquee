package com.example.chatterboi.afterauthenticated;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.chatterboi.Preferences;
import com.example.chatterboi.R;
import com.example.chatterboi.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomePageArpit extends AppCompatActivity {

    FloatingActionButton fab;
    TabAdapter tabAdapter;
    Toolbar toolbar;
    TabLayout tabLayout;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    String  uid;

    int[] colorIntArray = {R.color.colorAccent,R.color.colorPrimaryDark,0};
    int[] iconIntArray = {R.drawable.ic_add_post, R.drawable.ic_add_group,0};

    ImageView imageView;

    com.example.chatterboi.Preferences pref;
    ViewPager myViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_arpit);

        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tablayout);
        myViewPager = findViewById(R.id.myViewPager);
        tabLayout = findViewById(R.id.tablayout);
        imageView = findViewById(R.id.icon);

        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        uid = mAuth.getCurrentUser().getUid();

        String CHANNEL_ID = "MESSAGE";
        String CHANNEL_NAME = "MESSAGE";

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                myViewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() != 2) {
                    animateFab(tab.getPosition());
                }
                if(tab.getPosition() == 2){
                    fab.hide();
                }
                if(tab.getPosition() == 1){
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(HomePageArpit.this, AddGroups.class));
                        }
                    });
                }
                if(tab.getPosition() == 0){
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(HomePageArpit.this,AddPosts.class));
                        }
                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        List<File> files = new ArrayList<>(Arrays.asList(getCacheDir().listFiles()));
        for(File file : files){
            if(file.getName().equals(uid + ".jpg")){
                Toast.makeText(getApplicationContext(), "Uri loaded through file", Toast.LENGTH_SHORT).show();
                Picasso
                        .get() // if file found then load it
                        .load(file)
                        .into(imageView);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Title")
                .setContentText("Message")
                .build();
        manager.notify(22, notification);


        pref = new Preferences(getApplicationContext());
        tabAdapter = new TabAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(myViewPager);

        Thread thread = new Thread() {
            @Override
            public void run() {
                DocumentReference docRef = firestore.collection("All Users").document(uid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.getString("username");
                                String name = document.getString("Name");
                                Log.d("Check1", "Username " + username);
                                pref.setData("usernameAdded", name);
                                pref.setData("username", username);
                            } else {
                                Log.d("Check1", "No such document");
                            }
                        } else {
                            Log.d("Check1", "get failed with ", task.getException());
                        }
                    }
                });
            }};
        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ahomepage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logoutmenu){

            pref.removeData("LoggedIn");
            pref.removeData("Registered");
            pref.removeData("usernameAdded");
            pref.removeData("username");

            Intent intent = new Intent(HomePageArpit.this , Register.class);
            startActivity(intent);
            finish();
        }
        if(item.getItemId() == R.id.profile){
            startActivity(new Intent(this,myProfile.class));
        }
        return true;
    }


    protected void animateFab(final int position) {
        fab.clearAnimation();

        // Scale down animation
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.1f, 1f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(10);     // animation duration in milliseconds
        shrink.setInterpolator(new AccelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Change FAB color and icon
                fab.show();
                fab.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), colorIntArray[position]));
                fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), iconIntArray[position]));

                // Rotate Animation
                Animation rotate = new RotateAnimation(60.0f, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
                rotate.setDuration(70);
                rotate.setInterpolator(new DecelerateInterpolator());

                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.1f, 1f, 0.1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(80);     // animation duration in milliseconds
                expand.setInterpolator(new DecelerateInterpolator());

                // Add both animations to animation state
                AnimationSet s = new AnimationSet(false); //false means don't share interpolators
                s.addAnimation(rotate);
                s.addAnimation(expand);
                fab.startAnimation(s);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(shrink);
    }
}