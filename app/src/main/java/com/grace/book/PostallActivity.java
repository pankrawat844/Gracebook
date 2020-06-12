package com.grace.book;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PostallActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private Context mContext;
    private TextView prayerTitel;
    private int screenType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_prayer);
        screenType = getIntent().getIntExtra("screenType", 0);
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
        prayerTitel = (TextView) this.findViewById(R.id.prayerTitel);
        if (screenType == 0) {
            prayerTitel.setText("Create your post");
        } else {
            prayerTitel.setText("Create prayer request");
        }

    }


}
