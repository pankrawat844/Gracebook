package com.grace.book.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.R;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.model.CommentsList;

import java.util.ArrayList;


public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = CommentsAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<CommentsList> allSearchList;
    private FilterItemCallback lFilterItemCallback;

    public CommentsAdapter(Context context, ArrayList<CommentsList> myDataset) {
        mContext = context;
        this.allSearchList = new ArrayList<CommentsList>();
        this.allSearchList.addAll(myDataset);
    }

    public void addClickListiner(FilterItemCallback lFilterItemCallback){
        this.lFilterItemCallback=lFilterItemCallback;
    }
    public CommentsList getModelAt(int index) {
        return allSearchList.get(index);
    }

    public int getDataSize() {
        return allSearchList.size();
    }

    public void addAllList(ArrayList<CommentsList> clientList) {
        allSearchList.addAll(clientList);
        notifyDataSetChanged();
    }

    public void removeAllData() {
        allSearchList.clear();
        notifyDataSetChanged();
    }

    public void addnewItem(CommentsList dataObj) {
        allSearchList.add(dataObj);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_comments, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ItemViewHolder) {
            final ItemViewHolder holder = (ItemViewHolder) viewHolder;
            CommentsList mJobList = allSearchList.get(position);
            try {

                // ConstantFunctions.loadImage(mJobList, holder.imageCon);


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
        private ImageView imageCon;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setTag(getAdapterPosition());
        }

        @Override
        public void onClick(View v) {

        }

    }

}
