package com.example.chatterboi.arpit;

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
                Explore exp = new Explore();
                return exp;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {       // remember to change this value acc to oyur need
        return 3;
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
                return "My";   // Explore has been changed to My
            default:
                return null;
        }
    }
}
