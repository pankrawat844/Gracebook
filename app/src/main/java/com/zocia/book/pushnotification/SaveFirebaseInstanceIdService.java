package com.zocia.book.pushnotification;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.zocia.book.utils.PersistentUser;

import org.chat21.android.core.ChatManager;
import org.chat21.android.utils.ChatUtils;
import org.chat21.android.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static org.chat21.android.utils.DebugConstants.DEBUG_LOGIN;

/*
 * Created by stefanodp91 on 15/01/18.
 */

//https://github.com/MahmoudAlyuDeen/FirebaseIM/blob/master/app/src/main/java/afterapps/com/firebaseim/login/LoginActivity.java
public class SaveFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG_TOKEN = "TAG_TOKEN";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Log.d(DEBUG_LOGIN, "SaveFirebaseInstanceIdService.onTokenRefresh");

        String token = FirebaseInstanceId.getInstance().getToken();
        PersistentUser.setPushToken(getApplicationContext(), token);
    }
}
