package com.grace.book.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.R;
import com.grace.book.UserprofileActivity;
import com.grace.book.adapter.FriendRequestListAdapter;
import com.grace.book.adapter.MyPostListAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.FeedList;
import com.grace.book.model.FriendList;

import java.util.ArrayList;

public class FriendRequestFragment extends BaseFragment {
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private FriendRequestListAdapter mFriendRequestListAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notification, container, false);
        viewScreen(view);
        return view;
    }
    public void viewScreen(  View view){
        recycler_feed = (RecyclerView) view.findViewById(R.id.recycler_feed);
        mFriendRequestListAdapter = new FriendRequestListAdapter(getActivity(), new ArrayList<FriendList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        recycler_feed.setAdapter(mFriendRequestListAdapter);
        mFriendRequestListAdapter.addnewItem(new FriendList());
        mFriendRequestListAdapter.addnewItem(new FriendList());
        mFriendRequestListAdapter.addnewItem(new FriendList());
        mFriendRequestListAdapter.addFilterItemCallback(lFilterItemCallback);

    }
    public FilterItemCallback lFilterItemCallback =new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            Intent mm = new Intent(getActivity(), UserprofileActivity.class);
            startActivity(mm);
        }
    };
    @Override
    protected void onVisible() {

    }

    @Override
    protected void onInvisible() {

    }
}
