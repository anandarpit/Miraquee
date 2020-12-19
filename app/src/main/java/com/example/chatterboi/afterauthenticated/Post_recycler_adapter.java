package com.example.chatterboi.afterauthenticated;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterboi.SharedPreferences.Preferences;
import com.example.chatterboi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
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
        ImageView clickTolike;
        TextView likes;
        public myAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(final PostModel postModel) {

            TextView name = itemView.findViewById(R.id.textView4);
            TextView dateee = itemView.findViewById(R.id.textView6);
            TextView caption = itemView.findViewById(R.id.textView5);
            clickTolike = itemView.findViewById(R.id.clickToLike);
            CircularImageView profileImage= itemView.findViewById(R.id.circularImageView);
            ImageView postImage = itemView.findViewById(R.id.post_image);
            likes = itemView.findViewById(R.id.likes);

            TextView noOfComments = itemView.findViewById(R.id.noOfComments);

            final ImageView comment = itemView.findViewById(R.id.comment);
             comment.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                    Intent comment_intent = new Intent(context,comments.class);
                    comment_intent.putExtra("postId",postModel.getDocId());
                    Bundle bundle = ActivityOptions.makeCustomAnimation(context,  R.anim.slide_in_up, R.anim.slide_out_up).toBundle();
                    context.startActivity(comment_intent , bundle);
                 }
             });
            Uri profileUri = Uri.parse(postModel.getProfileUri());
            Uri postUri = Uri.parse(postModel.getPostUri());

            db.collection("All Posts").document(postModel.getDocId()).collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    int i = 0;
                    for (QueryDocumentSnapshot snap : value){
                        i++;
                    }
                    noOfComments.setText(Integer.toString(i));
                }
            });

            String wordMonth = null;
            String postTime = postModel.getTime();

            String date = convertDate(postTime,"dd");
            String month = convertDate(postTime,"MM");

            if(month.equals("01")){
                wordMonth = "Jan";
            }
            if(month.equals("02")){
                wordMonth = "Feb";
            }
            if(month.equals("03")){
                wordMonth = "March";
            }
            if(month.equals("04")){
                wordMonth = "April";
            }
            if(month.equals("05")){
                wordMonth = "May";
            }
            if(month.equals("06")){
                wordMonth = "June";
            }
            if(month.equals("07")){
                wordMonth = "July";
            }
            if(month.equals("08")){
                wordMonth = "Aug";
            }
            if(month.equals("09")){
                wordMonth = "Sep";
            }
            if(month.equals("10")){
                wordMonth = "Oct";
            }
            if(month.equals("11")){
                wordMonth = "Nov";
            }
            if(month.equals("12")){
                wordMonth = "Dec";
            }

            String time = convertDate(postTime,"hh:mm");

            dateee.setText(wordMonth +" " + date + ", " + time +" "+convertDate(postTime,"a") );

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

            pref.removeData(postModel.docId);

            Picasso.get().load(postUri).into(postImage);

            clickTolike.setImageResource(R.drawable.ic_no_fire);

            getTotalLikes(postModel);

        }

        private void getTotalLikes(PostModel postModel) {
            final List<LikeModel> list = new ArrayList<>();
            db.collection("All Posts").document(postModel.getDocId()).collection("Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    list.clear();

                    for(QueryDocumentSnapshot snap: value){
                        list.add(new LikeModel(snap.getString("likerId")));
                        if(snap.getString("likerId").equals(uid)){
                            clickTolike.setImageResource(R.drawable.ic_lit_fire);
                        }
                    }

                    likes.setText(list.size() + " flame");
                    clickTolike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int flag = 0;
                            for(LikeModel likerId: list){
                                if(likerId.getLikerId().equals(uid)){
                                    removeLike(postModel);
                                    flag = 1;
                                }
                            }
                            if(flag == 0){
                                addLike(postModel);
                            }
                        }
                    });
                }
            });

        }

        private void removeLike(PostModel postModel) {

            db.collection("All Posts").document(postModel.getDocId()).collection("Likes")
                    .document(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    clickTolike.setImageResource(R.drawable.ic_no_fire);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void addLike(PostModel postModel) {
            Map<String, Object> pp = new HashMap<>();
            pp.put("likerId", uid);
            db.collection("All Posts")
                    .document(postModel.getDocId())
                    .collection("Likes")
                    .document(uid)
                    .set(pp).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    clickTolike.setImageResource(R.drawable.ic_lit_fire);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        public String convertDate(String dateInMilliseconds,String dateFormat) {
            return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
        }
    }
}