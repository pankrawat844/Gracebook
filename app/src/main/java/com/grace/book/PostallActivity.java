package com.grace.book;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;

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
    private static final int GALLERY_SELECT_VIDEO = 13;
    private static final int CAMERA_SELECT_VIDEO = 14;
    private static final int GALLERY_SELECT_AUDIO = 15;
    private static final int CAMERA_SELECT_AUDIO = 16;

    private ImageView imageImages;
    private ImageView deletefile;
    private String post_type = "0";
    private int post_for = 0;
    private String group_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_prayer);
        screenType = getIntent().getIntExtra("screenType", 0);
        if (getIntent().hasExtra("group_id")) {
            group_id = getIntent().getStringExtra("group_id");
        }
        mContext = this;
//        CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .start(this);
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
                post_for = 1;
                checkFileUploadPermissions();
            }
        });
        findViewById(R.id.postfor_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_for = 2;
                checkFileUploadPermissions();
            }
        });
        findViewById(R.id.postfor_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_for = 3;
                checkFileUploadPermissions();
            }
        });
        deletefile.setVisibility(View.GONE);
        deletefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletefile.setVisibility(View.GONE);
                post_type = "0";
                selectedImagePath = "";
                Glide.with(mContext)
                        .load(new File(selectedImagePath))
                        .apply(requestOptionsForRadious)
                        .into(imageImages);
            }
        });
    }

    public void validation() {
        String message = edittextChat.getText().toString();
        if (message.equalsIgnoreCase("")) {
            ToastHelper.showToast(mContext, "Write your comment");
        } else {
            HashMap<String, String> allHashMap = new HashMap<>();
            allHashMap.put("details", message);
            allHashMap.put("is_user", "1");
            allHashMap.put("post_time", DateUtility.getCurrentTimeForsend());
            allHashMap.put("post_type", post_type);
            allHashMap.put("group_id", group_id);

            serverRequest(allHashMap);
        }


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

        if (screenType == 1)
            url = AllUrls.BASEURL + "addpost";
        else if (screenType == 2)
            url = AllUrls.BASEURL + "prayerAdd";
        else if (screenType == 3)
            url = AllUrls.BASEURL + "GroupPostAdd";

        Map<String, VolleyMultipartRequest.DataPart> ByteData = new HashMap<>();

        if (!selectedImagePath.equalsIgnoreCase("")) {
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
                        if (screenType == 1)
                            ToastHelper.showToast(mContext, "Post add successfully");
                        else if (screenType == 2)
                            ToastHelper.showToast(mContext, "Prayer request add successfully");
                        else if (screenType == 3)
                            ToastHelper.showToast(mContext, "Post add successfully");

                        Intent mIntent = getIntent();
                        setResult(RESULT_OK, mIntent);
                        finish();

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
            int hasstoragePermission = checkSelfPermission(storagePermission);
            int readhasstoragePermission = checkSelfPermission(readstoragePermission);

            if (hasstoragePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(storagePermission);
            }
            if (readhasstoragePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(readstoragePermission);
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
                if (post_for == 1) {
                    pickFromCamera();
                } else if (post_for == 2) {
                    pickFromCameraforVideo();
                } else if (post_for == 3) {
                    pickFromCameraforAudio();
                }

            }
        });
        layout_Gellery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (post_for == 1) {
                    pickFromGallery();
                } else if (post_for == 2) {
                    pickFromGalleryVideo();
                } else if (post_for == 3) {
                    pickFromGalleryAudio();
                }


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

    public void pickFromGalleryVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_SELECT_VIDEO);
    }

    public void pickFromCameraforVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Environment.getExternalStorageDirectory().getPath() + "videocapture_example.mp4");
        startActivityForResult(takeVideoIntent, CAMERA_SELECT_VIDEO);
    }


    public void pickFromGalleryAudio() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, GALLERY_SELECT_AUDIO);
    }

    public void pickFromCameraforAudio() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, CAMERA_SELECT_AUDIO);
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
                final Uri selectedImageUriCrop = Uri.fromFile(file);
                CropImage.activity(selectedImageUriCrop)
                        .start(PostallActivity.this);

            } else if (requestCode == CAMERA_TAKE_PHOTO) {
                File file = new File(PersistentUser.getImagePath(mContext));
                final Uri selectedImageUri = Uri.fromFile(file);
                CropImage.activity(selectedImageUri)
                        .start(this);

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                selectedImagePath = ImageFilePath.getPath(mContext, resultUri);
                Glide.with(mContext)
                        .load(new File(selectedImagePath))
                        .apply(requestOptionsForRadious)
                        .into(imageImages);
                post_type = "1";
                deletefile.setVisibility(View.VISIBLE);

            } else if (requestCode == GALLERY_SELECT_VIDEO) {
                if (null == data)
                    return;
                Uri selectedImageUri = data.getData();
                selectedImagePath = ImageFilePath.getPath(mContext, selectedImageUri);
                Glide.with(mContext)
                        .load(new File(selectedImagePath))
                        .apply(requestOptionsForRadious)
                        .into(imageImages);
                post_type = "2";
                deletefile.setVisibility(View.VISIBLE);


            } else if (requestCode == CAMERA_SELECT_VIDEO) {
                if (null == data)
                    return;
                Uri selectedImageUri = data.getData();
                selectedImagePath = ImageFilePath.getPath(mContext, selectedImageUri);
                Glide.with(mContext)
                        .load(new File(selectedImagePath))
                        .apply(requestOptionsForRadious)
                        .into(imageImages);
                post_type = "2";
                deletefile.setVisibility(View.VISIBLE);


            } else if (requestCode == GALLERY_SELECT_AUDIO) {
                if (null == data)
                    return;
                Uri selectedImageUri = data.getData();
                selectedImagePath = ImageFilePath.getPath(mContext, selectedImageUri);
                post_type = "3";
                imageImages.setImageResource(R.drawable.audio_icon);
                deletefile.setVisibility(View.VISIBLE);


            } else if (requestCode == CAMERA_SELECT_AUDIO) {
                if (null == data)
                    return;
                deletefile.setVisibility(View.VISIBLE);
                Uri selectedImageUri = data.getData();
                selectedImagePath = ImageFilePath.getPath(mContext, selectedImageUri);
                imageImages.setImageResource(R.drawable.audio_icon);
                post_type = "3";

            }
        }
    }


}
