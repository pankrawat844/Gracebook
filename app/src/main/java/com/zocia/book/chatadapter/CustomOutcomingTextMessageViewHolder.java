package com.zocia.book.chatadapter;

import android.view.View;

import com.zocia.book.chatmodel.Message;
import com.zocia.book.utils.GetTimeCovertAgo;
import com.stfalcon.chatkit.messages.MessageHolders;


public class CustomOutcomingTextMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<Message> {
    public CustomOutcomingTextMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        String textDuratin = GetTimeCovertAgo.getNewsFeeTimeAgo(message.getCreatedAt().getTime());
        time.setText(textDuratin);

    }
}
