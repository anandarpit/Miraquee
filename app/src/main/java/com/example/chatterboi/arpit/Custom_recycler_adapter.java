package com.example.chatterboi.arpit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterboi.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class Custom_recycler_adapter extends RecyclerView.Adapter<Custom_recycler_adapter.myAdapter> {

    List<ChatLists> list;
    Context context;
    int i =0;

    public Custom_recycler_adapter(List<ChatLists> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Custom_recycler_adapter.myAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.arpit_groups_recycler,
                parent,
                false
        );
        Log.d("Bind","Bind view holder created: " + ++i);

        return new myAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Custom_recycler_adapter.myAdapter holder, int position) {
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

        public void bind(final ChatLists chatLists) {
            TextView name = itemView.findViewById(R.id.groupname);
            CardView cardView = itemView.findViewById(R.id.cardView);
            name.setText(chatLists.getGroupname());
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context,
                            R.style.BottomSheetDialogTheme);
                    View bottomSheetView = LayoutInflater.from(context).inflate(
                            R.layout.group_preview_dialog, (ConstraintLayout)itemView.findViewById(R.id.constraint_layout));
                    bottomSheetDialog.setContentView(bottomSheetView);

                    //Todo: Check here by if and else if the blue button should be "Join" or "Send Join Request"



                    TextView group =  bottomSheetDialog.findViewById(R.id.groupname);
                    group.setText(chatLists.getGroupname());

                    bottomSheetDialog.show();

                    bottomSheetDialog.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context,ChatInterface.class);
                            intent.putExtra("GroupID",chatLists.getId());
                            intent.putExtra("GroupName",chatLists.getGroupname());
                            intent.putExtra("Time", chatLists.getTime());
                            context.startActivity(intent);
                        }
                    });


                }
            });
        }
    }
}
