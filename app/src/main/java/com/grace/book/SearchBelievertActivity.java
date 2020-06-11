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

import com.grace.book.adapter.MyPostListAdapter;
import com.grace.book.adapter.SearchbelieverListAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.BelieverList;
import com.grace.book.model.FeedList;

import java.util.ArrayList;

public class SearchBelievertActivity extends AppCompatActivity {
    private Context mContext;
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private SearchbelieverListAdapter mSearchbelieverListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_believers);
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
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_search);
        mSearchbelieverListAdapter = new SearchbelieverListAdapter(mContext, new ArrayList<BelieverList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        recycler_feed.setAdapter(mSearchbelieverListAdapter);
        mSearchbelieverListAdapter.addnewItem(new BelieverList());
        mSearchbelieverListAdapter.addnewItem(new BelieverList());
        mSearchbelieverListAdapter.addnewItem(new BelieverList());

        mSearchbelieverListAdapter.addFilterItemCallback(lFilterItemCallback);

    }
    public FilterItemCallback lFilterItemCallback =new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            Intent mm = new Intent(SearchBelievertActivity.this, UserprofileActivity.class);
            startActivity(mm);
        }
    };


}
