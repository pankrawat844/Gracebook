package com.grace.book.chatadapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grace.book.ChatActivity;
import com.grace.book.R;
import com.grace.book.chatmodel.Message;
import com.grace.book.utils.ConstantFunctions;
import com.grace.book.utils.GetTimeCovertAgo;
import com.stfalcon.chatkit.messages.MessageHolders;


/*
 * Created by troy379 on 05.04.17.
 */
public class CustomIncomingVideoMessageViewHolder
        extends MessageHolders.IncomingImageMessageViewHolder<Message> {
    private ImageView imageViewVideoIcome;
    private ImageView videoposticon;

    public CustomIncomingVideoMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        imageViewVideoIcome = itemView.findViewById(R.id.imageViewVideoIcome);
        videoposticon = itemView.findViewById(R.id.videoposticon);

    }

    @Override
    public void onBind(final Message message) {
        super.onBind(message);
        String text = GetTimeCovertAgo.getNewsFeeTimeAgo(message.getCreatedAt().getTime());
        time.setText(text);
        ConstantFunctions.loadImageMatch(message.getVoice().getUrl(), imageViewVideoIcome);
        imageViewVideoIcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity mSingleuserchatActivity = ChatActivity.getChatActivity();
                mSingleuserchatActivity.showFullImage(message.getVoice().getUrl());
            }
        });
        videoposticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity mSingleuserchatActivity = ChatActivity.getChatActivity();
                mSingleuserchatActivity.showFullImage(message.getVoice().getUrl());
            }
        });

    }
}