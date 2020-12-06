package com.zocia.book.chatadapter;

import android.view.View;
import android.widget.ImageView;

import com.zocia.book.ChatActivity;
import com.zocia.book.R;
import com.zocia.book.chatmodel.Message;
import com.zocia.book.utils.ConstantFunctions;
import com.zocia.book.utils.GetTimeCovertAgo;
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