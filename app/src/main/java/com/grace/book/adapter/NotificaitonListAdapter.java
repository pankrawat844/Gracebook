package com.grace.book.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.grace.book.R;
import com.grace.book.model.NotificationList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
        private ImageView ShowPlayIcon;
        private TextView rowplaylistname;
        private TextView rowplaylistduatiron;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ShowPlayIcon = (ImageView) itemView.findViewById(R.id.ShowPlayIcon);
            rowplaylistname = (TextView) itemView.findViewById(R.id.rowplaylistname);
            rowplaylistduatiron = (TextView) itemView.findViewById(R.id.rowplaylistduatiron);


            itemView.setTag(getAdapterPosition());
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }

    public String getDate(String duratraion) {
        if (duratraion == null || duratraion.equalsIgnoreCase(""))
            return "";
        else {

            String dateFormat = "dd MMMM yyyy";
            long milliSeconds = Long.parseLong(duratraion);
            milliSeconds = milliSeconds * 1000;
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            return formatter.format(calendar.getTime());
        }

    }


}

