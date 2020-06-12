package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.adapter.FeedListAdapter;
import com.grace.book.adapter.MyPostListAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.FeedList;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;

import java.util.ArrayList;

public class MyPostActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private Context mContext;
    private LinearLayout layoutBack;
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private MyPostListAdapter mMyPostListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);
        mContext = this;
        initUi();
    }

    private void initUi() {
        layoutBack=(LinearLayout)this.findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mMyPostListAdapter = new MyPostListAdapter(mContext, new ArrayList<FeedList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        recycler_feed.setAdapter(mMyPostListAdapter);
        mMyPostListAdapter.addnewItem(new FeedList());
        mMyPostListAdapter.addnewItem(new FeedList());
        mMyPostListAdapter.addnewItem(new FeedList());

        mMyPostListAdapter.addClickListiner(lFilterItemCallback);

    }
    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            if (type == 0) {
                Intent mm = new Intent(MyPostActivity.this, UserprofileActivity.class);
                startActivity(mm);
            } else if (type == 1) {
                Intent mm = new Intent(MyPostActivity.this, CommentActivity.class);
                startActivity(mm);
            }

        }
    };


}
