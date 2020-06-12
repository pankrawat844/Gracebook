package com.grace.book;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.grace.book.chatmodel.User;
import com.grace.book.myapplication.Myapplication;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.ConstantFunctions;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.ImageFilePath;
import com.grace.book.utils.Logger;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        mContext = this;
        initui();
    }

    public void initui() {
        findViewById(R.id.layoutBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edittextChat = (EditText) this.findViewById(R.id.edittextChat);
        messagesList = (MessagesList) this.findViewById(R.id.messagesList);
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
        mMessagesListAdapter.setLoadMoreListener(new MessagesListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (totalItemsCount >= 20) {
                    // serverdTOpRequest(totalItemsCount);
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


    }

    private void serverdRequest(int limit) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("userid", ConstantFunctions.getDeviceIMEI());
        allHashMap.put("limit", "" + limit);
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        final String url = AllUrls.BASEURL + "loadmessage.php";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getString("Type").equalsIgnoreCase("OK")) {
                        JSONArray result = mJsonObject.getJSONArray("Data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
//                        List<Servermessage> posts = new ArrayList<Servermessage>();
//                        posts = Arrays.asList(mGson.fromJson(result.toString(), Servermessage[].class));
//                        ArrayList<Servermessage> allProductList = new ArrayList<Servermessage>(posts);
//                        List<Message> messagesList = new ArrayList<>();
//
//                        for (Servermessage mServermessage : allProductList) {
//                            long time = DateUtility.dateToMillisecond(mServermessage.getDate());
//                            String senderid = mServermessage.getSenderid();
//                            if (senderid.equalsIgnoreCase(ConstantFunctions.getDeviceIMEI()))
//                                app_user_id = senderid;
//                            else
//                                app_user_id = "admin";
//
//                            if (mServermessage.getIsimage().equalsIgnoreCase("1")) {
//                                messagesList.add(getImageMessage(app_user_id, mServermessage.getMessageid(), mServermessage.getText(), mServermessage.getImage(), "" + time));
//                            } else {
//                                messagesList.add(addMessage(app_user_id, mServermessage.getMessageid(), mServermessage.getText(), "" + time));
//                            }
//                        }
//                        adapter.addToEnd(messagesList, false);

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                Logger.debugLog("onFailed", "are" + serverResponse);

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
                String selectedImagePath = ImageFilePath.getPath(mContext, selectedImageUri);
                File file = new File(selectedImagePath);
                try {
                    File compressedImage = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToFile(file);

                    //multipartDataInfoForProfile(compressedImage.getAbsolutePath());
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
                    //multipartDataInfoForProfile(compressedImage.getAbsolutePath());

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

//                Bundle extra = intent.getBundleExtra("extra");
//                Servermessage mServermessage = (Servermessage) extra.getSerializable("objects");
//                long time = DateUtility.dateToMillisecond(mServermessage.getDate());
//                String senderid = mServermessage.getSenderid();
//                if (senderid.equalsIgnoreCase(ConstantFunctions.getDeviceIMEI()))
//                    app_user_id = senderid;
//                else
//                    app_user_id = "admin";
//                if (mServermessage.getIsimage().equalsIgnoreCase("1")) {
//                    adapter.addToStart(getImageMessage(app_user_id, mServermessage.getMessageid(), mServermessage.getText(), mServermessage.getImage(), "" + time), true);
//                } else {
//                    adapter.addToStart(addMessage(app_user_id, mServermessage.getMessageid(), mServermessage.getText(), "" + time), true);
//                }

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


}
