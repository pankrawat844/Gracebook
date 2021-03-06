package com.zocia.book.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zocia.book.R;
import com.zocia.book.callbackinterface.FilterItemCallback;
import com.zocia.book.model.FriendList;
import com.zocia.book.model.Usersdata;
import com.zocia.book.utils.ConstantFunctions;

import java.util.ArrayList;


public class GroupAddfreindListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = GroupAddfreindListAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<FriendList> allSearchList;
    private FilterItemCallback lFilterItemCallback;

    public GroupAddfreindListAdapter(Context context, ArrayList<FriendList> myDataset) {
        mContext = context;
        this.allSearchList = new ArrayList<FriendList>();
        this.allSearchList.addAll(myDataset);
    }

    public void addFilterItemCallback(FilterItemCallback _lFilterItemCallback) {
        lFilterItemCallback = _lFilterItemCallback;
    }

    public FriendList getModelAt(int index) {
        return allSearchList.get(index);
    }

    public void deletePostion(int index) {
        allSearchList.remove(index);
        notifyDataSetChanged();
    }

    public int getDataSize() {
        return allSearchList.size();
    }

    public void addAllList(ArrayList<FriendList> clientList) {
        allSearchList.addAll(clientList);
        notifyDataSetChanged();
    }

    public void removeAllData() {
        allSearchList.clear();
        notifyDataSetChanged();
    }

    public void addnewItem(FriendList dataObj) {
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
            Usersdata mJobList = allSearchList.get(position).getmUsersdata();
            try {

                holder.AddBeliever.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lFilterItemCallback.ClickFilterItemCallback(1, position);
                    }
                });
                ConstantFunctions.loadImageForCircel(mJobList.getProfile_pic(), holder.userImage);
                holder.username.setText(mJobList.getFname() + " " + mJobList.getLname());
                holder.userNumber.setText("Member of " + mJobList.getChurch()+" church");
                holder.userLocation.setText(mJobList.getCity() + ", " + mJobList.getCountry());



            } catch (Exception ex) {
                Log.e("Exception", ex.getMessage());

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
        private LinearLayout AddBeliever;
        private TextView username;
        private TextView userLocation,userNumber;
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

