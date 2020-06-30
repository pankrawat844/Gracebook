package com.grace.book.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.R;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.model.Usersdata;
import com.grace.book.utils.ConstantFunctions;
import com.grace.book.utils.DateUtility;
import com.grace.book.utils.GetTimeCovertAgo;

import java.util.ArrayList;


/**
 * Created by Prosanto on 3/7/17.
 */
public class MessageFriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = MessageFriendListAdapter.class.getSimpleName();
    private Context mContext;
    private FilterItemCallback nFilterItemCallback;
    private ArrayList<Usersdata> mSpeciallistModel;

    public MessageFriendListAdapter(Context context, ArrayList<Usersdata> myDataset) {
        mContext = context;
        this.mSpeciallistModel = new ArrayList<Usersdata>();
        this.mSpeciallistModel.addAll(myDataset);

    }

    public Usersdata getModelAt(int index) {
        return mSpeciallistModel.get(index);
    }

    public void addModelAt(ArrayList<Usersdata> myDataset) {
        mSpeciallistModel.clear();
        mSpeciallistModel.addAll(myDataset);
        notifyDataSetChanged();
    }


    public void addFilterItemCallback(FilterItemCallback nFilterItemCallback) {
        this.nFilterItemCallback = nFilterItemCallback;
    }

    public ArrayList<Usersdata> getAllStudents() {
        return mSpeciallistModel;
    }

    public int getDataSize() {
        return mSpeciallistModel.size();
    }

    public void removeAllData() {
        mSpeciallistModel.clear();
        notifyDataSetChanged();
    }

    public void addnewItem(Usersdata dataObj) {
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
            Usersdata mJobList = mSpeciallistModel.get(position);
            try {

                ConstantFunctions.loadImageForCircel(mJobList.getProfile_pic(), holder.userImage);
                holder.username.setText(mJobList.getFname() + " " + mJobList.getLname());

                holder.nameofitme.setText("");
                holder.textMessage.setText("");
                if (mJobList.getmMessageList() != null) {
                    long time = DateUtility.dateToMillisecond(mJobList.getmMessageList().getDuration());
                    String text = GetTimeCovertAgo.getNewsFeeTimeAgo(time);
                    holder.nameofitme.setText(text);
                    if (mJobList.getmMessageList().getType().equalsIgnoreCase("0"))
                        holder.textMessage.setText(mJobList.getmMessageList().getMessage());
                    else
                        holder.textMessage.setText("Media");

                }


            } catch (Exception ex) {

            }

        }
    }

    @Override
    public int getItemCount() {
        return this.mSpeciallistModel.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userImage;
        private TextView username;
        private TextView nameofitme;
        private TextView textMessage;

        public ItemViewHolder(View itemView) {
            super(itemView);
            userImage = (ImageView) itemView.findViewById(R.id.userImage);
            username = (TextView) itemView.findViewById(R.id.username);
            nameofitme = (TextView) itemView.findViewById(R.id.nameofitme);
            textMessage = (TextView) itemView.findViewById(R.id.textMessage);


            itemView.setTag(getPosition());
            itemView.setOnClickListener(this); // bind the listener
        }

        @Override
        public void onClick(View v) {
            nFilterItemCallback.ClickFilterItemCallback(0, getPosition());

        }
    }


}

