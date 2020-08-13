package com.grace.book;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.book.adapter.MyPostListAdapter;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.chatadapter.CustomIncomingImageMessageViewHolder;
import com.grace.book.chatadapter.CustomIncomingTextMessageViewHolder;
import com.grace.book.chatadapter.CustomIncomingVideoMessageViewHolder;
import com.grace.book.chatadapter.CustomOutcomingImageMessageViewHolder;
import com.grace.book.chatadapter.CustomOutcomingTextMessageViewHolder;
import com.grace.book.chatadapter.CustomOutcomingVideoMessageViewHolder;
import com.grace.book.chatmodel.Message;
import com.grace.book.chatmodel.MessageList;
import com.grace.book.chatmodel.User;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.FeedList;
import com.grace.book.model.Usersdata;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.ConstantFunctions;
import com.grace.book.utils.DateUtility;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;
import com.grace.book.utils.ToastHelper;
import com.grace.book.utils.ValidateEmail;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.grace.book.utils.ConstantFunctions.requestOptionsRadioMatch;

public class FeedabckActivity extends AppCompatActivity implements MessageHolders.ContentChecker<Message> {
    private static final String TAG = FeedabckActivity.class.getSimpleName();
    private Context mContext;
    private BusyDialog mBusyDialog;
    final private int REQUEST_CODE_ASK_PERMISSIONS_AGENT = 100;
    List<String> permissions = new ArrayList<String>();
    private static final int CAMERA_TAKE_PHOTO = 10;
    private static final int GALLERY_TAKE_PHOTO = 12;
    private static final int GALLERY_SELECT_VIDEO = 13;
    private static final int CAMERA_SELECT_VIDEO = 14;
    private MessagesList messagesList;
    private MessagesListAdapter mMessagesListAdapter;
    private ImageLoader imageLoader;
    private String app_user_id = "";
    private String photocreateImagePath = "";
    private EditText edittextChat;
    private TextView chatuser;
    private LinearLayout fileattachment;
    private LinearLayout filesend;
    private String selectedImagePath = "";
    private static final byte CONTENT_TYPE_VOICE = 1;
    private int chat_for = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mContext = this;
        app_user_id = PersistentUser.getUserID(mContext);

        initUi();
    }

    private void initUi() {
        findViewById(R.id.layoutBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edittextChat = (EditText) this.findViewById(R.id.edittextChat);
        messagesList = (MessagesList) this.findViewById(R.id.messagesList);
        fileattachment = (LinearLayout) this.findViewById(R.id.fileattachment);
        filesend = (LinearLayout) this.findViewById(R.id.filesend);
        edittextChat.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(final ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Glide.with(FeedabckActivity.this)
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
                .registerContentType(
                        CONTENT_TYPE_VOICE,
                        CustomIncomingVideoMessageViewHolder.class,
                        R.layout.item_custom_incoming_video_message,
                        CustomOutcomingVideoMessageViewHolder.class,
                        R.layout.item_custom_outcoming_video_message,
                        this)
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
                hideSoftKeyboard(FeedabckActivity.this);
                // popupMenu();

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
                        mJsonObject.put("receiver_id", "1");
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
                    hideSoftKeyboard(FeedabckActivity.this);
                }
            }
        });
        messagesList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(FeedabckActivity.this);
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
        allHashMap.put("receiver_id", "1");
        allHashMap.put("limit", "" + limit);
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));

        final String url = AllUrls.BASEURL + "loadadminmessagelist";
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
                            Log.e("mServermessage", mServermessage.getType());
                            long time = DateUtility.dateToMillisecond(mServermessage.getDuration());
                            String senderid = mServermessage.getSender_id();
                            if (mServermessage.getType().equalsIgnoreCase("0")) {
                                messagesList.add(addMessage(senderid, mServermessage.getId(), mServermessage.getMessage(), "" + time));
                            } else if (mServermessage.getType().equalsIgnoreCase("1")) {
                                messagesList.add(getImageMessage(senderid, mServermessage.getId(), mServermessage.getMessage(), mServermessage.getImagepath(), "" + time));
                            } else if (mServermessage.getType().equalsIgnoreCase("2")) {
                                messagesList.add(getVideoMessage(senderid, mServermessage.getId(), mServermessage.getMessage(), mServermessage.getImagepath(), "" + time));
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

    public Message getVideoMessage(String userId, String messageId, String text, String imagefile, String durarion) {

        Log.e("imagefile", imagefile);
        User user = new User(userId, "Tobi", null, true);
        long totaldurarion = Long.parseLong(durarion);
        Message message = new Message(messageId, user, null, new Date(totaldurarion));
        message.setVoice(new Message.Voice(imagefile, 0));
        message.setText(text);
        return message;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void serverdRequestForMessage(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));

        final String url = AllUrls.BASEURL + "sendadminMessage";
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

    @Override
    public boolean hasContentFor(Message message, byte type) {
        switch (type) {
            case CONTENT_TYPE_VOICE:
                return message.getVoice() != null
                        && message.getVoice().getUrl() != null
                        && !message.getVoice().getUrl().isEmpty();
        }
        return false;
    }

}
