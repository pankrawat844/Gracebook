package com.grace.book;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.adapter.CommentsAdapter;
import com.grace.book.adapter.FeedListAdapter;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.CommentsList;
import com.grace.book.model.FeedList;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;

import java.util.ArrayList;

import static com.grace.book.myapplication.Myapplication.getContext;

public class CommentActivity extends AppCompatActivity {
    private final int VERTICAL_ITEM_SPACE = 20;
    private Context mContext;
    private RecyclerView recycler_feed;
    private CommentsAdapter mFeedListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        mContext = this;
        initUi();
    }

    private void initUi() {
        findViewById(R.id.layoutBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mFeedListAdapter = new CommentsAdapter(mContext, new ArrayList<CommentsList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recycler_feed.setAdapter(mFeedListAdapter);
        mFeedListAdapter.addnewItem(new CommentsList());
        mFeedListAdapter.addnewItem(new CommentsList());
        mFeedListAdapter.addnewItem(new CommentsList());


    }


}
