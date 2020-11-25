package com.example.chatterboi.afterauthenticated;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterboi.R;
import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUnits;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class Custom_post_adapter extends RecyclerView.Adapter<Custom_post_adapter.myAdapter> {

    List<PostModel> list;
    Context context;

    public Custom_post_adapter(List<PostModel> list, Context context) {
        this.list = list;
        this.context = context;
        
    }

    @NonNull
    @Override
    public Custom_post_adapter.myAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_layout,
                        parent,
                        false);
        return new myAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Custom_post_adapter.myAdapter holder, int position) {
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

        public void bind(PostModel postModel) {

            manageLikes(postModel.docId);
            TextView name = itemView.findViewById(R.id.textView4);
            TextView date = itemView.findViewById(R.id.textView6);
            TextView caption = itemView.findViewById(R.id.textView5);
            CircularImageView profileImage= itemView.findViewById(R.id.circularImageView);
            ImageView postImage = itemView.findViewById(R.id.post_image);

            Uri profileUri = Uri.parse(postModel.getProfileUri());
            Uri postUri = Uri.parse(postModel.getPostUri());

            String postTime = postModel.getTime();
            date.setText(postTime);

            String profileName = postModel.getDisplayName();
            String postCaption = postModel.getPostText();

            String CaptionWithName = "<b>" + profileName + "</b>" + "&ensp;" + postCaption;

            caption.setText(Html.fromHtml(CaptionWithName));
            name.setText(profileName);

            Picasso.get().load(profileUri).into(profileImage);
            Picasso.get().load(postUri).into(postImage);

        }

        private void manageLikes(String docId) {
            Boolean isLiked = false;

        }
    }
}
