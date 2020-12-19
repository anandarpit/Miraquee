package com.arpit.miraquee.afterauthenticated;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class CallsRecyclerAdapter extends RecyclerView.Adapter<CallsRecyclerAdapter.myAdapter> {
    List<CallsModel> list;
    Context context;
    StorageReference storageReference;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String uid;
    
    public CallsRecyclerAdapter(List<CallsModel> allList, Context context) {
        list = allList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth =  FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public CallsRecyclerAdapter.myAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calls_element,
                        parent,
                        false);
        return new CallsRecyclerAdapter.myAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallsRecyclerAdapter.myAdapter holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myAdapter extends RecyclerView.ViewHolder {

        CircularImageView civ;
        ConstraintLayout constraintLayout;
        TextView name, username;
        ImageView callsDirection;

        public myAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(CallsModel callsModel) {
            civ = itemView.findViewById(R.id.CallsCiv);
            name = itemView.findViewById(R.id.contactname);
            username = itemView.findViewById(R.id.username);
            callsDirection = itemView.findViewById(R.id.callsdirection);
            constraintLayout = itemView.findViewById(R.id.constraint);


            storageReference.child("Profile Photos").child(callsModel.getUid())
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(civ);
                }
            });
            name.setText(callsModel.getName());
            username.setText("@"+callsModel.getUsername());
            if(callsModel.getSorR().equals("S")){
                callsDirection.setImageResource(R.drawable.ic_call_made);
            }
            if(callsModel.getSorR().equals("R")){
                callsDirection.setImageResource(R.drawable.ic_call_received);
            }

            String wordMonth = null;
            String postTime = Long.toString( callsModel.getTime());

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

            String time = convertDate(postTime,"hh:mm:ss");
            String display = wordMonth +" " + date + ", " + time +" "+convertDate(postTime,"a");

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog
                            .setTitle("Called On")
                            .setMessage(display)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                  dialogInterface.dismiss();
                                }
                            });
                    alertDialog.create().show();
                }
            });
        }
        public String convertDate(String dateInMilliseconds,String dateFormat) {
            return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
        }
    }
}
