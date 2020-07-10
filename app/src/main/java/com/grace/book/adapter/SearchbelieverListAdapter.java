package com.grace.book.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.R;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.model.Usersdata;
import com.grace.book.utils.ConstantFunctions;

import java.util.ArrayList;


public class SearchbelieverListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = SearchbelieverListAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<Usersdata> allSearchList;
    private FilterItemCallback lFilterItemCallback;

    public SearchbelieverListAdapter(Context context, ArrayList<Usersdata> myDataset) {
        mContext = context;
        this.allSearchList = new ArrayList<Usersdata>();
        this.allSearchList.addAll(myDataset);
    }

    public void addFilterItemCallback(FilterItemCallback _lFilterItemCallback) {
        lFilterItemCallback = _lFilterItemCallback;
    }

    public Usersdata getModelAt(int index) {
        return allSearchList.get(index);
    }
    public void  deletePostion(int index) {
        allSearchList.remove(index);
         notifyDataSetChanged();
    }

    public int getDataSize() {
        return allSearchList.size();
    }

    public void addAllList(ArrayList<Usersdata> clientList) {
        allSearchList.addAll(clientList);
        notifyDataSetChanged();
    }

    public void removeAllData() {
        allSearchList.clear();
        notifyDataSetChanged();
    }

    public void addnewItem(Usersdata dataObj) {
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
            Usersdata mJobList = allSearchList.get(position);
            try {

                holder.AddBeliever.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lFilterItemCallback.ClickFilterItemCallback(1, position);
                    }
                });
                ConstantFunctions.loadImageForCircel(mJobList.getProfile_pic(), holder.userImage);
                holder.username.setText(mJobList.getFname() + " " + mJobList.getLname());
                holder.userNumber.setText("Member of " + mJobList.getChurch());
                holder.userLocation.setText(mJobList.getCity() + ", " + mJobList.getCountry());



            } catch (Exception ex) {
                Log.e("Exception",ex.getMessage());

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
        private ImageView userImage;
        private LinearLayout layoutProfile;
        private LinearLayout AddBeliever;
        private TextView username,userNumber,userLocation;

        public ItemViewHolder(View itemView) {
            super(itemView);
            AddBeliever = (LinearLayout) itemView.findViewById(R.id.AddBeliever);
            userImage = (ImageView) itemView.findViewById(R.id.userImage);

            username = (TextView) itemView.findViewById(R.id.username);
            userNumber = (TextView) itemView.findViewById(R.id.userNumber);
            userLocation = (TextView) itemView.findViewById(R.id.userLocation);

            itemView.setOnClickListener(this);
            itemView.setTag(getAdapterPosition());
        }

        @Override
        public void onClick(View v) {
            lFilterItemCallback.ClickFilterItemCallback(0, getAdapterPosition());

        }

    }

}

