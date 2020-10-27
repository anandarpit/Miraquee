package com.example.chatterboi.arpit;

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
import android.preference.Preference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.chatterboi.Preferences;
import com.example.chatterboi.R;
import com.example.chatterboi.Register;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomePageArpit extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    TabAdapter tabAdapter;
    Toolbar toolbar;
    TabLayout tabLayout;

    com.example.chatterboi.Preferences pref;
    ViewPager myViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_arpit);

        floatingActionButton = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tablayout);
        myViewPager = findViewById(R.id.myViewPager);
        tabLayout = findViewById(R.id.tablayout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chatter Boi");

        Context context;

        String CHANNEL_ID = "MESSAGE";
        String CHANNEL_NAME = "MESSAGE";

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePageArpit.this, AddGroups.class));
            }
        });
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
        return true;
    }
}