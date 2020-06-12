package com.grace.book.chatadapter;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;

import com.grace.book.R;
import com.grace.book.chatmodel.Message;
import com.grace.book.utils.GetTimeCovertAgo;
import com.stfalcon.chatkit.messages.MessageHolders;


public class CustomIncomingTextMessageViewHolder
        extends MessageHolders.IncomingTextMessageViewHolder<Message> {

    private View onlineIndicator;
    public CustomIncomingTextMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        String textTime = GetTimeCovertAgo.getNewsFeeTimeAgo(message.getCreatedAt().getTime());
        time.setText(textTime);
        text.setText(Html.fromHtml(message.getText()));
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
