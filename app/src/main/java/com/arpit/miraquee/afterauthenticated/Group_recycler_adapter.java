package com.arpit.miraquee.afterauthenticated;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.arpit.miraquee.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Group_recycler_adapter extends RecyclerView.Adapter<Group_recycler_adapter.myAdapter> {

    List<GroupChatList> list;
    Context context;
    int i =0;
    StorageReference storageReference;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public Group_recycler_adapter(List<GroupChatList> list, Context context) {
        this.list = list;
        this.context = context;
        storageReference = FirebaseStorage.getInstance().getReference();

        mAuth =  FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public Group_recycler_adapter.myAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.arpit_groups_recycler,
                parent,
                false);
        Log.d("Bind","Bind view holder created: " + ++i);
        return new myAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Group_recycler_adapter.myAdapter holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myAdapter extends RecyclerView.ViewHolder{

        public myAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(final GroupChatList groupChatList) {
            TextView name = itemView.findViewById(R.id.groupname);
            ConstraintLayout cardView = itemView.findViewById(R.id.cardView);
            final CircularImageView imageView = itemView.findViewById(R.id.civ);
            TextView admin = itemView.findViewById(R.id.username);

            name.setText(groupChatList.getGroupname());

            admin.setText(" @"+ groupChatList.getUsername());

            StorageReference profoleRef = storageReference.child("Groups Photo").child(groupChatList.getGroupname());

            profoleRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("Check", "Uri has been received");
                    Picasso.get().load(uri).into(imageView);
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent intent = new Intent(context, GroupChatInterface.class);
                            intent.putExtra("GroupID", groupChatList.getId());
                            intent.putExtra("GroupName", groupChatList.getGroupname());
                            intent.putExtra("Time", groupChatList.getTime());
                            context.startActivity(intent);

//                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context,
//                            R.style.BottomSheetDialogTheme);
//                    View bottomSheetView = LayoutInflater.from(context).inflate(
//                            R.layout.group_preview_dialog, (ConstraintLayout)itemView.findViewById(R.id.constraint_layout));
//                    bottomSheetDialog.setContentView(bottomSheetView);
//
//                    //Todo: Check here by if and else if the blue button should be "Join" or "Send Join Request"
//
//                    TextView group =  bottomSheetDialog.findViewById(R.id.groupname);
//                    group.setText(groupChatList.getGroupname());
//
//                    bottomSheetDialog.show();
//
//                    bottomSheetDialog.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = new Intent(context,GroupChatInterface.class);
//                            intent.putExtra("GroupID",groupChatList.getId());
//                            intent.putExtra("GroupName",groupChatList.getGroupname());
//                            intent.putExtra("Time", groupChatList.getTime());
//                            context.startActivity(intent);
//                        }
//                    });
//                    bottomSheetDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            bottomSheetDialog.dismiss();
//                        }
//                    });
                }
            });
        }
    }
}