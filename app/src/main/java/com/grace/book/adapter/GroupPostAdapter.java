package com.grace.book.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.R;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.model.FeedList;

import java.util.ArrayList;


public class GroupPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = GroupPostAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<FeedList> allSearchList;
    private FilterItemCallback lFilterItemCallback;

    public GroupPostAdapter(Context context, ArrayList<FeedList> myDataset) {
        mContext = context;
        this.allSearchList = new ArrayList<FeedList>();
        this.allSearchList.addAll(myDataset);
    }

    public void addClickListiner(FilterItemCallback lFilterItemCallback){
        this.lFilterItemCallback=lFilterItemCallback;
    }
    public FeedList getModelAt(int index) {
        return allSearchList.get(index);
    }

    public int getDataSize() {
        return allSearchList.size();
    }

    public void addAllList(ArrayList<FeedList> clientList) {
        allSearchList.addAll(clientList);
        notifyDataSetChanged();
    }

    public void removeAllData() {
        allSearchList.clear();
        notifyDataSetChanged();
    }

    public void addnewItem(FeedList dataObj) {
        allSearchList.add(dataObj);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ItemViewHolder) {
            final ItemViewHolder holder = (ItemViewHolder) viewHolder;
            FeedList mJobList = allSearchList.get(position);
            try {


                holder.userView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lFilterItemCallback.ClickFilterItemCallback(0,position);
                    }
                });
                holder.layoutchat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lFilterItemCallback.ClickFilterItemCallback(1,position);
                    }
                });


            } catch (Exception ex) {

            }

        }
    }

    @Override
    public int getItemCount() {
        return this.allSearchList.size();
    }


    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout layoutchat;
        private LinearLayout userView;
        private LinearLayout shareView;
        private LinearLayout likeView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            userView = (LinearLayout) itemView.findViewById(R.id.userView);
            likeView = (LinearLayout) itemView.findViewById(R.id.likeView);
            layoutchat = (LinearLayout) itemView.findViewById(R.id.layoutchat);
            shareView = (LinearLayout) itemView.findViewById(R.id.shareView);

            itemView.setOnClickListener(this);
            itemView.setTag(getAdapterPosition());
        }

        @Override
        public void onClick(View v) {

        }

    }

}

