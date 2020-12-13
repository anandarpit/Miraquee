package com.example.chatterboi.afterauthenticated;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterboi.SharedPreferences.Preferences;
import com.example.chatterboi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post_recycler_adapter extends RecyclerView.Adapter<Post_recycler_adapter.myAdapter> {

    List<PostModel> list;
    Context context;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String uid;
    Preferences pref;
    StorageReference storageReference;
    public Post_recycler_adapter(List<PostModel> list, Context context) {
        this.list = list;
        this.context = context;
        pref = new Preferences(context);
        mAuth =  FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public Post_recycler_adapter.myAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_layout,
                        parent,
                        false);
        return new myAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Post_recycler_adapter.myAdapter holder, int position) {
        holder.bind(list.get(position));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class myAdapter extends RecyclerView.ViewHolder {
        public myAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(final PostModel postModel) {

            TextView name = itemView.findViewById(R.id.textView4);
            TextView date = itemView.findViewById(R.id.textView6);
            TextView caption = itemView.findViewById(R.id.textView5);
            final ImageView clickTolike = itemView.findViewById(R.id.clickToLike);
            CircularImageView profileImage= itemView.findViewById(R.id.circularImageView);
            ImageView postImage = itemView.findViewById(R.id.post_image);
            final TextView likes = itemView.findViewById(R.id.likes);

            final ImageView comment = itemView.findViewById(R.id.comment);
             comment.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                    Intent comment_intent = new Intent(context,comments.class);
                    comment_intent.putExtra("POST_KEY",postModel.getDocId());
                    context.startActivity(comment_intent);


                 }
             });
            Uri profileUri = Uri.parse(postModel.getProfileUri());
            Uri postUri = Uri.parse(postModel.getPostUri());

            String postTime = postModel.getTime();
            date.setText(postTime);

            String profileName = postModel.getDisplayName();
            String postCaption = postModel.getPostText();

            String CaptionWithName = "<b>" + profileName + "</b>" + "&ensp;" + postCaption;

            caption.setText(Html.fromHtml(CaptionWithName));
            name.setText(profileName);

            StorageReference profoleRef = storageReference.child("Profile Photos").child(postModel.getUid());
            profoleRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("Check", "Uri has been received");
                    Picasso.get().load(uri).into(profileImage);
                }
            });

            Picasso.get().load(postUri).into(postImage);

            if(pref.getData(postModel.docId) == "Y"){
                clickTolike.setImageDrawable(null);
                clickTolike.setBackgroundResource(R.drawable.ic_lit_fire);
            }

            final List<LikeModel> list = new ArrayList<>();
            db.collection("All Posts").document(postModel.getDocId()).collection("Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    list.clear();
                    int flag = 0;
                    String culpritId = "";
                    for(QueryDocumentSnapshot snap: value){
                        list.add(new LikeModel(snap.getString("likerId")));
                        if(snap.getString("likerId") == uid){
                            flag++;
                            culpritId = snap.getId();
                            pref.setData(postModel.docId,"Y");
                        }
                    }

                    likes.setText(list.size() + " flame");
                    final int finalFlag = flag;
                    final String finalCulpritId = culpritId;
                    clickTolike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(finalFlag == 0) {
                                Map<String, Object> pp = new HashMap<>();
                                pp.put("likerId", uid);
                                db.collection("All Posts").document(postModel.getDocId()).collection("Likes")
                                        .add(pp)
                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        clickTolike.setImageDrawable(null);
                                        clickTolike.setBackgroundResource(R.drawable.ic_lit_fire);
                                    }
                                });
                            }
                            if(finalFlag == 1){
                                Map<String, Object> pp = new HashMap<>();
                                pp.put("likerId", uid);
                                db.collection("All Posts").document(postModel.getDocId()).collection("Likes").document(finalCulpritId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        clickTolike.setImageDrawable(null);
                                        clickTolike.setBackgroundResource(R.drawable.ic_no_fire);
                                        pref.setData(postModel.docId,"N");
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }
}