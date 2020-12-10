package com.example.chatterboi.afterauthenticated;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatterboi.R;
import com.example.chatterboi.SharedPreferences.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddPeople extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    RecyclerView recyclerView;
    EditText search;
    List<SearchModel> list;
    String searchTerm;
    TextView totalsearchResults;
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);

        if (Build.VERSION.SDK_INT >= 21) { //To change the color of the status bar
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.AddPostStatusBar));
        }

        totalsearchResults = findViewById(R.id.noOfItemsFound);
        mAuth =  FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        search = findViewById(R.id.searchPeople);
        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        preferences = new Preferences(getApplicationContext());
        list = new ArrayList<>();

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Toast.makeText(AddPeople.this, "Search Pressed", Toast.LENGTH_SHORT).show();

                    searchTerm = search.getText().toString();

                    search.clearFocus();
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(search.getWindowToken(), 0);

                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch() {
        db.collection("All Users").whereGreaterThanOrEqualTo("username",searchTerm)
                .whereNotEqualTo("username",preferences.getData("username"))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                list.clear();
                for(QueryDocumentSnapshot snap: value){
                    list.add(new SearchModel(snap.getString("Name"),snap.getString("username"),snap.getId()));
                }
                totalsearchResults.setText(list.size() +" people found ");
                recyclerView.setAdapter(new Custom_search_adapter(list,getApplicationContext()));
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_in_top);
    }
}