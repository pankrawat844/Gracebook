package com.grace.book.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.grace.book.LoginActivity;
import com.grace.book.R;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.model.FeedList;
import com.grace.book.model.NotificationList;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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
                holder.notifcationText.setText(mJobList.getDetails());
                holder.delete_notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ServerRequest(position);
                    }
                });

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
        private ImageView delete_notification;

        public ItemViewHolder(View itemView) {
            super(itemView);
            notifcationText = (TextView) itemView.findViewById(R.id.notifcationText);
            delete_notification = (ImageView) itemView.findViewById(R.id.delete_notification);


            itemView.setTag(getAdapterPosition());
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }

    BusyDialog mBusyDialog;

    private void ServerRequest(final int position) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        String post_id = getModelAt(position).getId();
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();

        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("notification_id", post_id);
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "removenotification";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    mBusyDialog.dismis();

                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        allMessageList.remove(position);
                        notifyDataSetChanged();

                    }
                } catch (Exception e) {
                    Logger.debugLog("Exception", e.getMessage());

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                mBusyDialog.dismis();
                if (statusCode.equalsIgnoreCase("404")) {
                    PersistentUser.resetAllData(mContext);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            }
        });
    }

}

