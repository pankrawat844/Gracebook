package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.grace.book.utils.PersistentUser;

public class SplashActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                        PersistentUser.setPushToken(mContext, token);
                        Log.e("token", token);
                    }
                });
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
                Intent mm = new Intent(SplashActivity.this, LoginContentActivity.class);
                startActivity(mm);
                SplashActivity.this.finish();
            }


        }
    };
}
