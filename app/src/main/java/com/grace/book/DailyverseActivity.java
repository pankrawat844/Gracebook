package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.grace.book.utils.PersistentUser;

import java.io.File;

public class DailyverseActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private Context mContext;
    private LinearLayout layoutBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailyverse);
        mContext = this;
        initUi();
    }

    private void initUi() {
//        Glide.with(mContext)
//                .load(new File(mMywallpaperList))
//                .apply(requestOptionsNormal)
//                .into(sliding_image);
//        sliding_image.setOnTouchListener(new ImageMatrixTouchHandler(getActivity()));

        layoutBack=(LinearLayout)this.findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
