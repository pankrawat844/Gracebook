package com.grace.book.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grace.book.R;
import com.grace.book.model.Usersdata;

import java.util.ArrayList;
import java.util.Vector;

public class ItemlistAdapter extends ArrayAdapter<Usersdata> {
    Context context;
    public ArrayList<Usersdata> items = new ArrayList<>();

    public ItemlistAdapter(Context context, ArrayList<Usersdata> allIddescriptions) {
        super(context, R.layout.item_person, R.id.textview_person);
        this.context = context;
        items = allIddescriptions;
    }

    public Usersdata getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        MyViewHolder mViewHolder = null;
        if (convertView == null) {

            mViewHolder = new MyViewHolder();
            LayoutInflater li = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.item_person, parent, false);

//            mViewHolder.userProfileImage = (ImageView) convertView.findViewById(R.id.userProfileImage);
//            mViewHolder.userProfileNmae = (TextView) convertView.findViewById(R.id.userProfileNmae);
            mViewHolder.username = (TextView) convertView.findViewById(R.id.textview_person);
//            mViewHolder.userNumber = (TextView) convertView.findViewById(R.id.userNumber);
//            mViewHolder.userLocation = (TextView) convertView.findViewById(R.id.userLocation);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Usersdata mJobList = getItem(position);

//        mViewHolder.userProfileNmae.setText(mJobList.getFname() + " " + mJobList.getLname());
        // ConstantFunctions.loadImageForCircel(mJobList.getProfile_pic(), holder.userProfileImage);
        mViewHolder.username.setText(mJobList.getName());
//        mViewHolder.userNumber.setText("Member of " + mJobList.getChurch() + " church");


        return convertView;
    }

    private class MyViewHolder {
        private ImageView userProfileImage;
        private TextView userProfileNmae;
        private TextView username;
        private TextView userNumber;
        private TextView userLocation;
    }
}

