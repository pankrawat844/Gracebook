package com.grace.book.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.R;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.model.BelieverList;

import java.util.ArrayList;


public class SearchbelieverListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = SearchbelieverListAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<BelieverList> allSearchList;
    private FilterItemCallback lFilterItemCallback;
    public SearchbelieverListAdapter(Context context, ArrayList<BelieverList> myDataset) {
        mContext = context;
        this.allSearchList = new ArrayList<BelieverList>();
        this.allSearchList.addAll(myDataset);
    }
    public void addFilterItemCallback(FilterItemCallback _lFilterItemCallback){
        lFilterItemCallback=_lFilterItemCallback;
    }
    public BelieverList getModelAt(int index) {
        return allSearchList.get(index);
    }

    public int getDataSize() {
        return allSearchList.size();
    }

    public void addAllList(ArrayList<BelieverList> clientList) {
        allSearchList.addAll(clientList);
        notifyDataSetChanged();
    }

    public void removeAllData() {
        allSearchList.clear();
        notifyDataSetChanged();
    }

    public void addnewItem(BelieverList dataObj) {
        allSearchList.add(dataObj);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_search_believer, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ItemViewHolder) {
            final ItemViewHolder holder = (ItemViewHolder) viewHolder;
            BelieverList mJobList = allSearchList.get(position);
            try {


                holder.layoutProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lFilterItemCallback.ClickFilterItemCallback(0,position);
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
        private ImageView imageCon;
        private LinearLayout layoutProfile;
        private LinearLayout AddBeliever;

        public ItemViewHolder(View itemView) {
            super(itemView);
            layoutProfile=(LinearLayout)itemView.findViewById(R.id.layoutProfile);
            AddBeliever=(LinearLayout)itemView.findViewById(R.id.AddBeliever);

            itemView.setOnClickListener(this);
            itemView.setTag(getAdapterPosition());
        }

        @Override
        public void onClick(View v) {

        }

    }

}

