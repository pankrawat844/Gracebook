package com.grace.book;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.grace.book.myapplication.Myapplication;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_home, frameLayout);
        Myapplication.selection = 0;
        selectedDeselectedLayut();

    }

}
