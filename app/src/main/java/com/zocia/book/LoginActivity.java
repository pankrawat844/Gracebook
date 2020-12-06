package com.zocia.book;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.BusyDialog;
import com.zocia.book.utils.Helpers;
import com.zocia.book.utils.Logger;
import com.zocia.book.utils.PersistentUser;
import com.zocia.book.utils.ToastHelper;
import com.zocia.book.utils.ValidateEmail;

import org.chat21.android.core.ChatManager;
import org.chat21.android.core.authentication.task.RefreshFirebaseInstanceIdTask;
import org.chat21.android.core.exception.ChatFieldNotFoundException;
import org.chat21.android.core.users.models.ChatUser;
import org.chat21.android.core.users.models.IChatUser;
import org.chat21.android.ui.ChatUI;
import org.chat21.android.ui.contacts.activites.ContactListActivity;
import org.chat21.android.ui.conversations.listeners.OnNewConversationClickListener;
import org.chat21.android.ui.login.activities.ChatLoginActivity;
import org.chat21.android.utils.StringUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static org.chat21.android.utils.DebugConstants.DEBUG_LOGIN;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Context mContext;
    private BusyDialog mBusyDialog;
    private EditText editpassword;
    private EditText edittextEMail;
    private FirebaseAuth mAuth;

    private static IChatUser decodeContactSnapShop(DataSnapshot dataSnapshot) throws ChatFieldNotFoundException {
        Log.v(TAG, "decodeContactSnapShop called");

        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

//        String contactId = dataSnapshot.getKey();

        String uid = (String) map.get("uid");
        if (uid == null) {
            throw new ChatFieldNotFoundException("Required uid field is null for contact id : " + uid);
        }

//        String timestamp = (String) map.get("timestamp");

        String lastname = (String) map.get("lastname");
        String firstname = (String) map.get("firstname");
        String imageurl = (String) map.get("imageurl");
        String email = (String) map.get("email");


        IChatUser contact = new ChatUser();
        contact.setId(uid);
        contact.setEmail(email);
        contact.setFullName(firstname + " " + lastname);
        contact.setProfilePictureUrl(imageurl);

        Log.v(TAG, "decodeContactSnapShop.contact : " + contact);

        return contact;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        mAuth = FirebaseAuth.getInstance();

//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            return;
//                        }
//                        String token = task.getResult().getToken();
//                        PersistentUser.setPushToken(mContext, token);
//                        Log.e("token", token);
//                    }
//                });
//        Log.e("token", FirebaseInstanceId.getInstance().getToken());
        PersistentUser.setPushToken(mContext, FirebaseInstanceId.getInstance().getToken());

        initUi();
    }

    public void vaidation() {
        String email = edittextEMail.getText().toString().trim();
        String password = editpassword.getText().toString().trim();

        if (email.equalsIgnoreCase("")) {
            edittextEMail.setError("Enter Email");
            return;
        } else if (!ValidateEmail.validateEmail(email)) {
            edittextEMail.setError("Enter valid Email");
            return;
        } else if (password.equalsIgnoreCase("")) {
            editpassword.setError("Enter confirm password");
            return;
        } else {
            HashMap<String, String> allHashMap = new HashMap<>();
            allHashMap.put("email", email);
            allHashMap.put("password", password);
            allHashMap.put("device_type", "1");
            allHashMap.put("device_token", PersistentUser.getPushToken(mContext));
            sigupServerRequest(allHashMap);
        }

    }

    private void initUi() {
        edittextEMail = (EditText) this.findViewById(R.id.edittextEMail);
        editpassword = (EditText) this.findViewById(R.id.editpassword);

        findViewById(R.id.tvForgotpassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForVideo();
            }
        });
        findViewById(R.id.tvLoginSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vaidation();
            }
        });
        findViewById(R.id.tvSignUpSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
//        edittextEMail.setText("prosanto.mbstu@gmail.com");
//        editpassword.setText("123");

    }

    AlertDialog alertDialog;

    public void showDialogForVideo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.dialog_forgotpassword, null);
        builder.setView(mView);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
        final EditText edittextphone = (EditText) mView.findViewById(R.id.edittextphone);
        final TextView Submit_btn = (TextView) mView.findViewById(R.id.Submit_btn);

        Submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                String email_address = edittextphone.getText().toString();

                if (email_address.equalsIgnoreCase("")) {
                    ToastHelper.showToast(mContext, "Please enter email address");
                    return;
                } else if (!ValidateEmail.validateEmail(email_address)) {
                    ToastHelper.showToast(mContext, "Please enter valid email address");
                    return;
                } else {
                    HashMap<String, String> allHashMap = new HashMap<>();
                    allHashMap.put("email_address", email_address);
                    forgorpasswordServerRequest(allHashMap);
                }

            }
        });
    }

    private void sigupServerRequest(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        final String url = AllUrls.BASEURL + "login";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        JSONObject data = mJsonObject.getJSONObject("data");
                        String is_verify = data.getString("is_verify");
                        if (is_verify.equalsIgnoreCase("1")) {
                            String userId = data.getString("id");
                            String auth_token = data.getString("auth_token");
                            PersistentUser.setUserID(mContext, userId);
                            PersistentUser.setUserToken(mContext, auth_token);
                            PersistentUser.setLogin(mContext);
                            PersistentUser.setUserDetails(mContext, responseServer);
                            signInFirebase(edittextEMail.getText().toString(), editpassword.getText().toString());
                        } else {
                            String message = "Your account not varification.";
                            ToastHelper.showToast(mContext, message);
                            String otp_code = data.getString("otp_code");
                            String phone = data.getString("phone");
                            String country_code = data.getString("country_code");
                            Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                            intent.putExtra("screen", 0);
                            intent.putExtra("otp_code", otp_code);
                            intent.putExtra("phone", phone);
                            intent.putExtra("country_code", country_code);
                            startActivity(intent);
                        }

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

    private void forgorpasswordServerRequest(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        final String url = AllUrls.BASEURL + "resendCode";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {

                        ToastHelper.showToast(mContext,"Please check your email.we send a link for reset password");

//                        JSONObject data = mJsonObject.getJSONObject("data");
//                        String otp_code = data.getString("otp_code");
//                        String phone = data.getString("phone");
//                        String country_code = data.getString("country_code");
//                        PersistentUser.setUserDetails(mContext, responseServer);
//                        Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
//                        intent.putExtra("screen", 1);
//                        intent.putExtra("otp_code", otp_code);
//                        intent.putExtra("phone", phone);
//                        intent.putExtra("country_code", country_code);
//                        startActivity(intent);

                    } else {
                        String message = mJsonObject.getString("message");
                        ToastHelper.showToast(mContext, message);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

                            lookUpContactById(user.getUid(), new ChatLoginActivity.OnUserLookUpComplete() {
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

                                    ChatManager.start(LoginActivity.this, mChatConfiguration, loggedUser);
                                    Log.i(TAG, "chat has been initialized with success");

//                                    // get device token
                                    new RefreshFirebaseInstanceIdTask().execute();

                                    ChatUI.getInstance().setContext(LoginActivity.this);
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

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
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

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
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

    public void lookUpContactById(String userId, final ChatLoginActivity.OnUserLookUpComplete onUserLookUpComplete) {


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
                        IChatUser loggedUser = decodeContactSnapShop(dataSnapshot);
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
}
