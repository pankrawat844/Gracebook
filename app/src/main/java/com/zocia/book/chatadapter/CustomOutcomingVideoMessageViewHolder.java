package com.zocia.book.chatadapter;

import android.util.Log;
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
public class CustomOutcomingVideoMessageViewHolder
        extends MessageHolders.IncomingImageMessageViewHolder<Message> {
    private ImageView imageViewVideo;
    private ImageView videoposticon;

    public CustomOutcomingVideoMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        imageViewVideo = itemView.findViewById(R.id.imageViewVideo);
        videoposticon = itemView.findViewById(R.id.videoposticon);

    }

    //messageText
    @Override
    public void onBind(final Message message) {
        super.onBind(message);
        String text = GetTimeCovertAgo.getNewsFeeTimeAgo(message.getCreatedAt().getTime());
        time.setText(text);
        String videoImage=message.getVoice().getUrl();
        ConstantFunctions.loadImageMatch(videoImage, imageViewVideo);
        Log.w("videoImage",videoImage);
        imageViewVideo.setOnClickListener(new View.OnClickListener() {
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