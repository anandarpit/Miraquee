package com.example.chatterboi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class popupLogout {

    Context context;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    public popupLogout(Context context) {
        this.context = context;
    }
    public void showPopupDialog(){
        builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.popup_logout,null);

        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }
}
