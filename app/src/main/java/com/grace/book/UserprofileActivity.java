package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.adapter.MyPostListAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.FeedList;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;

import java.util.ArrayList;

import static com.grace.book.myapplication.Myapplication.getContext;

public class UserprofileActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private Context mContext;
    private LinearLayout layoutBack;
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private MyPostListAdapter mMyPostListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useprofile);
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

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_of_profile, null);


        SmartRecyclerAdapter smartRecyclerAdapter = new SmartRecyclerAdapter(mMyPostListAdapter);
        smartRecyclerAdapter.setHeaderView(view);


        recycler_feed.setAdapter(smartRecyclerAdapter);
        mMyPostListAdapter.addnewItem(new FeedList());
        mMyPostListAdapter.addnewItem(new FeedList());
        mMyPostListAdapter.addnewItem(new FeedList());

    }

    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            if (position == 0) {
                Intent mm = new Intent(UserprofileActivity.this, UserprofileActivity.class);
                startActivity(mm);
            } else if (position == 1) {
                Intent mm = new Intent(UserprofileActivity.this, CommentActivity.class);
                startActivity(mm);
            }

        }
    };

}
