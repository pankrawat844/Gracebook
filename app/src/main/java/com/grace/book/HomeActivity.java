package com.grace.book;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.grace.book.adapter.FeedListAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.FeedList;
import com.grace.book.myapplication.Myapplication;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;

import java.util.ArrayList;

import static com.grace.book.myapplication.Myapplication.getContext;

public class HomeActivity extends BaseActivity {
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private FeedListAdapter mFeedListAdapter;
    private Context mContext;
    private Button tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_home, frameLayout);
        Myapplication.selection = 0;
        mContext = this;
        selectedDeselectedLayut();
        initUI();
    }

    private void initUI() {
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mFeedListAdapter = new FeedListAdapter(mContext, new ArrayList<FeedList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        SmartRecyclerAdapter smartRecyclerAdapter = new SmartRecyclerAdapter(mFeedListAdapter);
       // smartRecyclerAdapter.setFooterView(footerView);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.header_home_screen, null);


        recycler_feed.setAdapter(smartRecyclerAdapter);
        mFeedListAdapter.addnewItem(new FeedList());
        mFeedListAdapter.addnewItem(new FeedList());
        mFeedListAdapter.addnewItem(new FeedList());

        smartRecyclerAdapter.setHeaderView(view);

        EditText edittextHomePost=(EditText)view.findViewById(R.id.edittextHomePost);
        edittextHomePost.setFocusable(false);
        edittextHomePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(HomeActivity.this, PostallActivity.class);
                mIntent.putExtra("screenType",0);
                startActivity(mIntent);
            }
        });


    }
    public FilterItemCallback lFilterItemCallback =new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            Intent mm = new Intent(HomeActivity.this, CommentActivity.class);
            startActivity(mm);
        }
    };
}
