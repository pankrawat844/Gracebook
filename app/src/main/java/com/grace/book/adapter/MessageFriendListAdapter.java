package com.grace.book.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.R;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.model.MesageUserList;

import java.util.ArrayList;


/**
 * Created by Prosanto on 3/7/17.
 */
public class MessageFriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = MessageFriendListAdapter.class.getSimpleName();
    private Context mContext;
    private FilterItemCallback nFilterItemCallback;
    private ArrayList<MesageUserList> mSpeciallistModel;

    public MessageFriendListAdapter(Context context, ArrayList<MesageUserList> myDataset) {
        mContext = context;
        this.mSpeciallistModel = new ArrayList<MesageUserList>();
        this.mSpeciallistModel.addAll(myDataset);

    }

    public MesageUserList getModelAt(int index) {
        return mSpeciallistModel.get(index);
    }

    public void addFilterItemCallback(FilterItemCallback nFilterItemCallback) {
        this.nFilterItemCallback = nFilterItemCallback;
    }

    public ArrayList<MesageUserList> getAllStudents() {
        return mSpeciallistModel;
    }

    public int getDataSize() {
        return mSpeciallistModel.size();
    }

    public void removeAllData() {
        mSpeciallistModel.clear();
        notifyDataSetChanged();
    }

    public void addnewItem(MesageUserList dataObj) {
        mSpeciallistModel.add(dataObj);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_messageuser, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ItemViewHolder) {
            final ItemViewHolder holder = (ItemViewHolder) viewHolder;
            MesageUserList mJobList = mSpeciallistModel.get(position);
            try {


            } catch (Exception ex) {

            }

        }
    }

    @Override
    public int getItemCount() {
        return this.mSpeciallistModel.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setTag(getPosition());
            itemView.setOnClickListener(this); // bind the listener
        }
        @Override
        public void onClick(View v) {
            nFilterItemCallback.ClickFilterItemCallback(0, getPosition());

        }
    }


}

