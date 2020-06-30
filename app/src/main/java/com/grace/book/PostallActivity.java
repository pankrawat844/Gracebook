package com.grace.book;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.networkcalls.VolleyMultipartRequest;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.DateUtility;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.ImageFilePath;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;
import com.grace.book.utils.ToastHelper;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.grace.book.utils.ConstantFunctions.requestOptionsForRadious;

public class PostallActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private Context mContext;
    private TextView prayerTitel;
    private int screenType = 0;
    private BusyDialog mBusyDialog;
    private String selectedImagePath = "";
    private EditText edittextChat;
    final private int REQUEST_CODE_ASK_PERMISSIONS_AGENT = 100;
    private static final int CAMERA_TAKE_PHOTO = 10;
    private static final int GALLERY_TAKE_PHOTO = 12;
    private ImageView imageImages;
    private ImageView deletefile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_prayer);
        screenType = getIntent().getIntExtra("screenType", 0);
        mContext = this;
        initUi();
    }

    private void initUi() {
        findViewById(R.id.layoutBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        deletefile = (ImageView) this.findViewById(R.id.deletefile);
        imageImages = (ImageView) this.findViewById(R.id.imageImages);
        edittextChat = (EditText) this.findViewById(R.id.edittextChat);
        prayerTitel = (TextView) this.findViewById(R.id.prayerTitel);
        if (screenType == 0) {
            prayerTitel.setText("Create your post");
        } else {
            prayerTitel.setText("Create prayer request");
        }
        findViewById(R.id.postView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
        findViewById(R.id.addPostImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
        deletefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void validation() {
        String message = edittextChat.getText().toString();
        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("details", message);
        allHashMap.put("is_user", "1");
        allHashMap.put("post_time", DateUtility.getCurrentTimeForsend());
        allHashMap.put("post_type", "0");
        serverRequest(allHashMap);

    }

    public void serverRequest(HashMap<String, String> allHashMap) {

        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }

        HashMap<String, String> headerParams = new HashMap<>();
        headerParams.put("appKey", AllUrls.APP_KEY);
        headerParams.put("authToken", PersistentUser.getUserToken(mContext));
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        String url = AllUrls.BASEURL + "addpost";
        Map<String, VolleyMultipartRequest.DataPart> ByteData = new HashMap<>();

        if (!selectedImagePath.equalsIgnoreCase("")) {
            String[] tokens = selectedImagePath.split("[\\\\|/]");
            String fileName = tokens[tokens.length - 1];
            byte[] data = ImageFilePath.readBytesFromFile(selectedImagePath);
            ByteData.put("profile_pic", new VolleyMultipartRequest.DataPart(fileName, data));

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
                        ToastHelper.showToast(mContext, "Post add successfully");


                    }

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("onFailed", serverResponse);


                } catch (Exception ex) {

                }
            }
        });

    }

    List<String> permissions = new ArrayList<String>();
    public void checkFileUploadPermissions() {
        permissions.clear();
        if (Build.VERSION.SDK_INT > 22) {
            String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            String readstoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
            //String cameraPermission = Manifest.permission.CAMERA;

            int hasstoragePermission = checkSelfPermission(storagePermission);
            int readhasstoragePermission = checkSelfPermission(readstoragePermission);
            //int hasCameraPermission = checkSelfPermission(cameraPermission);

            if (hasstoragePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(storagePermission);
            }
            if (readhasstoragePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(readstoragePermission);
            }

//            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
//                permissions.add(cameraPermission);
//            }

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
        ToastHelper.showToast(mContext, "aasd");
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
            PersistentUser.setImagePath(mContext, image.getAbsolutePath());
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
                selectedImagePath = ImageFilePath.getPath(mContext, selectedImageUri);

                Logger.debugLog("selectedImagePath", "are" + selectedImagePath);
                Glide.with(mContext)
                        .load(new File(selectedImagePath))
                        .apply(requestOptionsForRadious)
                        .circleCrop()
                        .error(R.drawable.user_icon)
                        .placeholder(R.drawable.user_icon)
                        .into(imageImages);

            } else if (requestCode == CAMERA_TAKE_PHOTO) {
                File file = new File(PersistentUser.getImagePath(mContext));
                final Uri selectedImageUri = Uri.fromFile(file);
                selectedImagePath = ImageFilePath.getPath(mContext, selectedImageUri);
                Glide.with(mContext)
                        .load(new File(selectedImagePath))
                        .apply(requestOptionsForRadious)
                        .circleCrop()
                        .error(R.drawable.user_icon)
                        .placeholder(R.drawable.user_icon)
                        .into(imageImages);

            }
        }
    }


}
