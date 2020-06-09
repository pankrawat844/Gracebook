package com.grace.book;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class VerificationActivity extends AppCompatActivity {

    private int screen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        screen = getIntent().getIntExtra("screen", 0);
        initUi();
    }

    private void initUi() {
        findViewById(R.id.Submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (screen == 0) {
                    Intent mm = new Intent(VerificationActivity.this, LoginActivity.class);
                    startActivity(mm);
                } else {
                    Intent mm = new Intent(VerificationActivity.this, PasswordresetActivity.class);
                    startActivity(mm);
                }
            }
        });

    }
}
