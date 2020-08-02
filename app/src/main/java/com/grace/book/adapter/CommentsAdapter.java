package com.grace.book.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.R;
import com.grace.book.UserprofileActivity;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.model.CommentsList;
import com.grace.book.model.Usersdata;
import com.grace.book.utils.ConstantFunctions;
import com.grace.book.utils.DateUtility;
import com.grace.book.utils.GetTimeCovertAgo;
import com.grace.book.utils.PersistentUser;
import com.grace.book.utils.ToastHelper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = CommentsAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<CommentsList> allSearchList;
    private FilterItemCallback lFilterItemCallback;
    private ArrayList<Usersdata> inputList = new ArrayList<>();

    public CommentsAdapter(Context context, ArrayList<CommentsList> myDataset) {
        mContext = context;
        this.allSearchList = new ArrayList<CommentsList>();
        this.allSearchList.addAll(myDataset);
    }

    public void addClickListiner(FilterItemCallback lFilterItemCallback) {
        this.lFilterItemCallback = lFilterItemCallback;
    }

    public void deleteItem(int index) {
        allSearchList.remove(index);
        notifyDataSetChanged();
    }

    public CommentsList getModelAt(int index) {
        return allSearchList.get(index);
    }

    public int getDataSize() {
        return allSearchList.size();
    }

    public void addAllList(ArrayList<CommentsList> clientList) {
        allSearchList.addAll(clientList);
        notifyDataSetChanged();
    }

    public void addAllinputListList(ArrayList<Usersdata> clientList) {
        inputList.addAll(clientList);
        notifyDataSetChanged();
    }

    public void removeAllData() {
        allSearchList.clear();
        notifyDataSetChanged();
    }

    public void addnewItem(CommentsList dataObj) {
        allSearchList.add(dataObj);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_comments, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ItemViewHolder) {
            final ItemViewHolder holder = (ItemViewHolder) viewHolder;
            CommentsList mJobList = allSearchList.get(position);
            try {
                holder.userProfileNmae.setText(mJobList.getmUsersdata().getFname() + " " + mJobList.getmUsersdata().getLname());
                ConstantFunctions.loadImageForCircel(mJobList.getmUsersdata().getProfile_pic(), holder.userProfileImage);
                long time = DateUtility.dateToMillisecond(mJobList.getComment_time());
                String text = GetTimeCovertAgo.getNewsFeeTimeAgo(time);
                holder.nameofitme.setText(text);
                holder.textComment.setText(mJobList.getMessage());
                holder.deleteMessage.setVisibility(View.GONE);
                if (mJobList.getUser_id().equalsIgnoreCase(PersistentUser.getUserID(mContext))) {
                    holder.deleteMessage.setVisibility(View.VISIBLE);
                }
                holder.deleteMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lFilterItemCallback.ClickFilterItemCallback(0, position);
                    }
                });

                setSpan(mJobList.getMessage(), holder.textComment);

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
        private ImageView deleteMessage;
        private ImageView userProfileImage;
        private TextView nameofitme;
        private TextView textComment;
        private TextView userProfileNmae;

        public ItemViewHolder(View itemView) {
            super(itemView);
            deleteMessage = (ImageView) itemView.findViewById(R.id.deleteMessage);
            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            nameofitme = (TextView) itemView.findViewById(R.id.nameofitme);
            textComment = (TextView) itemView.findViewById(R.id.textComment);
            userProfileNmae = (TextView) itemView.findViewById(R.id.userProfileNmae);

            itemView.setOnClickListener(this);
            itemView.setTag(getAdapterPosition());
        }

        @Override
        public void onClick(View v) {

        }

    }

    private void setSpan(String text, TextView mItemTitle) {
        SpannableString spannable = new SpannableString(text);
        final Matcher mention = Pattern.compile("@\\s*(\\w+)").matcher(text);
        while (mention.find()) {
            final String city = mention.group(1);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    //Your Redirect Activity or Fragment
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(Color.BLACK);
                    ds.setFakeBoldText(true);
                }
            };
            int cityIndex = text.indexOf(city) - 1;
            spannable.setSpan(clickableSpan, cityIndex, cityIndex + city.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (inputList.size() > 0) {
            for (int i = 0; i < inputList.size(); i++) {
                if (text.contains(inputList.get(i).getName())) {
                    //Log.d(Constants.TAG,"FeedTitle contains user-->"+tagUser.get(i).getDisplayName());
                    int startIndex = text.indexOf(inputList.get(i).getName());
                    int lastIndex = startIndex + inputList.get(i).getName().length();
                    final int finalI = i;
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {

                            Intent mIntent = new Intent(mContext, UserprofileActivity.class);
                            Bundle extra = new Bundle();
                            extra.putSerializable("objects", inputList.get(finalI));
                            mIntent.putExtra("extra", extra);
                            mContext.startActivity(mIntent);

                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            ds.setColor(Color.BLACK);
                            ds.setFakeBoldText(true);
                        }
                    };
                    //int cityIndex = text.indexOf(city) - 1;
                    spannable.setSpan(clickableSpan, startIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                }
            }
        }
        mItemTitle.setText(spannable);
        mItemTitle.setMovementMethod(LinkMovementMethod.getInstance());
    }


}

