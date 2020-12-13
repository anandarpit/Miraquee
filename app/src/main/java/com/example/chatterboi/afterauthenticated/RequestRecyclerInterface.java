package com.example.chatterboi.afterauthenticated;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterboi.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestRecyclerInterface extends RecyclerView.Adapter<RequestRecyclerInterface.myAdapter> {

    List<Requestmodel> list;
    Context context;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String mUid;

    StorageReference storageReference;

    public RequestRecyclerInterface(List<Requestmodel> list, Context context) {
        this.list = list;
        this.context = context;
        db = FirebaseFirestore.getInstance();
        mAuth =  FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public RequestRecyclerInterface.myAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_search_element,
                        parent,
                        false);
        return new RequestRecyclerInterface.myAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestRecyclerInterface.myAdapter holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myAdapter extends RecyclerView.ViewHolder {

        TextView name, username;
        CircularImageView image;
        Button addContact;
        Long time;

        public myAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Requestmodel requestmodel) {
            name =itemView.findViewById(R.id.searchname);
            username = itemView.findViewById(R.id.searchusername);
            image = itemView.findViewById(R.id.searchChatCiv);
            addContact = itemView.findViewById(R.id.addContact);
            addContact.setBackgroundResource(R.drawable.ic_doble_tick);
            name.setText(requestmodel.getName());
            username.setText("@"+requestmodel.getUsername());

            time = System.currentTimeMillis();

            addContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(requestmodel.getSorR().equals("R")) {
                        addContact(requestmodel);
                    }
                    if(requestmodel.getSorR().equals("S")){
                        removeRequest(requestmodel);
                    }
                }
            });


            StorageReference profoleRef = storageReference.child("Profile Photos").child(requestmodel.getUid());
            profoleRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("Check", "Uri has been received");
                    Picasso.get().load(uri).into(image);
                }
            });
        }

        private void removeRequest(Requestmodel requestmodel) {
            db.collection("All Users").document(requestmodel.getUid()).collection("Contacts")
                    .document(mUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String SorR = documentSnapshot.getString("SentOrRecieved");
                    if(SorR.equals("R")){
                        db.collection("All Users").document(requestmodel.getUid()).collection("Contacts")
                                .document(mUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                removeFromMyContactList(requestmodel);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        private void removeFromMyContactList(Requestmodel requestmodel) {
            db.collection("All Users").document(mUid).collection("Contacts")
                    .document(requestmodel.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    addContact.setEnabled(false);
                    addContact.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addContact.setEnabled(true);
                        }
                    }, 500);
                    addContact.setBackgroundResource(R.drawable.ic_person_add);
                    Toast.makeText(context, "Request Deleted", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void addContact(Requestmodel requestmodel) {
            DocumentReference documentReference = db.collection("All Users").document(mUid).collection("Contacts")
                    .document(requestmodel.getUid());
            Map<String, Object> request = new HashMap<>();
            request.put("Status",true);
            request.put("timeOfFriendship",time);
            documentReference.update(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    addContactToOpponent(requestmodel);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Contact Adding Failed :(", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void addContactToOpponent(Requestmodel requestmodel) {
            DocumentReference documentReference = db.collection("All Users").document(requestmodel.getUid()).collection("Contacts")
                    .document(mUid);
            Map<String, Object> request = new HashMap<>();
            request.put("Status",true);
            request.put("timeOfFriendship",time);
            documentReference.update(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    addContact.setBackgroundResource(R.drawable.ic_friends);
                    Toast.makeText(context, "Contact Has been added to your Chat!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Contact Adding Failed :(", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
