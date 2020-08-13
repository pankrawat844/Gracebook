package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.ConstantFunctions;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;
import com.grace.book.utils.ToastHelper;
import com.grace.book.utils.ValidateEmail;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = SignupActivity.class.getSimpleName();
    private EditText editCity;
    private EditText editCountry;
    private EditText edittextFname;
    private EditText edittextLname;
    private EditText edittextEmail;
    private CountryCodePicker ccp;
    private EditText edittextphone;
    private EditText editChurch;
    private EditText editpassword;
    private EditText editConfirmpassword;
    private BusyDialog mBusyDialog;
    private Context mContext;
    private CheckBox checkboxterms;
    private TextView textiew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mContext = this;
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
                validation();
            }
        });
        editCountry = (EditText) this.findViewById(R.id.editCountry);
        editCity = (EditText) this.findViewById(R.id.editCity);

        edittextFname = (EditText) this.findViewById(R.id.edittextFname);
        edittextLname = (EditText) this.findViewById(R.id.edittextLname);
        edittextEmail = (EditText) this.findViewById(R.id.edittextEmail);
        edittextphone = (EditText) this.findViewById(R.id.edittextphone);
        editChurch = (EditText) this.findViewById(R.id.editChurch);
        editpassword = (EditText) this.findViewById(R.id.editpassword);
        edittextFname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        edittextLname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editChurch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editCountry.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editCity.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        
        editConfirmpassword = (EditText) this.findViewById(R.id.editConfirmpassword);
        ccp = (CountryCodePicker) this.findViewById(R.id.ccp);
        textiew = (TextView) this.findViewById(R.id.textiew);
        checkboxterms = (CheckBox) this.findViewById(R.id.checkboxterms);
        checkboxterms.setChecked(true);
        textiew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mm = new Intent(SignupActivity.this, OtherActivity.class);
                startActivity(mm);
            }
        });
        try {
            String[] address = ConstantFunctions.getAddress();
            if (address.length > 1) {
                editCountry.setText(address[1]);
                editCity.setText(address[0]);

            }
        } catch (Exception ex) {
            Log.e("Exception", "Exception" + ex.getMessage());

        }
    }

    public void validation() {
        String fname = edittextFname.getText().toString().trim();
        String lname = edittextLname.getText().toString().trim();
        String email = edittextEmail.getText().toString().trim();
        String phone = edittextphone.getText().toString().trim();
        String country = editCountry.getText().toString().trim();
        String church = editChurch.getText().toString().trim();
        String city = editCity.getText().toString().trim();
        String password = editpassword.getText().toString().trim();
        String confirmpassword = editConfirmpassword.getText().toString().trim();
        String country_code = ccp.getSelectedCountryCode().toString().trim();
        String country_code_name = ccp.getSelectedCountryNameCode().toString().trim();

        if (!checkboxterms.isChecked()) {
            ToastHelper.showToast(mContext, "Please check our term and condition");
            return;
        } else if (fname.equalsIgnoreCase("")) {
            edittextFname.setError("Enter first name");
            return;
        } else if (lname.equalsIgnoreCase("")) {
            edittextLname.setError("Enter last name");
            return;
        } else if (email.equalsIgnoreCase("")) {
            edittextEmail.setError("Enter email");
            return;
        } else if (!ValidateEmail.validateEmail(email)) {
            edittextEmail.setError("Enter valid email");
            return;
        } else if (phone.equalsIgnoreCase("")) {
            edittextphone.setError("Enter phone");
            return;
        } else if (church.equalsIgnoreCase("")) {
            editChurch.setError("Enter church");
            return;
        } else if (country.equalsIgnoreCase("")) {
            editCountry.setError("Enter country");
            return;
        } else if (city.equalsIgnoreCase("")) {
            editCity.setError("Enter city");
            return;
        } else if (password.equalsIgnoreCase("")) {
            editpassword.setError("Enter password");
            return;
        } else if (confirmpassword.equalsIgnoreCase("")) {
            editConfirmpassword.setError("Enter confirm password");
            return;
        } else if (!confirmpassword.equalsIgnoreCase(password)) {
            editConfirmpassword.setError("Enter correct password");
            return;
        } else {
            HashMap<String, String> allHashMap = new HashMap<>();
            allHashMap.put("fname", fname);
            allHashMap.put("lname", lname);
            allHashMap.put("email", email);
            allHashMap.put("phone", phone);
            allHashMap.put("country_code", country_code);
            allHashMap.put("country_code_name", country_code_name);
            allHashMap.put("country", country);
            allHashMap.put("church", church);
            allHashMap.put("city", city);
            allHashMap.put("password", password);
            allHashMap.put("confirmpassword", confirmpassword);
            allHashMap.put("device_type", "1");
            allHashMap.put("device_token", PersistentUser.getPushToken(mContext));
            allHashMap.put("latitude", PersistentUser.getLatitude(mContext));
            allHashMap.put("longitude", PersistentUser.getLongitude(mContext));
            sigupServerRequest(allHashMap);

        }

    }

    private void sigupServerRequest(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        final String phone = edittextphone.getText().toString().trim();
        final String country_code = ccp.getSelectedCountryCode().toString().trim();

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        final String url = AllUrls.BASEURL + "signup";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        Intent mIntent = new Intent(SignupActivity.this, LoginActivity.class);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mIntent);
                        finish();


//                        JSONObject data = mJsonObject.getJSONObject("data");
//                        String otp_code = data.getString("otp_code");
//                        Intent intent = new Intent(SignupActivity.this, VerificationActivity.class);
//                        intent.putExtra("screen", 0);
//                        intent.putExtra("otp_code", otp_code);
//                        intent.putExtra("phone", phone);
//                        intent.putExtra("country_code", country_code);
//
//                        startActivity(intent);

                    } else {
                        String message = mJsonObject.getString("message");
                        ToastHelper.showToast(mContext, message);

                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                mBusyDialog.dismis();
                ToastHelper.showToast(mContext, serverResponse);
                Logger.debugLog("onFailed serverResponse", serverResponse);
            }
        });
    }
}
