package com.grace.book;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.potyvideo.library.AndExoPlayerView;

public class VideoPlayertActivity extends AppCompatActivity {
    private Context mContext;
    private LinearLayout layoutBack;
    private String url = "";
    private AndExoPlayerView andExoPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        mContext = this;
        url = getIntent().getStringExtra("url");
        initUi();
    }

    private void initUi() {
        andExoPlayerView = findViewById(R.id.andExoPlayerView);
        layoutBack = (LinearLayout) this.findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        andExoPlayerView.setSource(url);

        initUiNew();
    }

    private void initUiNew() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
