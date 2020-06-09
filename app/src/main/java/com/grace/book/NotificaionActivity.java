package com.grace.book;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.grace.book.myapplication.Myapplication;

public class NotificaionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_notification, frameLayout);
        Myapplication.selection = 3;
        selectedDeselectedLayut();

    }

}
