package com.arpit.miraquee.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    Context context;

    public Preferences(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        editor = sp.edit();
    }
    public void setData(String Key, String Value)
    {
        editor.putString(Key, Value);
        editor.commit();
    }
    public String getData(String Key)
    {
        return sp.getString(Key,"");// "" is the default value in case the string is not retrieved
    }
    public void clearData()
    {
        editor.clear();
        editor.commit();
    }
    public void removeData(String Key)
    {
        editor.remove(Key);
        editor.commit();
    }
}
