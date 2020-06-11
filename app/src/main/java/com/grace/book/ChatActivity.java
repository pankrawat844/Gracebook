package com.grace.book;

import android.os.Bundle;

import com.grace.book.myapplication.Myapplication;

public class ChatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_chatsuser, frameLayout);
        Myapplication.selection = 4;
        selectedDeselectedLayut();

    }

}
