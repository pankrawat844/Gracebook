package com.grace.book;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.chatadapter.CustomIncomingImageMessageViewHolder;
import com.grace.book.chatadapter.CustomIncomingTextMessageViewHolder;
import com.grace.book.chatadapter.CustomOutcomingImageMessageViewHolder;
import com.grace.book.chatadapter.CustomOutcomingTextMessageViewHolder;
import com.grace.book.chatmodel.Message;
import com.grace.book.chatmodel.MessageList;
import com.grace.book.chatmodel.User;
import com.grace.book.model.GroupList;
import com.grace.book.model.Usersdata;
import com.grace.book.myapplication.Myapplication;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.networkcalls.VolleyMultipartRequest;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.ConstantFunctions;
import com.grace.book.utils.DateUtility;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.ImageFilePath;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;
import com.grace.book.utils.ToastHelper;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;

import static com.grace.book.utils.ConstantFunctions.requestOptionsRadioMatch;


public class ChatActivity extends AppCompatActivity {
    private final String TAG = ChatActivity.class.getSimpleName();
    final private int REQUEST_CODE_ASK_PERMISSIONS_AGENT = 100;
    List<String> permissions = new ArrayList<String>();
    private static final int CAMERA_TAKE_PHOTO = 10;
    private static final int GALLERY_TAKE_PHOTO = 12;
    private MessagesList messagesList;
    private MessagesListAdapter mMessagesListAdapter;
    private ImageLoader imageLoader;
    private Context mContext;
    private String app_user_id = "";
    private String photocreateImagePath = "";
    private EditText edittextChat;
    private Usersdata mUsersdata;
    private TextView chatuser;
    private BusyDialog mBusyDialog;
    private LinearLayout fileattachment;
    private LinearLayout filesend;
    private String selectedImagePath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        mContext = this;
        Bundle extra = getIntent().getBundleExtra("extra");
        mUsersdata = (Usersdata) extra.getSerializable("objects");
        mContext = this;
        app_user_id = PersistentUser.getUserID(mContext);
        initui();
    }

    public void initui() {
        chatuser = (TextView) this.findViewById(R.id.chatuser);
        findViewById(R.id.layoutBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        chatuser.setText(mUsersdata.getFname() + " " + mUsersdata.getLname());
        edittextChat = (EditText) this.findViewById(R.id.edittextChat);
        messagesList = (MessagesList) this.findViewById(R.id.messagesList);
        fileattachment = (LinearLayout) this.findViewById(R.id.fileattachment);
        filesend = (LinearLayout) this.findViewById(R.id.filesend);


        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(final ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Glide.with(ChatActivity.this)
                        .asBitmap()
                        .load(url)
                        .apply(requestOptionsRadioMatch)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                imageView.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }
        };
        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(
                        CustomIncomingTextMessageViewHolder.class,
                        R.layout.item_custom_incoming_text_message)
                .setOutcomingTextConfig(
                        CustomOutcomingTextMessageViewHolder.class,
                        R.layout.item_custom_outcoming_text_message)
                .setIncomingImageConfig(
                        CustomIncomingImageMessageViewHolder.class,
                        R.layout.item_custom_incoming_image_message)
                .setOutcomingImageConfig(
                        CustomOutcomingImageMessageViewHolder.class,
                        R.layout.item_custom_outcoming_image_message);
        mMessagesListAdapter = new MessagesListAdapter<>(app_user_id, holdersConfig, imageLoader);
        messagesList.setAdapter(mMessagesListAdapter);

        fileattachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(ChatActivity.this);
                checkFileUploadPermissions();
            }
        });
        filesend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edittextChat.getText().toString();
                if (text.length() > 0) {
                    try {
                        selectedImagePath = "";
                        edittextChat.setText("");
                        HashMap<String, String> mJsonObject = new HashMap<>();
                        mJsonObject.put("type", "0");
                        //mJsonObject.put("message", URLEncoder.encode(text, "UTF-8"));
                        mJsonObject.put("message", text);
                        mJsonObject.put("duration", DateUtility.getCurrentTimeForsend());
                        mJsonObject.put("receiver_id", mUsersdata.getId());
                        serverdRequestForMessage(mJsonObject);

                    } catch (Exception x) {
                    }

                }
            }
        });

        mMessagesListAdapter.setLoadMoreListener(new MessagesListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (totalItemsCount >= 20) {
                    // serverdTOpRequest(totalItemsCount);
                }
            }
        });
        mMessagesListAdapter.setLoadMoreListener(new MessagesListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (totalItemsCount >= 20) {
                    serverdRequest(totalItemsCount);
                }
            }
        });

        edittextChat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideSoftKeyboard(ChatActivity.this);
                }
            }
        });
        messagesList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(ChatActivity.this);
                return false;
            }
        });

        serverdRequest(0);

    }

    private void serverdRequest(int limit) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("receiver_id", mUsersdata.getId());
        allHashMap.put("limit", "" + limit);
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));

        final String url = AllUrls.BASEURL + "loadmessagelist";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {

                        JSONArray jsonArray = mJsonObject.getJSONArray("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        List<MessageList> posts = new ArrayList<MessageList>();
                        posts = Arrays.asList(mGson.fromJson(jsonArray.toString(), MessageList[].class));
                        ArrayList<MessageList> allLists = new ArrayList<MessageList>(posts);
                        List<Message> messagesList = new ArrayList<>();
                        for (MessageList mServermessage : allLists) {
                            long time = DateUtility.dateToMillisecond(mServermessage.getDuration());
                            String senderid = mServermessage.getSender_id();

                            if (mServermessage.getType().equalsIgnoreCase("0")) {
                                messagesList.add(addMessage(senderid, mServermessage.getId(), mServermessage.getMessage(), "" + time));
                            } else {
                                messagesList.add(getImageMessage(senderid, mServermessage.getId(), mServermessage.getMessage(), mServermessage.getImagepath(), "" + time));
                            }
                        }
                        mMessagesListAdapter.addToEnd(messagesList, false);

                    }


                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                if (statusCode.equalsIgnoreCase("404")) {
                    PersistentUser.resetAllData(mContext);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

    private void serverdRequestForMessage(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));

        final String url = AllUrls.BASEURL + "sendMessage";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        JSONObject result = mJsonObject.getJSONObject("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        MessageList mServermessage = (MessageList) mGson.fromJson(result.toString(), MessageList.class);
                        long time = DateUtility.dateToMillisecond(mServermessage.getDuration());
                        String senderid = mServermessage.getSender_id();
                        if (mServermessage.getType().equalsIgnoreCase("0")) {
                            mMessagesListAdapter.addToStart(addMessage(senderid, mServermessage.getId(), mServermessage.getMessage(), "" + time), true);

                        } else {
                            mMessagesListAdapter.addToStart(getImageMessage(senderid, mServermessage.getId(), mServermessage.getMessage(), mServermessage.getImagepath(), "" + time), true);

                        }

                    }


                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                if (statusCode.equalsIgnoreCase("404")) {
                    PersistentUser.resetAllData(mContext);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

    public void sendServerRequest(HashMap<String, String> allHashMap) {

        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }

        Log.e("allHashMap", allHashMap.toString());
        HashMap<String, String> headerParams = new HashMap<>();
        headerParams.put("appKey", AllUrls.APP_KEY);
        headerParams.put("authToken", PersistentUser.getUserToken(mContext));
        String url = AllUrls.BASEURL + "sendMessage";
        Map<String, VolleyMultipartRequest.DataPart> ByteData = new HashMap<>();

        if (!selectedImagePath.equalsIgnoreCase("")) {
            mBusyDialog = new BusyDialog(mContext);
            mBusyDialog.show();
            String[] tokens = selectedImagePath.split("[\\\\|/]");
            String fileName = tokens[tokens.length - 1];
            byte[] data = ImageFilePath.readBytesFromFile(selectedImagePath);
            ByteData.put("file", new VolleyMultipartRequest.DataPart(fileName, data));

        }
        ServerCallsProvider.VolleyMultipartRequest(url, allHashMap, headerParams, ByteData, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                if (mBusyDialog != null)
                    mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        JSONObject result = mJsonObject.getJSONObject("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        MessageList mServermessage = (MessageList) mGson.fromJson(result.toString(), MessageList.class);
                        long time = DateUtility.dateToMillisecond(mServermessage.getDuration());
                        String senderid = mServermessage.getSender_id();
                        if (mServermessage.getType().equalsIgnoreCase("0")) {
                            mMessagesListAdapter.addToStart(addMessage(senderid, mServermessage.getId(), mServermessage.getMessage(), "" + time), true);

                        } else {
                            mMessagesListAdapter.addToStart(getImageMessage(senderid, mServermessage.getId(), mServermessage.getMessage(), mServermessage.getImagepath(), "" + time), true);

                        }


                    }


                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                if (mBusyDialog != null)
                    mBusyDialog.dismis();

                if (statusCode.equalsIgnoreCase("404")) {
                    PersistentUser.resetAllData(mContext);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private Message addMessage(String userId, String messageId, String text, String durarion) {
        User user = new User(userId, "Tobi", null, true);
        long totaldurarion = Long.parseLong(durarion);
        return new Message(messageId, user, text, new Date(totaldurarion));
    }

    public Message getImageMessage(String userId, String messageId, String text, String imagefile, String durarion) {
        User user = new User(userId, "Tobi", null, true);
        long totaldurarion = Long.parseLong(durarion);
        Message message = new Message(messageId, user, null, new Date(totaldurarion));
        message.setImage(new Message.Image(imagefile));
        message.setText(text);
        return message;
    }


    public void checkFileUploadPermissions() {
        permissions.clear();
        if (Build.VERSION.SDK_INT > 22) {
            String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            int hasstoragePermission = checkSelfPermission(storagePermission);
            if (hasstoragePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(storagePermission);
            }
            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
                requestPermissions(params, REQUEST_CODE_ASK_PERMISSIONS_AGENT);
            } else {
                showDialogForVideo();
            }
        } else
            showDialogForVideo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_AGENT:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        showDialogForVideo();
                    }
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void showDialogForVideo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.dialog_photoselction, null);
        builder.setView(mView);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
        final LinearLayout Camera = (LinearLayout) mView.findViewById(R.id.layout_Camera);
        final LinearLayout layout_Gellery = (LinearLayout) mView.findViewById(R.id.layout_Gellery);
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                pickFromCamera();

            }
        });
        layout_Gellery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                pickFromGallery();

            }
        });
    }

    public void pickFromCamera() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new java.util.Date());
        String imageFileName = timeStamp + "_";
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            photocreateImagePath = image.getAbsolutePath();
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoURI = FileProvider.getUriForFile(mContext,
                    BuildConfig.APPLICATION_ID + ".provider", image);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(cameraIntent, CAMERA_TAKE_PHOTO);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pickFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_TAKE_PHOTO) {
                if (null == data)
                    return;
                Uri selectedImageUri = data.getData();
                String selectedImagePath2 = ImageFilePath.getPath(mContext, selectedImageUri);
                File file = new File(selectedImagePath2);
                try {
                    File compressedImage = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToFile(file);
                    selectedImagePath = compressedImage.getAbsolutePath();
                    edittextChat.setText("");
                    HashMap<String, String> mJsonObject = new HashMap<>();
                    mJsonObject.put("type", "1");
                    mJsonObject.put("message", "");
                    mJsonObject.put("duration", DateUtility.getCurrentTimeForsend());
                    mJsonObject.put("receiver_id", mUsersdata.getId());
                    sendServerRequest(mJsonObject);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (requestCode == CAMERA_TAKE_PHOTO) {
                File file = new File(photocreateImagePath);
                try {
                    File compressedImage = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToFile(file);

                    selectedImagePath = compressedImage.getAbsolutePath();
                    edittextChat.setText("");
                    HashMap<String, String> mJsonObject = new HashMap<>();
                    mJsonObject.put("type", "1");
                    mJsonObject.put("message", "");
                    mJsonObject.put("duration", DateUtility.getCurrentTimeForsend());
                    mJsonObject.put("receiver_id", mUsersdata.getId());
                    sendServerRequest(mJsonObject);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Myapplication.NEW_MESSAGE_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Myapplication.NEW_MESSAGE_ACTION.equals(action)) {
                Bundle extra = intent.getBundleExtra("extra");
                MessageList mServermessage = (MessageList) extra.getSerializable("objects");
                long time = DateUtility.dateToMillisecond(mServermessage.getDuration());
                String senderid = mServermessage.getSender_id();
                if (senderid.equalsIgnoreCase(mUsersdata.getId())) {
                    if (mServermessage.getType().equalsIgnoreCase("0")) {
                        mMessagesListAdapter.addToStart(addMessage(senderid, mServermessage.getId(), mServermessage.getMessage(), "" + time), true);

                    } else {
                        mMessagesListAdapter.addToStart(getImageMessage(senderid, mServermessage.getId(), mServermessage.getMessage(), mServermessage.getImagepath(), "" + time), true);

                    }
                } else {
                    String text = "New message";
                    String message = "";
                    if (mServermessage.getType().equalsIgnoreCase("0"))
                        message = mServermessage.getMessage();
                    else
                        message = "Send a media";
                    sendNotificationForcard(text, message);

                }

            }
        }
    };

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void sendNotificationForcard(String message, String title) {
        String channelId = "Gracebook";
        String channelName = "Mzadcom";
        Intent resultIntent = new Intent(this, SplashActivity.class);
        resultIntent.putExtra("push_message", message);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.ic_stat_tab_app_icon)
                .setContentTitle(title)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.ic_stat_tab_app_icon);
            mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            mBuilder.setSmallIcon(R.drawable.ic_stat_tab_app_icon);
        }
        Notification notification = mBuilder.build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mChannel.enableVibration(true);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(0, notification);
    }


}
