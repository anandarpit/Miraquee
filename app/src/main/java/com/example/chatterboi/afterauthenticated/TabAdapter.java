package com.example.chatterboi.afterauthenticated;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {


    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0:
                Posts post = new Posts();
                return post;
            case 1:
                Groups group = new Groups();
                return group;
            case 2:
                Chats exp = new Chats();
                return exp;
            case 3:
                Calls call = new Calls();
                return call;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {       // remember to change this value acc to oyur need
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Posts";
            case 1:
                return "Groups";
            case 2:
                return "Chats";   // Chats has been changed to Chats
            case 3:
                return "Calls";
            default:
                return null;
        }
    }
}
