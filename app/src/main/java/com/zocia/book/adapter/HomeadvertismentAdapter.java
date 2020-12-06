package com.zocia.book.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.zocia.book.fragment.HomeadverstismentFragment;
import com.zocia.book.model.BannerList;

import java.util.ArrayList;

public class HomeadvertismentAdapter extends FragmentStatePagerAdapter {
    public static ArrayList<BannerList> advertisementListArrayList = new ArrayList<>();
    public HomeadvertismentAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public int getCount() {
        return advertisementListArrayList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return HomeadverstismentFragment.newInstance(position);
    }
    public void addAdvertisementList(ArrayList<BannerList> _advertisementListArrayList) {
        advertisementListArrayList.clear();
        advertisementListArrayList.addAll(_advertisementListArrayList);
        notifyDataSetChanged();
    }
    public void removeDataArray(){
        advertisementListArrayList.clear();
        notifyDataSetChanged();

    }
}
