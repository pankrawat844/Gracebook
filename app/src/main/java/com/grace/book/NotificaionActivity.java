package com.grace.book;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.adapter.MyPostListAdapter;
import com.grace.book.adapter.NotificaitonListAdapter;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.FeedList;
import com.grace.book.model.NotificationList;
import com.grace.book.myapplication.Myapplication;

import java.util.ArrayList;

public class NotificaionActivity extends BaseActivity {
    private final int VERTICAL_ITEM_SPACE = 10;
    private Context mContext;
    private RecyclerView recycler_feed;
    private NotificaitonListAdapter mNotificaitonListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_notification, frameLayout);
        Myapplication.selection = 3;
        selectedDeselectedLayut();
        initUi();
    }

    private void initUi() {
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mNotificaitonListAdapter = new NotificaitonListAdapter(mContext, new ArrayList<NotificationList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        recycler_feed.setAdapter(mNotificaitonListAdapter);
        mNotificaitonListAdapter.addList(new NotificationList());
        mNotificaitonListAdapter.addList(new NotificationList());
        mNotificaitonListAdapter.addList(new NotificationList());
    }

}
