package com.grace.book;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.grace.book.utils.ConstantFunctions;

public class SignupActivity extends AppCompatActivity {
    private EditText editCity;
    private EditText editCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initUi();
    }

    private void initUi() {
        findViewById(R.id.tvLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, VerificationActivity.class);
                intent.putExtra("screen",0);
                startActivity(intent);
            }
        });
        editCountry=(EditText)this.findViewById(R.id.editCountry);
        editCity=(EditText)this.findViewById(R.id.editCity);


        try {
            String[] address= ConstantFunctions.getAddress();
            if(address.length>1){
                editCountry.setText(address[1]);
                editCity.setText(address[0]);

            }
        }
        catch (Exception ex){
            Log.e("Exception","Exception"+ex.getMessage());

        }

    }
}
