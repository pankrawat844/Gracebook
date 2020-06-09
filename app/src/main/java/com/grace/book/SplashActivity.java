package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.grace.book.utils.PersistentUser;

public class SplashActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        mHandler.postDelayed(mPendingLauncherRunnable, 1000);

    }

    private final Runnable mPendingLauncherRunnable = new Runnable() {
        @Override
        public void run() {
            if (PersistentUser.isLogged(mContext)) {
                Intent mm = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(mm);
                SplashActivity.this.finish();
            } else {
                Intent mm = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(mm);
                SplashActivity.this.finish();
            }


        }
    };
}
