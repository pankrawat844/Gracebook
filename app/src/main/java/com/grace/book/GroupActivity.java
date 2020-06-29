package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.adapter.MessageFriendListAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.MesageUserList;
import com.grace.book.myapplication.Myapplication;

import java.util.ArrayList;

public class GroupActivity extends BaseActivity {
    private final int VERTICAL_ITEM_SPACE = 20;
    private MessageFriendListAdapter mMessageFriendListAdapter;
    private  RecyclerView recycler_feed;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_group, frameLayout);
        Myapplication.selection = 5;
        selectedDeselectedLayut();
        initUi();

    }

    private void initUi() {
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mMessageFriendListAdapter = new MessageFriendListAdapter(mContext, new ArrayList<MesageUserList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        recycler_feed.setAdapter(mMessageFriendListAdapter);
        mMessageFriendListAdapter.addnewItem(new MesageUserList());
        mMessageFriendListAdapter.addnewItem(new MesageUserList());
        mMessageFriendListAdapter.addnewItem(new MesageUserList());
        mMessageFriendListAdapter.addFilterItemCallback(lFilterItemCallback);
    }
    public FilterItemCallback lFilterItemCallback =new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            Intent mm = new Intent(GroupActivity.this, ChatActivity.class);
            startActivity(mm);
        }
    };


}
