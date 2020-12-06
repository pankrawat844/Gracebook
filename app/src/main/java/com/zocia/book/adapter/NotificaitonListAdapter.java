package com.zocia.book.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.zocia.book.R;
import com.zocia.book.model.NotificationList;
import com.zocia.book.utils.ConstantFunctions;
import com.zocia.book.utils.DateUtility;
import com.zocia.book.utils.GetTimeCovertAgo;

import java.util.ArrayList;

public class NotificaitonListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = NotificaitonListAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<NotificationList> allMessageList = new ArrayList<>();
    private int showPlayIcon = 0;

    public NotificaitonListAdapter(Context context, ArrayList<NotificationList> myDataset) {
        mContext = context;
        this.allMessageList = new ArrayList<NotificationList>();
        this.allMessageList.addAll(myDataset);

    }

    public NotificationList getModelAt(int index) {
        return allMessageList.get(index);
    }

    public int getDataSize() {
        return allMessageList.size();
    }

    public void addAllList(ArrayList<NotificationList> clientList) {
        allMessageList.clear();
        allMessageList.addAll(clientList);
        notifyDataSetChanged();
    }

    public void addList(NotificationList clientList) {
        allMessageList.add(clientList);
        notifyDataSetChanged();
    }
    public void removeaddList(int clientList) {
        allMessageList.remove(clientList);
        notifyDataSetChanged();
    }

    public int getPlayIcon() {
        return showPlayIcon;
    }

    public void showPlayIcon(int position) {
        this.showPlayIcon = position;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_notificaionlist, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ItemViewHolder) {
            final ItemViewHolder holder = (ItemViewHolder) viewHolder;
            NotificationList mJobList = allMessageList.get(position);
            try {
                holder.notifcationText.setText(mJobList.getDetails());
                long time = DateUtility.dateToMillisecond(mJobList.getDuration());
                String text = GetTimeCovertAgo.getNewsFeeTimeAgo(time);
                holder.rowplaylistduatiron.setText(text);
                if(mJobList.getmUsersdata()!=null){
                    ConstantFunctions.loadImageForCircel(mJobList.getmUsersdata().getProfile_pic(), holder.ShowPlayIcon);
                }

            } catch (Exception ex) {

            }

        }
    }

    @Override
    public int getItemCount() {
        return this.allMessageList.size();
    }

    public interface ClickListener {
        void onItemClick(int type, int position, View v);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView notifcationText;
        private TextView rowplaylistduatiron;
        private ImageView ShowPlayIcon;

        public ItemViewHolder(View itemView) {
            super(itemView);
            notifcationText = (TextView) itemView.findViewById(R.id.notifcationText);
            rowplaylistduatiron = (TextView) itemView.findViewById(R.id.rowplaylistduatiron);
            ShowPlayIcon = (ImageView) itemView.findViewById(R.id.ShowPlayIcon);


            itemView.setTag(getAdapterPosition());
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }


}

