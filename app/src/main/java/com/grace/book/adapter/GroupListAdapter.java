package com.grace.book.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.R;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.model.GroupList;

import java.util.ArrayList;


/**
 * Created by Prosanto on 3/7/17.
 */
public class GroupListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = GroupListAdapter.class.getSimpleName();
    private Context mContext;
    private FilterItemCallback nFilterItemCallback;
    private ArrayList<GroupList> mSpeciallistModel;

    public GroupListAdapter(Context context, ArrayList<GroupList> myDataset) {
        mContext = context;
        this.mSpeciallistModel = new ArrayList<GroupList>();
        this.mSpeciallistModel.addAll(myDataset);

    }

    public void addallList(ArrayList<GroupList> myDataset) {
        this.mSpeciallistModel.clear();
        this.mSpeciallistModel.addAll(myDataset);
        notifyDataSetChanged();
    }

    public GroupList getModelAt(int index) {
        return mSpeciallistModel.get(index);
    }

    public void addFilterItemCallback(FilterItemCallback nFilterItemCallback) {
        this.nFilterItemCallback = nFilterItemCallback;
    }

    public ArrayList<GroupList> getAllStudents() {
        return mSpeciallistModel;
    }

    public int getDataSize() {
        return mSpeciallistModel.size();
    }

    public void removeAllData() {
        mSpeciallistModel.clear();
        notifyDataSetChanged();
    }

    public void addnewItem(GroupList dataObj) {
        mSpeciallistModel.add(dataObj);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_group, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ItemViewHolder) {
            final ItemViewHolder holder = (ItemViewHolder) viewHolder;
            GroupList mJobList = mSpeciallistModel.get(position);
            try {
                holder.rowplaylistname.setText(mJobList.getGroup_name());

            } catch (Exception ex) {

            }

        }
    }

    @Override
    public int getItemCount() {
        return this.mSpeciallistModel.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView rowplaylistname;

        public ItemViewHolder(View itemView) {
            super(itemView);
            rowplaylistname = (TextView) itemView.findViewById(R.id.rowplaylistname);
            itemView.setTag(getPosition());
            itemView.setOnClickListener(this); // bind the listener
        }

        @Override
        public void onClick(View v) {
            nFilterItemCallback.ClickFilterItemCallback(0, getPosition());

        }
    }


}

