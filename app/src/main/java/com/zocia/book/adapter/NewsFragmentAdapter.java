package com.zocia.book.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class NewsFragmentAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragmentList = new ArrayList<>();


    public NewsFragmentAdapter(FragmentManager supportFragmentManager, List<Fragment> fragmentList) {
        super(supportFragmentManager, BEHAVIOR_SET_USER_VISIBLE_HINT);
//        super(fragment);
        this.fragmentList = fragmentList;
    }

//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        return fragmentList.get(position);
//    }
//
//    @Override
//    public int getItemCount() {
//        return fragmentList.size();
//    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
