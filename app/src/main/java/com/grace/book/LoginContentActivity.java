package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.grace.book.utils.PersistentUser;

public class LoginContentActivity extends AppCompatActivity {
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
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
        findViewById(R.id.layoutSign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mm = new Intent(LoginContentActivity.this, SignupActivity.class);
                startActivity(mm);


            }
        });
        findViewById(R.id.layoutLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mm = new Intent(LoginContentActivity.this, LoginActivity.class);
                startActivity(mm);
            }
        });

    }


}
