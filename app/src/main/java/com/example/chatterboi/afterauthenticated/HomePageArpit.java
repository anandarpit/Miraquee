package com.example.chatterboi.afterauthenticated;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
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
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.chatterboi.SharedPreferences.Preferences;
import com.example.chatterboi.R;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class HomePageArpit extends AppCompatActivity {

    FloatingActionButton fab;
    TabAdapter tabAdapter;
    Toolbar toolbar;
    TabLayout tabLayout;

    StorageReference ref;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private InterstitialAd mInterstitialAd;

    private static int REQUEST_CODE_BATTERY_OPTIMIZATION = 1000;

    String  uid;

    int[] colorIntArray = {R.color.colorAccent,R.color.colorPrimaryDark,0};
    int[] iconIntArray = {R.drawable.ic_add_post, R.drawable.ic_add_group,0};

    ImageView imageView;

    Preferences pref;
    ViewPager myViewPager;

    String CHANNEL_ID = "MESSAGE";
    String CHANNEL_NAME = "MESSAGE";

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

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        uid = mAuth.getCurrentUser().getUid();
        ref = FirebaseStorage.getInstance().getReference().child("Profile Photos").child(uid);

        pref = new Preferences(getApplicationContext());

        LoadFragments load = new LoadFragments();
        load.execute();

        LoadPreferences loadPreferences = new LoadPreferences();
        loadPreferences.execute();


        loadPhotoFirst();

//        showNotification();

//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        setSupportActionBar(toolbar);
        checkForBatteryOptimizations();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    pref.setData("TOKEN", task.getResult().getToken());
                    sendFCMTokenToDatabase(task.getResult().getToken());
                }
            }
        });
    }

    private void showNotification() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(HomePageArpit.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(HomePageArpit.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Chatter-Boi")
                .setAutoCancel(false)
                .setContentText("Chatter-Boi is running...")
                .build();
        manager.notify(22, notification);
    }

    private void sendFCMTokenToDatabase(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference=db.collection("All Users").document(mUser.getUid());
        Map<String,Object> user=new HashMap<>();
        user.put("FCM_token",token);
        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(HomePageArpit.this, "Token Updated", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomePageArpit.this, "Token Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPhotoFirst() {
        File file = new File(getCacheDir() + File.separator + uid + ".jpg");
        if (!file.exists()) {
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
//                    Toast.makeText(getApplicationContext(), "Photo had to be downloaded", Toast.LENGTH_SHORT).show();
                    Picasso picasso = Picasso.get();
                    picasso.setIndicatorsEnabled(true);
                    picasso.load(uri).into(imageView);
                    loadImage(uri);
                }
            });
            return;
        }
        if(file.exists()){
            if (file.getName().equals(uid + ".jpg")) {
//                Toast.makeText(getApplicationContext(), "Photo From Cache", Toast.LENGTH_SHORT).show();
                Picasso
                        .get() // if file found then load it
                        .load(file)
                        .into(imageView);
            }
        }
    }
    public void loadImage(final Uri uri){
        Thread thread = new Thread() {
            @Override
            public void run() {
                Log.d("Checkere", "stuck at file");
                File file = new File(getCacheDir() + File.separator + uid + ".jpg");
                Log.d("Checkere", "file" + file);
                try {
                    Picasso picasso = Picasso.get();
                    Bitmap bitmap = picasso.load(uri).get();
                    Log.d("Checkere", "bitmap working");
                    FileOutputStream fOut = new FileOutputStream(file);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 20, out);
                    out.writeTo(fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                    Log.d("Checkere", e.getMessage());
                    e.printStackTrace();
                }
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
        if(item.getItemId() == R.id.profile){
            Intent intent = new Intent(this, myProfile.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        if(item.getItemId() == R.id.addPeople){
            Intent intent = new Intent(this,AddPeople.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
        }
        if(item.getItemId() == R.id.requests){
            Intent intent = new Intent(this, Requests.class);
            intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
//        if(item.getItemId() == R.id.ads){
//
//            if (mInterstitialAd.isLoaded()) {
//                mInterstitialAd.show();
//
//                mInterstitialAd = new InterstitialAd(this);
//                mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
//            } else {
//                Log.d("TAG", "The interstitial wasn't loaded yet.");
//            }
//        }
        return true;
    }


    private class LoadFragments extends AsyncTask  {
//        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
//            dialog  = new ProgressDialog(HomePageArpit.this);
//            dialog.setMessage("Posting...");
//            dialog.setCancelable(false);
//            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
//            dialog.dismiss();
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            tabAdapter = new TabAdapter(getSupportFragmentManager());
            myViewPager.setAdapter(tabAdapter);
            tabLayout.setupWithViewPager(myViewPager);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    myViewPager.setCurrentItem(tab.getPosition());

                    if (tab.getPosition() < 2) {
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
                                startActivity(new Intent(HomePageArpit.this, AddPosts.class));
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
            return null;
        }
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

    private class LoadPreferences extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
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
//                            Toast.makeText(getApplicationContext(), "Shared preferences ready", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("Check1", "No such document");
                        }
                    } else {
                        Log.d("Check1", "get failed with ", task.getException());
                    }
                }
            });
            return null;
        }
    }

    private void checkForBatteryOptimizations(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if(!powerManager.isIgnoringBatteryOptimizations(getPackageName())){
                Context context;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePageArpit.this);
                        alertDialog.setTitle("Warning")
                                .setMessage("Battery Optimization is Enabled. You won't be able to receive calls when your app is in background." +
                                        " Remember though that even if you follow this step and " +
                                        "turn off the background optimization, still there is a chance that you won't receive incoming calls " +
                                        "in background state. There is nothing I can do as popular apps " +
                                        " are whitelisted by google " +
                                        "hence they can carry on with their background services" +
                                        " without any restrictions. Still give it a try. In any case the call should work fine when apps on both side are active!")
                                .setPositiveButton("DISABLE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                                        startActivityForResult(intent,REQUEST_CODE_BATTERY_OPTIMIZATION);
                                    }
                                });
                        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i){
                                dialogInterface.dismiss();
                            }
                        });
                        alertDialog.create().show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_BATTERY_OPTIMIZATION){
            checkForBatteryOptimizations();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}