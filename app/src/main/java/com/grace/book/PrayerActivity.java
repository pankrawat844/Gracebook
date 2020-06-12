package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.adapter.FeedListAdapter;
import com.grace.book.adapter.PrayRequestAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.FeedList;
import com.grace.book.myapplication.Myapplication;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;

import java.util.ArrayList;

import static com.grace.book.myapplication.Myapplication.getContext;

public class PrayerActivity extends BaseActivity {
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private PrayRequestAdapter mPrayRequestAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_prayer, frameLayout);
        Myapplication.selection = 1;
        mContext=this;
        selectedDeselectedLayut();
        initUI();

    }
    private void initUI() {
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mPrayRequestAdapter = new PrayRequestAdapter(mContext, new ArrayList<FeedList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        SmartRecyclerAdapter smartRecyclerAdapter = new SmartRecyclerAdapter(mPrayRequestAdapter);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.header_prayer_screen, null);


        recycler_feed.setAdapter(smartRecyclerAdapter);
        mPrayRequestAdapter.addnewItem(new FeedList());
        mPrayRequestAdapter.addnewItem(new FeedList());
        mPrayRequestAdapter.addnewItem(new FeedList());

        smartRecyclerAdapter.setHeaderView(view);

        EditText edittextChat=(EditText)view.findViewById(R.id.edittextChat);
        edittextChat.setFocusable(false);
        edittextChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(PrayerActivity.this, PostallActivity.class);
                mIntent.putExtra("screenType",1);
                startActivity(mIntent);
            }
        });


    }
    public FilterItemCallback lFilterItemCallback =new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            Intent mm = new Intent(PrayerActivity.this, CommentActivity.class);
            startActivity(mm);
        }
    };
}
