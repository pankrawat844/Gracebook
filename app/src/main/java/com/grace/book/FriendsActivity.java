package com.grace.book;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.grace.book.fragment.FriendRequestFragment;
import com.grace.book.fragment.FriendsFragment;
import com.grace.book.myapplication.Myapplication;

public class FriendsActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_profile, frameLayout);
        Myapplication.selection = 2;
        selectedDeselectedLayut();
        intiUi();

    }

    private void intiUi() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Friend Requests"));
        tabLayout.addTab(tabLayout.newTab().setText("Friends"));

       // tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        final MyAdapter adapter = new MyAdapter(this, getSupportFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public class MyAdapter extends FragmentPagerAdapter {
        Context context;
        int totalTabs;

        public MyAdapter(Context c, FragmentManager fm, int totalTabs) {
            super(fm);
            context = c;
            this.totalTabs = totalTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    FriendRequestFragment footballFragment = new FriendRequestFragment();
                    return footballFragment;
                case 1:
                    FriendsFragment cricketFragment = new FriendsFragment();
                    return cricketFragment;


                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return totalTabs;
        }
    }


}
