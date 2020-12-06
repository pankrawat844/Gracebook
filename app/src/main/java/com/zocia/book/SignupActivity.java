package com.zocia.book;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zocia.book.adapter.InterestsAdapter;
import com.zocia.book.callbackinterface.InterestListener;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.myapplication.Myapplication;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.BusyDialog;
import com.zocia.book.utils.ConstantFunctions;
import com.zocia.book.utils.Helpers;
import com.zocia.book.utils.InterestList;
import com.zocia.book.utils.Logger;
import com.zocia.book.utils.PersistentUser;
import com.zocia.book.utils.ToastHelper;
import com.zocia.book.utils.ValidateEmail;
import com.hbb20.CountryCodePicker;

import org.chat21.android.core.ChatManager;
import org.chat21.android.core.authentication.task.RefreshFirebaseInstanceIdTask;
import org.chat21.android.core.contacts.synchronizers.ContactsSynchronizer;
import org.chat21.android.core.exception.ChatFieldNotFoundException;
import org.chat21.android.core.users.models.IChatUser;
import org.chat21.android.ui.ChatUI;
import org.chat21.android.ui.contacts.activites.ContactListActivity;
import org.chat21.android.ui.conversations.listeners.OnNewConversationClickListener;
import org.chat21.android.ui.login.activities.ChatLoginActivity;
import org.chat21.android.ui.login.activities.ChatSignUpActivity;
import org.chat21.android.utils.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.chat21.android.ui.ChatUI.BUNDLE_SIGNED_UP_USER_EMAIL;
import static org.chat21.android.ui.ChatUI.BUNDLE_SIGNED_UP_USER_PASSWORD;
import static org.chat21.android.utils.DebugConstants.DEBUG_LOGIN;

public class SignupActivity extends AppCompatActivity implements InterestListener {
    private static final String TAG = SignupActivity.class.getSimpleName();
    public static ArrayList<String> interestArrayList = new ArrayList<>();
    private Spinner editCountry;
    private EditText editCity;
    private CountryCodePicker ccp;
    private EditText edittextFname;
    private EditText edittextLname;
    private EditText edittextEmail;
    private TextView edittextDOB;
    private EditText edittextphone;
    private Spinner mGender;
    private EditText editpassword;
    private EditText editConfirmpassword;
    private BusyDialog mBusyDialog;
    private Context mContext;
    private CheckBox checkboxterms;
    private TextView textiew;
    private LinearLayout signUpLL;
    private LinearLayout interestLL;
    private RecyclerView interestRv;
    private String date, month, yearValue;
    private ArrayList<String> genderList;
    private InterestsAdapter interestsAdapter;
    private TextView selectedInterestTotalTxt;
    private LinearLayout layoutBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mContext = this;
        mAuth = FirebaseAuth.getInstance();

        genderList = new ArrayList<>();

        genderList.add("Enter Your Gender");
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Others");
        initUi();
        setInterestList();
        setGenderSpinner();
    }

    private void setInterestList() {
        interestsAdapter = new InterestsAdapter(this, InterestList.getInterestList(), this);
        interestRv.setLayoutManager(new LinearLayoutManager(this));
        interestRv.setItemAnimator(new DefaultItemAnimator());
        interestRv.setAdapter(interestsAdapter);
    }

    private void setGenderSpinner() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(SignupActivity.this, android.R.layout.simple_list_item_1, genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mGender.setAdapter(genderAdapter);
    }

    private void initUi() {
        interestLL = (LinearLayout) this.findViewById(R.id.interestLL);
        signUpLL = (LinearLayout) this.findViewById(R.id.signUpLL);
        interestRv = (RecyclerView) this.findViewById(R.id.recycler_interest);
        selectedInterestTotalTxt = (TextView) this.findViewById(R.id.selectedItemTotal);
        layoutBack = findViewById(R.id.layoutBack1);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestLL.setVisibility(View.GONE);
                signUpLL.setVisibility(View.VISIBLE);
                ((Myapplication) getApplicationContext()).getInterest().clear();

            }
        });
        findViewById(R.id.tvLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestLL.setVisibility(View.VISIBLE);
                signUpLL.setVisibility(View.GONE);
                setInterestList();


            }
        });
        findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((Myapplication) getApplicationContext()).getInterest().size() == 5)
                    validation();
                else
                    Toast.makeText(SignupActivity.this, "Please select 5 interest", Toast.LENGTH_SHORT).show();
            }
        });
        editCountry = (Spinner) this.findViewById(R.id.editCountry);
        String[] arr = getResources().getStringArray(R.array.signup_countries);
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i].compareTo(arr[j]) > 0) {
                    String temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        editCountry.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, arr));
        editCity = (EditText) this.findViewById(R.id.editCity);
        mGender = (Spinner) this.findViewById(R.id.gender);
        edittextFname = (EditText) this.findViewById(R.id.edittextFname);
        edittextLname = (EditText) this.findViewById(R.id.edittextLname);
        edittextDOB = (TextView) this.findViewById(R.id.edittextDOB);
        edittextEmail = (EditText) this.findViewById(R.id.edittextEmail);
        edittextphone = (EditText) this.findViewById(R.id.edittextphone);
        editpassword = (EditText) this.findViewById(R.id.editpassword);
        ccp = (CountryCodePicker) this.findViewById(R.id.ccp);
        edittextFname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        edittextLname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
//        editCountry.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editCity.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editConfirmpassword = (EditText) this.findViewById(R.id.editConfirmpassword);
        textiew = (TextView) this.findViewById(R.id.textiew);
        checkboxterms = (CheckBox) this.findViewById(R.id.checkboxterms);
        checkboxterms.setChecked(true);
        edittextDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });
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
//                editCountry.setText(address[1]);
                editCity.setText(address[0]);
            }
        } catch (Exception ex) {
            Log.e("Exception", "Exception" + ex.getMessage());

        }
    }

    private void openDatePickerDialog() {
        final Calendar selectedDate = Calendar.getInstance();
        final Calendar currentDate = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDate.set(year, monthOfYear, dayOfMonth);
                date = SignupActivity.this.getDateValue(dayOfMonth);
                month = SignupActivity.this.getDateValue(monthOfYear + 1);
                yearValue = String.valueOf(year);
                edittextDOB.setText(date + "/" + month + "/" + yearValue);
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private String getDateValue(int date) {
        if (date > 0 && date < 10) {
            return "0" + date;
        } else {
            return String.valueOf(date);
        }
    }

    public void validation() {
        String fname = edittextFname.getText().toString().trim();
        String lname = edittextLname.getText().toString().trim();
        String dob = edittextDOB.getText().toString().trim();
        String email = edittextEmail.getText().toString().trim();
        String phone = edittextphone.getText().toString().trim();
        String country = editCountry.getSelectedItem().toString();
        String city = editCity.getText().toString().trim();
        String password = editpassword.getText().toString().trim();
        String confirmpassword = editConfirmpassword.getText().toString().trim();
        String gender = mGender.getSelectedItem().toString();
        String country_code = ccp.getSelectedCountryCode().toString().trim();
        String country_code_name = ccp.getSelectedCountryNameCode().toString().trim();

        if (!checkboxterms.isChecked()) {
            interestLL.setVisibility(View.GONE);
            signUpLL.setVisibility(View.VISIBLE);
            ToastHelper.showToast(mContext, "Please check our term and condition");
            return;
        } else if (fname.equalsIgnoreCase("")) {
            interestLL.setVisibility(View.GONE);
            signUpLL.setVisibility(View.VISIBLE);
            edittextFname.setError("Enter first name");
            return;
        } else if (lname.equalsIgnoreCase("")) {
            interestLL.setVisibility(View.GONE);
            signUpLL.setVisibility(View.VISIBLE);
            edittextLname.setError("Enter last name");
            return;
        } else if (dob.equalsIgnoreCase("")) {
            interestLL.setVisibility(View.GONE);
            signUpLL.setVisibility(View.VISIBLE);
            edittextDOB.setError("Enter DOB");
            return;
        } else if (email.equalsIgnoreCase("")) {
            interestLL.setVisibility(View.GONE);
            signUpLL.setVisibility(View.VISIBLE);
            edittextEmail.setError("Enter email");
            return;
        } else if (!ValidateEmail.validateEmail(email)) {
            interestLL.setVisibility(View.GONE);
            signUpLL.setVisibility(View.VISIBLE);
            edittextEmail.setError("Enter valid email");
            return;
        } else if (phone.equalsIgnoreCase("")) {
            interestLL.setVisibility(View.GONE);
            signUpLL.setVisibility(View.VISIBLE);
            edittextphone.setError("Enter phone");
            return;
        } else if (city.equalsIgnoreCase("")) {
            interestLL.setVisibility(View.GONE);
            signUpLL.setVisibility(View.VISIBLE);
            editCity.setError("Enter phone");
            return;
        } else if (mGender.getSelectedItemPosition() == 0) {
            interestLL.setVisibility(View.GONE);
            signUpLL.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return;
        } else if (country.equalsIgnoreCase("")) {
            interestLL.setVisibility(View.GONE);
            signUpLL.setVisibility(View.VISIBLE);
//            editCountry.setError("Enter country");
            ToastHelper.showToast(this, "Please select country.");
            return;
        } else if (password.equalsIgnoreCase("")) {
            interestLL.setVisibility(View.GONE);
            signUpLL.setVisibility(View.VISIBLE);
            editpassword.setError("Enter password");
            return;
        } else if (confirmpassword.equalsIgnoreCase("")) {
            interestLL.setVisibility(View.GONE);
            signUpLL.setVisibility(View.VISIBLE);
            editConfirmpassword.setError("Enter confirm password");
            return;
        } else if (!confirmpassword.equalsIgnoreCase(password)) {
            interestLL.setVisibility(View.GONE);
            signUpLL.setVisibility(View.VISIBLE);
            editConfirmpassword.setError("Enter correct password");
            return;
        } else {
            HashMap<String, String> allHashMap = new HashMap<>();
            allHashMap.put("fname", fname);
            allHashMap.put("lname", lname);
            allHashMap.put("dob", dob);
            allHashMap.put("email", email);
            allHashMap.put("gender", gender);
            allHashMap.put("phone", phone);
            allHashMap.put("country", country);
            allHashMap.put("city", city);
            allHashMap.put("country_code", country_code);
            allHashMap.put("country_code_name", country_code_name);
            allHashMap.put("password", password);
            allHashMap.put("confirmpassword", confirmpassword);
            allHashMap.put("device_type", "1");
            allHashMap.put("church", "church");
            allHashMap.put("device_token", PersistentUser.getPushToken(mContext));
            allHashMap.put("latitude", PersistentUser.getLatitude(mContext));
            allHashMap.put("longitude", PersistentUser.getLongitude(mContext));
            for (int i = 0; i < ((Myapplication) getApplicationContext()).getInterest().size(); i++) {
                allHashMap.put("interest" + (i + 1), ((Myapplication) getApplicationContext()).getInterest().get(i));
            }
//            sigupServerRequest(allHashMap);


            Intent intent = new Intent(SignupActivity.this, OtpActivity.class);
            intent.putExtra("data", allHashMap);
            startActivity(intent);
        }

    }

    private void sigupServerRequest(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        final String phone = edittextphone.getText().toString().trim();
        final String country_code = ccp.getSelectedCountryCode().toString().trim();
        if (!allHashMap.containsKey("interest1")) {
            Toast.makeText(this, "Please select atleaset one interest.", Toast.LENGTH_SHORT).show();
            return;
        }
//        if(!allHashMap.containsKey("interest2")){
//            allHashMap.put("interest2","");
//        }
//        if(!allHashMap.containsKey("interest3")){
//            allHashMap.put("interest3","");
//        }
//        if(!allHashMap.containsKey("interest4")){
//            allHashMap.put("interest4","");
//        }
//        if(!allHashMap.containsKey("interest5")){
//            allHashMap.put("interest5","");
//        }
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

                        createUserOnFirebaseAuthentication(edittextEmail.getText().toString(), editpassword.getText().toString(), new OnUserCreatedOnFirebaseCallback() {

                            @Override
                            public void onUserCreatedSuccess(final String userUID) {

                                final Map<String, Object> userMap = new HashMap<>();
                                userMap.put("email", edittextEmail.getText().toString());
                                userMap.put("firstname", edittextFname.getText().toString());
                                userMap.put("imageurl", "");
                                userMap.put("lastname", edittextLname.getText().toString());
                                userMap.put("timestamp", new Date().getTime());
                                userMap.put("uid", userUID);

                                createUserOnContacts(userUID, userMap, new ChatSignUpActivity.OnUserCreatedOnContactsCallback() {

                                    @Override
                                    public void onUserCreatedSuccess() {
//                                        Intent intent = getIntent();
//                                        intent.putExtra(BUNDLE_SIGNED_UP_USER_EMAIL, edittextEmail.getText().toString());
//                                        intent.putExtra(BUNDLE_SIGNED_UP_USER_PASSWORD, editpassword.getText().toString());
//                                        setResult(RESULT_OK, intent);
//                                        finish();


                                        Intent mIntent = new Intent(SignupActivity.this, LoginActivity.class);
                                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(mIntent);
                                        finish();
                                    }

                                    @Override
                                    public void onUserCreatedError(Exception e) {
                                        // TODO: 04/01/18
                                        Toast.makeText(SignupActivity.this, "Saving user on contacts failed." + e,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onUserCreatedError(Exception e) {
                                // TODO: 04/01/18  string
                                Toast.makeText(SignupActivity.this, "Authentication failed." + e,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });


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

    @Override
    public void getSelectedInterest(String interest) {
        if (((Myapplication) getApplicationContext()).getInterest().contains(interest)) {
            ((Myapplication) getApplicationContext()).removeInterest(interest);
        } else if (((Myapplication) getApplicationContext()).getInterest().size() < 5) {
            ((Myapplication) getApplicationContext()).saveInterest(interest);

        } else {
            Toast.makeText(mContext, "You can select up to 5 interests only", Toast.LENGTH_SHORT).show();
        }

        selectedInterestTotalTxt.setText("show " + ((Myapplication) getApplicationContext()).getInterest().size() + "/5 selected");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ((Myapplication) getApplicationContext()).getInterest().clear();
    }

    private void createUserOnFirebaseAuthentication(final String email,
                                                    final String password,
                                                    final OnUserCreatedOnFirebaseCallback onUserCreatedOnFirebaseCallback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
//                        Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    onUserCreatedOnFirebaseCallback.onUserCreatedError(task.getException());
                                    Log.e("error.........", "error");
                                } else {
                                    // user created with success
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser(); // retrieve the created user
                                    onUserCreatedOnFirebaseCallback.onUserCreatedSuccess(firebaseUser.getUid());
                                }
                            }
                        }
                ).addOnFailureListener(SignupActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void createUserOnContacts(String key, Map<String, Object> user,
                                      final ChatSignUpActivity.OnUserCreatedOnContactsCallback onUserCreatedOnContactsCallback) {
        DatabaseReference contactsNode;
        if (StringUtils.isValid(ChatManager.Configuration.firebaseUrl)) {
            contactsNode = FirebaseDatabase.getInstance()
                    .getReferenceFromUrl(ChatManager.Configuration.firebaseUrl)
                    .child("/apps/" + ChatManager.Configuration.appId + "/contacts");
        } else {
            contactsNode = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("/apps/" + ChatManager.Configuration.appId + "/contacts");
        }

        // save the user on contacts node
        contactsNode.child(key)
                .setValue(user, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            // all gone right
                            onUserCreatedOnContactsCallback.onUserCreatedSuccess();
                        } else {
                            // errors
                            onUserCreatedOnContactsCallback.onUserCreatedError(databaseError.toException());
                        }
                    }
                });
    }

    private void signInFirebase(String email, String password) {
        Log.d(TAG, "signIn:" + email);


        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            lookUpContactById(user.getUid(), new OnUserLookUpComplete() {
                                @Override
                                public void onUserRetrievedSuccess(IChatUser loggedUser) {
                                    Log.d(TAG, "ChatLoginActivity.signInWithEmail.onUserRetrievedSuccess: loggedUser == " + loggedUser.toString());

                                    ChatManager.Configuration mChatConfiguration =
                                            new ChatManager.Configuration.Builder(ChatManager.Configuration.appId)
//                                                    .firebaseUrl(ChatManager.Configuration.firebaseUrl)
//                                                    .storageBucket(ChatManager.Configuration.storageBucket)
                                                    .build();

//                                    IChatUser iChatUser = new ChatUser();
//                                    iChatUser.setId(user.getUid());
//                                    iChatUser.setEmail(user.getEmail());

                                    ChatManager.start(SignupActivity.this, mChatConfiguration, loggedUser);
                                    Log.i(TAG, "chat has been initialized with success");

//                                    // get device token
                                    new RefreshFirebaseInstanceIdTask().execute();

                                    ChatUI.getInstance().setContext(SignupActivity.this);
                                    Log.i(TAG, "ChatUI has been initialized with success");

                                    ChatUI.getInstance().enableGroups(false);

                                    // set on new conversation click listener
                                    // final IChatUser support = new ChatUser("support", "Chat21 Support");
                                    final IChatUser support = null;
                                    ChatUI.getInstance().setOnNewConversationClickListener(new OnNewConversationClickListener() {
                                        @Override
                                        public void onNewConversationClicked() {
                                            if (support != null) {
                                                ChatUI.getInstance().openConversationMessagesActivity(support);
                                            } else {
                                                Intent intent = new Intent(getApplicationContext(),
                                                        ContactListActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // start activity from context

                                                startActivity(intent);
                                            }
                                        }
                                    });

//                                    // on attach button click listener
//                                    ChatUI.getInstance().setOnAttachClickListener(new OnAttachClickListener() {
//                                        @Override
//                                        public void onAttachClicked(Object object) {
//                                            Toast.makeText(getApplicationContext(),
//                                                    "onAttachClickListener", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//
//                                    // on create group button click listener
//                                    ChatUI.getInstance().setOnCreateGroupClickListener(new OnCreateGroupClickListener() {
//                                        @Override
//                                        public void onCreateGroupClicked() {
//                                            Toast.makeText(getApplicationContext(),
//                                                    "setOnCreateGroupClickListener", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
                                    Log.i(TAG, "ChatUI has been initialized with success");

                                    Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onUserRetrievedError(Exception e) {
                                    Log.d(TAG, "ChatLoginActivity.signInWithEmail.onUserRetrievedError: " + e.toString());
                                }
                            });

                            // enable persistence must be made before any other usage of FirebaseDatabase instance.
                            try {
                                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                            } catch (DatabaseException databaseException) {
                                Log.w(TAG, databaseException.toString());
                            } catch (Exception e) {
                                Log.w(TAG, e.toString());
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

//                            setResult(Activity.RESULT_CANCELED);
//                            finish();
//                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
//                            setResult(Activity.RESULT_CANCELED);
//                            finish();
//                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    public void lookUpContactById(String userId, final OnUserLookUpComplete onUserLookUpComplete) {


        DatabaseReference contactsNode;
        if (StringUtils.isValid(ChatManager.Configuration.firebaseUrl)) {
            contactsNode = FirebaseDatabase.getInstance()
                    .getReferenceFromUrl(ChatManager.Configuration.firebaseUrl)
                    .child("/apps/" + ChatManager.Configuration.appId + "/contacts/" + userId);
        } else {
            contactsNode = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("/apps/" + ChatManager.Configuration.appId + "/contacts/" + userId);
        }

        contactsNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(DEBUG_LOGIN, "ChatLoginActivity.lookUpContactById: dataSnapshot == " + dataSnapshot.toString());

                if (dataSnapshot.getValue() != null) {
                    try {
                        IChatUser loggedUser = ContactsSynchronizer.decodeContactSnapShop(dataSnapshot);
                        Log.d(DEBUG_LOGIN, "ChatLoginActivity.lookUpContactById.onDataChange: loggedUser == " + loggedUser.toString());
                        onUserLookUpComplete.onUserRetrievedSuccess(loggedUser);
                    } catch (ChatFieldNotFoundException e) {
                        Log.e(DEBUG_LOGIN, "ChatLoginActivity.lookUpContactById.onDataChange: " + e.toString());
                        onUserLookUpComplete.onUserRetrievedError(e);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(DEBUG_LOGIN, "ChatLoginActivity.lookUpContactById: " + databaseError.toString());
                onUserLookUpComplete.onUserRetrievedError(databaseError.toException());
            }
        });
    }


    public interface OnUserLookUpComplete {
        void onUserRetrievedSuccess(IChatUser loggedUser);

        void onUserRetrievedError(Exception e);
    }


    public interface OnUserCreatedOnFirebaseCallback {
        void onUserCreatedSuccess(String userUID);

        void onUserCreatedError(Exception e);
    }


    public interface OnUserCreatedOnContactsCallback {
        void onUserCreatedSuccess();

        void onUserCreatedError(Exception e);
    }

}
