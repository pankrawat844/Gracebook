package com.grace.book.chatadapter;

import android.view.View;
import android.widget.TextView;

import com.grace.book.R;
import com.grace.book.chatmodel.Message;
import com.grace.book.utils.GetTimeCovertAgo;
import com.stfalcon.chatkit.messages.MessageHolders;

/*
 * Created by troy379 on 05.04.17.
 */
public class CustomOutcomingImageMessageViewHolder
        extends MessageHolders.OutcomingImageMessageViewHolder<Message> {
    private TextView newmessageText;

    public CustomOutcomingImageMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        newmessageText = itemView.findViewById(R.id.newmessageText);

    }


    //messageText
    @Override
    public void onBind(final Message message) {
        super.onBind(message);

        String text = GetTimeCovertAgo.getNewsFeeTimeAgo(message.getCreatedAt().getTime());
        time.setText(text);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SingleuserchatActivity mSingleuserchatActivity=SingleuserchatActivity.getmSingleuserchatActivity();
                // mSingleuserchatActivity.showFullImage(message.getImageUrl());
            }
        });
        newmessageText.setVisibility(View.GONE);

    }

}