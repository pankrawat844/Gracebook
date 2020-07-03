package com.grace.book.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.LoginActivity;
import com.grace.book.R;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.model.FeedList;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.ConstantFunctions;
import com.grace.book.utils.DateUtility;
import com.grace.book.utils.GetTimeCovertAgo;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class GroupPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = GroupPostAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<FeedList> allSearchList;
    private FilterItemCallback lFilterItemCallback;

    public GroupPostAdapter(Context context, ArrayList<FeedList> myDataset) {
        mContext = context;
        this.allSearchList = new ArrayList<FeedList>();
        this.allSearchList.addAll(myDataset);
    }

    public void addClickListiner(FilterItemCallback lFilterItemCallback) {
        this.lFilterItemCallback = lFilterItemCallback;
    }

    public FeedList getModelAt(int index) {
        return allSearchList.get(index);
    }

    public void remvoeList(int index) {
        allSearchList.remove(index);
        notifyDataSetChanged();

    }

    public int getDataSize() {
        return allSearchList.size();
    }

    public void addAllList(ArrayList<FeedList> clientList) {
        allSearchList.addAll(clientList);
        notifyDataSetChanged();
    }

    public void removeAllData() {
        allSearchList.clear();
        notifyDataSetChanged();
    }

    public void addnewItem(FeedList dataObj) {
        allSearchList.add(dataObj);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ItemViewHolder) {
            final ItemViewHolder holder = (ItemViewHolder) viewHolder;
            FeedList mJobList = allSearchList.get(position);
            try {

                holder.userView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lFilterItemCallback.ClickFilterItemCallback(0, position);
                    }
                });
                holder.likeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ServerRequest(position);
                        //lFilterItemCallback.ClickFilterItemCallback(1, position);
                    }
                });

                holder.layoutchat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lFilterItemCallback.ClickFilterItemCallback(2, position);
                    }
                });
                holder.shareView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lFilterItemCallback.ClickFilterItemCallback(3, position);
                    }
                });
                holder.delete_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lFilterItemCallback.ClickFilterItemCallback(4, position);
                    }
                });
                holder.videoposticon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lFilterItemCallback.ClickFilterItemCallback(5, position);
                    }
                });
                holder.layoutForImage_audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lFilterItemCallback.ClickFilterItemCallback(5, position);
                    }
                });


                holder.textMessage.setText(mJobList.getDetails());
                holder.username.setText(mJobList.getmUsersdata().getFname() + " " + mJobList.getmUsersdata().getLname());
                long time = DateUtility.dateToMillisecond(mJobList.getPost_time());
                String text = GetTimeCovertAgo.getNewsFeeTimeAgo(time);
                holder.timeduration.setText(text);
                holder.totalLike.setText(mJobList.getLike_count());
                holder.totalComment.setText(mJobList.getComment_count());
                ConstantFunctions.loadImageForCircel(mJobList.getmUsersdata().getProfile_pic(), holder.profileUser);

                if (mJobList.isIs_like()) {
                    holder.IsLikemage.setImageResource(R.drawable.ic_favorite_24px);
                } else {
                    holder.IsLikemage.setImageResource(R.drawable.ic_un_favorite_24px);
                }

                holder.videoposticon.setVisibility(View.GONE);
                holder.layoutForImage_video.setVisibility(View.GONE);
                holder.layoutForImage_audio.setVisibility(View.GONE);
                holder.delete_icon.setVisibility(View.GONE);


                if (mJobList.getPost_type().equalsIgnoreCase("0")) {

                } else if (mJobList.getPost_type().equalsIgnoreCase("1")) {
                    holder.layoutForImage_video.setVisibility(View.VISIBLE);
                    ConstantFunctions.loadImageNomal(mJobList.getPost_path(), holder.postImage);

                } else if (mJobList.getPost_type().equalsIgnoreCase("2")) {
                    holder.layoutForImage_video.setVisibility(View.VISIBLE);
                    holder.videoposticon.setVisibility(View.VISIBLE);
                    ConstantFunctions.loadImageNomal(mJobList.getPost_path(), holder.postImage);

                } else if (mJobList.getPost_type().equalsIgnoreCase("3")) {
                    holder.layoutForImage_audio.setVisibility(View.VISIBLE);

                }
                if (PersistentUser.getUserID(mContext).equalsIgnoreCase(mJobList.getUser_id())) {
                    holder.delete_icon.setVisibility(View.VISIBLE);
                }


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
        private LinearLayout layoutchat;
        private RelativeLayout userView;
        private LinearLayout shareView;
        private LinearLayout likeView;
        private TextView textMessage;
        private TextView totalComment;
        private TextView totalLike;
        private ImageView IsLikemage;
        private TextView username;
        private TextView timeduration;
        private ImageView profileUser;
        private ImageView postImage;
        private ImageView videoposticon;
        private RelativeLayout layoutForImage_video;
        private RelativeLayout layoutForImage_audio;
        private ImageView delete_icon;


        public ItemViewHolder(View itemView) {
            super(itemView);
            userView = (RelativeLayout) itemView.findViewById(R.id.userView);
            likeView = (LinearLayout) itemView.findViewById(R.id.likeView);
            layoutchat = (LinearLayout) itemView.findViewById(R.id.layoutchat);
            shareView = (LinearLayout) itemView.findViewById(R.id.shareView);
            postImage = (ImageView) itemView.findViewById(R.id.postImage);
            videoposticon = (ImageView) itemView.findViewById(R.id.videoposticon);
            layoutForImage_video = (RelativeLayout) itemView.findViewById(R.id.layoutForImage_video);
            layoutForImage_audio = (RelativeLayout) itemView.findViewById(R.id.layoutForImage_audio);

            delete_icon = (ImageView) itemView.findViewById(R.id.deletePost);
            profileUser = (ImageView) itemView.findViewById(R.id.profileUser);
            IsLikemage = (ImageView) itemView.findViewById(R.id.IsLikemage);
            totalLike = (TextView) itemView.findViewById(R.id.totalLike);
            totalComment = (TextView) itemView.findViewById(R.id.totalComment);
            textMessage = (TextView) itemView.findViewById(R.id.textMessage);
            timeduration = (TextView) itemView.findViewById(R.id.timeduration);
            username = (TextView) itemView.findViewById(R.id.username);
            itemView.setOnClickListener(this);
            itemView.setTag(getAdapterPosition());
        }

        @Override
        public void onClick(View v) {

        }

    }

    private void ServerRequest(final int position) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        String post_id = getModelAt(position).getId();
        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("group_post_id", post_id);

        Log.e("allHashMap", allHashMap.toString());


        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "grouppostaddLike";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        FeedList mFeedList = getModelAt(position);
                        if (mFeedList.isIs_like()) {
                            mFeedList.setIs_like(false);
                            int countLike = Integer.parseInt(mFeedList.getLike_count());
                            countLike = countLike - 1;
                            getModelAt(position).setLike_count("" + countLike);
                        } else {
                            mFeedList.setIs_like(true);
                            int countLike = Integer.parseInt(mFeedList.getLike_count());
                            countLike = countLike + 1;
                            getModelAt(position).setLike_count("" + countLike);

                        }
                        notifyDataSetChanged();

                    }
                } catch (Exception e) {
                    Logger.debugLog("Exception", e.getMessage());

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
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

