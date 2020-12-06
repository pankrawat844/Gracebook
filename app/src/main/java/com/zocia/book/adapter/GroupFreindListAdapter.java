package com.zocia.book.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zocia.book.R;
import com.zocia.book.callbackinterface.FilterItemCallback;
import com.zocia.book.model.GroupFriendList;
import com.zocia.book.model.GroupList;
import com.zocia.book.model.Usersdata;
import com.zocia.book.utils.ConstantFunctions;

import java.util.ArrayList;


public class GroupFreindListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = GroupFreindListAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<GroupFriendList> allSearchList;
    private FilterItemCallback lFilterItemCallback;
    private GroupList mGroupList;

    public GroupFreindListAdapter(Context context, ArrayList<GroupFriendList> myDataset) {
        mContext = context;
        this.allSearchList = new ArrayList<GroupFriendList>();
        this.allSearchList.addAll(myDataset);
    }

    public void addFilterItemCallback(FilterItemCallback _lFilterItemCallback) {
        lFilterItemCallback = _lFilterItemCallback;
    }

    public GroupFriendList getModelAt(int index) {
        return allSearchList.get(index);
    }

    public void deletePostion(int index) {
        allSearchList.remove(index);
        notifyDataSetChanged();
    }

    public int getDataSize() {
        return allSearchList.size();
    }

    public void addAllList(ArrayList<GroupFriendList> clientList) {
        allSearchList.addAll(clientList);
        notifyDataSetChanged();
    }
    public void addGroupList(GroupList mGroupList) {
        this.mGroupList=mGroupList;
        notifyDataSetChanged();
    }

    public void removeAllData() {
        allSearchList.clear();
        notifyDataSetChanged();
    }

    public void addnewItem(GroupFriendList dataObj) {
        allSearchList.add(dataObj);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_group_friend, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ItemViewHolder) {
            final ItemViewHolder holder = (ItemViewHolder) viewHolder;
            Usersdata mJobList = allSearchList.get(position).getmUsersdata();
            try {

                ConstantFunctions.loadImageForCircel(mJobList.getProfile_pic(), holder.userImage);
                holder.username.setText(mJobList.getFname() + " " + mJobList.getLname());
                holder.username.setText(mJobList.getFname() + " " + mJobList.getLname());
                holder.userNumber.setText("Member of " + mJobList.getChurch()+" church");
                holder.userLocation.setText(mJobList.getCity() + ", " + mJobList.getCountry());
                holder.group_admin.setVisibility(View.GONE);
                if(mGroupList.getGroup_owner().equalsIgnoreCase(mJobList.getId())){
                    holder.group_admin.setVisibility(View.VISIBLE);

                }

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
        private TextView username,group_admin,userNumber,userLocation;

        public ItemViewHolder(View itemView) {
            super(itemView);
            userImage = (ImageView) itemView.findViewById(R.id.userImage);
            username = (TextView) itemView.findViewById(R.id.username);
            userLocation = (TextView) itemView.findViewById(R.id.userLocation);
            group_admin = (TextView) itemView.findViewById(R.id.group_admin);
            userNumber = (TextView) itemView.findViewById(R.id.userNumber);


            itemView.setOnClickListener(this);
            itemView.setTag(getAdapterPosition());
        }

        @Override
        public void onClick(View v) {
            lFilterItemCallback.ClickFilterItemCallback(0,getAdapterPosition());
        }

    }

}

