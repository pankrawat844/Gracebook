package com.zocia.book.chatadapter;

import android.view.View;
import android.widget.TextView;

import com.zocia.book.R;
import com.zocia.book.chatmodel.Message;
import com.zocia.book.utils.GetTimeCovertAgo;
import com.stfalcon.chatkit.messages.MessageHolders;


/*
 * Created by troy379 on 05.04.17.
 */
public class CustomIncomingImageMessageViewHolder
        extends MessageHolders.IncomingImageMessageViewHolder<Message> {
    private View onlineIndicator;
    private TextView newmessageText;


    public CustomIncomingImageMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
//        newmessageText = itemView.findViewById(R.id.newmessageText);

    }

    @Override
    public void onBind(final Message message) {
        super.onBind(message);
        String text = GetTimeCovertAgo.getNewsFeeTimeAgo(message.getCreatedAt().getTime());
        time.setText(text);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SingleuserchatActivity mSingleuserchatActivity=SingleuserchatActivity.getmSingleuserchatActivity();
                //mSingleuserchatActivity.showFullImage(message.getImageUrl());
            }
        });
//        newmessageText.setText(message.getText());
//        if (message.getText().toString().trim().equalsIgnoreCase("")) {
//            newmessageText.setVisibility(View.GONE);
//        }
    }
}