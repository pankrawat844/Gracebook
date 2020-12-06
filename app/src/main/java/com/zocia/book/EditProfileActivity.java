package com.zocia.book;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zocia.book.adapter.InterestsAdapter;
import com.zocia.book.callbackinterface.InterestListener;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.myapplication.Myapplication;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.networkcalls.VolleyMultipartRequest;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.BusyDialog;
import com.zocia.book.utils.ConstantFunctions;
import com.zocia.book.utils.Helpers;
import com.zocia.book.utils.ImageFilePath;
import com.zocia.book.utils.InterestList;
import com.zocia.book.utils.Logger;
import com.zocia.book.utils.PersistentUser;
import com.zocia.book.utils.ToastHelper;
import com.zocia.book.utils.ValidateEmail;
import com.theartofdev.edmodo.cropper.CropImage;

import org.chat21.android.core.ChatManager;
import org.chat21.android.utils.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.zocia.book.utils.ConstantFunctions.requestOptionsForRadious;

public class EditProfileActivity extends AppCompatActivity implements InterestListener {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private static final int CAMERA_TAKE_PHOTO = 10;
    private static final int GALLERY_TAKE_PHOTO = 12;
    private final Handler mHandler = new Handler();
    final private int REQUEST_CODE_ASK_PERMISSIONS_AGENT = 100;
    RelativeLayout toolbarLayout;
    TextView male, female, both;
    RelativeLayout rate_us;
    String isnotification = "0";
    String[] arrayListSignup;
    String[] arrayList;
    List<String> permissions = new ArrayList<String>();
    private Context mContext;
    private LinearLayout layoutBack;
    private String selectedImagePath = "";
    private ImageView userImage;
    private EditText edittextFname;
    private EditText edittextLname;
    private EditText edittextnio;
    private EditText edittextEmail;
    private EditText edittextphone;
    private Spinner edittextcountry_pref;
    private Spinner edittextcountry;
    private LinearLayout gender_prefGrp;
    private BusyDialog mBusyDialog;
    private SwitchCompat myswitch;
    private String country_code;
    private String country_code_name;
    private String gender_select = "Both";
    private TextView interest1, interest2, interest3, interest4, interest5, save;
    private ImageView editInterst;
    private LinearLayout headerLayout, interestLayout;
    private TextView selectedInterestTotalTxt;
    private InterestsAdapter interestsAdapter;
    private RecyclerView interestRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eidtprofile);
        mContext = this;
        initUi();
    }

    private void initUi() {
        myswitch = (SwitchCompat) this.findViewById(R.id.myswitch);
        layoutBack = (LinearLayout) this.findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        userImage = (ImageView) this.findViewById(R.id.userImageEdit);
        rate_us = (RelativeLayout) this.findViewById(R.id.rate_us);
        rate_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                } catch (ActivityNotFoundException e) {
                    Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                }

            }
        });
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFileUploadPermissions();
            }
        });
        myswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myswitch.isChecked()) {
                    isnotification = "1";
                } else {
                    isnotification = "0";
                }
            }
        });
        interestRv = (RecyclerView) this.findViewById(R.id.recycler_interest);
        TextView name = findViewById(R.id.name);
        TextView location = findViewById(R.id.location);
        JSONObject mJsonObject = null;
        try {
            mJsonObject = new JSONObject(PersistentUser.getUserDetails(mContext));
            if (mJsonObject.getBoolean("success")) {

                JSONObject data = mJsonObject.getJSONObject("data");
                name.setText(data.getString("fname") + " " + data.getString("lname"));
                location.setText(data.getString("city") + ", " + data.getString("country"));
                String isnotification = data.getString("isnotification");

                if (isnotification.equalsIgnoreCase("1")) {
                    myswitch.setChecked(true);
                    this.isnotification = "1";
                } else {
                    myswitch.setChecked(false);
                    this.isnotification = "0";

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        selectedInterestTotalTxt = (TextView) this.findViewById(R.id.selectedItemTotal);
        headerLayout = findViewById(R.id.headerLayout);
        interestLayout = findViewById(R.id.interestLL);
        edittextFname = (EditText) this.findViewById(R.id.edittextFname);
        edittextLname = (EditText) this.findViewById(R.id.edittextLname);
        edittextnio = (EditText) this.findViewById(R.id.edittextnio);
        edittextEmail = (EditText) this.findViewById(R.id.edittextEmail);
        edittextphone = (EditText) this.findViewById(R.id.edittextphone);
        edittextcountry_pref = (Spinner) this.findViewById(R.id.country_pref);
        edittextcountry = (Spinner) this.findViewById(R.id.edittextCountry);
        gender_prefGrp = (LinearLayout) this.findViewById(R.id.segmentedButtonGroup);
        edittextFname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        edittextLname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        interest1 = (TextView) this.findViewById(R.id.interest1);
        interest2 = (TextView) this.findViewById(R.id.interest2);
        interest3 = (TextView) this.findViewById(R.id.interest3);
        interest4 = (TextView) this.findViewById(R.id.interest4);
        interest5 = (TextView) this.findViewById(R.id.interest5);
        editInterst = (ImageView) this.findViewById(R.id.edit_interest);
        save = (TextView) this.findViewById(R.id.btnSignup);
        toolbarLayout = (RelativeLayout) this.findViewById(R.id.toolbarLayout);
        male = (TextView) this.findViewById(R.id.male);
        female = (TextView) this.findViewById(R.id.female);
        both = (TextView) this.findViewById(R.id.both);

        editInterst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Myapplication) getApplicationContext()).getInterest().clear();
                setInterestList();
                headerLayout.setVisibility(View.GONE);
                toolbarLayout.setVisibility(View.GONE);
                interestLayout.setVisibility(View.VISIBLE);
                save.setText("SAVE");
            }
        });
        findViewById(R.id.layoutBack1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headerLayout.setVisibility(View.VISIBLE);
                toolbarLayout.setVisibility(View.VISIBLE);
                interestLayout.setVisibility(View.GONE);

            }
        });
        arrayList = getResources().getStringArray(R.array.countries);
//        for (int i = 0; i < arrayList.length; i++) {
//            for (int j = i + 1; j < arrayList.length; j++) {
//                if (arrayList[i].compareTo(arrayList[j]) > 0) {
//                    String temp = arrayList[i];
//                    arrayList[i] = arrayList[j];
//                    arrayList[j] = temp;
//                }
//            }
//        }


        arrayListSignup = getResources().getStringArray(R.array.countries);
        for (int i = 0; i < arrayListSignup.length; i++) {
            for (int j = i + 1; j < arrayListSignup.length; j++) {
                if (arrayListSignup[i].compareTo(arrayListSignup[j]) > 0) {
                    String temp = arrayListSignup[i];
                    arrayListSignup[i] = arrayListSignup[j];
                    arrayListSignup[j] = temp;
                }
            }
        }
        edittextcountry_pref.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList));
        edittextcountry.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListSignup));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((Myapplication) getApplicationContext()).getInterest().size() == 5) {
                    interest1.setText(((Myapplication) getApplicationContext()).getInterest().get(0));
                    interest2.setText(((Myapplication) getApplicationContext()).getInterest().get(1));
                    interest3.setText(((Myapplication) getApplicationContext()).getInterest().get(2));
                    interest4.setText(((Myapplication) getApplicationContext()).getInterest().get(3));
                    interest5.setText(((Myapplication) getApplicationContext()).getInterest().get(4));
                    headerLayout.setVisibility(View.VISIBLE);
                    toolbarLayout.setVisibility(View.VISIBLE);
                    interestLayout.setVisibility(View.GONE);
                    ScrollView sc = (ScrollView) findViewById(R.id.scrollView);
                    sc.scrollTo(0, 0);
                } else
                    Toast.makeText(EditProfileActivity.this, "Please select 5 interest.", Toast.LENGTH_SHORT).show();
            }
        });
        try {
            JSONObject mJsonObject1 = new JSONObject(PersistentUser.getUserDetails(mContext));
            JSONObject data = mJsonObject1.getJSONObject("data");
            if (data.has("interest1") && !data.isNull("interest1")) {
                interest1.setVisibility(View.VISIBLE);
                interest1.setText(data.getString("interest1"));
            }

            if (data.has("interest2") && !data.isNull("interest2")) {
                interest2.setVisibility(View.VISIBLE);
                interest2.setText(data.getString("interest2"));
            }
            if (data.has("interest3") && !data.isNull("interest3")) {
                interest3.setVisibility(View.VISIBLE);
                interest3.setText(data.getString("interest3"));
            }
            if (data.has("interest4") && !data.isNull("interest4")) {
                interest4.setVisibility(View.VISIBLE);
                interest4.setText(data.getString("interest4"));
            }
            if (data.has("interest5") && !data.isNull("interest5")) {
                interest5.setVisibility(View.VISIBLE);
                interest5.setText(data.getString("interest5"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        findViewById(R.id.btinSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
        userDataList();


        male.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                gender_select = "Male";
                male.setBackground(getResources().getDrawable(R.drawable.gender_select_bg_btn));
                male.setTextColor(getColor(R.color.white));
                female.setBackground(getResources().getDrawable(R.drawable.gender_bg_btn));
                female.setTextColor(getColor(R.color.black));
                both.setBackground(getResources().getDrawable(R.drawable.gender_bg_btn));
                both.setTextColor(getColor(R.color.black));

            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                gender_select = "Female";
                female.setBackground(getResources().getDrawable(R.drawable.gender_select_bg_btn));
                female.setTextColor(getColor(R.color.white));
                male.setBackground(getResources().getDrawable(R.drawable.gender_bg_btn));
                male.setTextColor(getColor(R.color.black));
                both.setBackground(getResources().getDrawable(R.drawable.gender_bg_btn));
                both.setTextColor(getColor(R.color.black));

            }
        });

        both.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                gender_select = "Both";
                both.setBackground(getResources().getDrawable(R.drawable.gender_select_bg_btn));
                both.setTextColor(getColor(R.color.white));
                male.setBackground(getResources().getDrawable(R.drawable.gender_bg_btn));
                male.setTextColor(getColor(R.color.black));
                female.setBackground(getResources().getDrawable(R.drawable.gender_bg_btn));
                female.setTextColor(getColor(R.color.black));

            }
        });
    }

    public void userDataList() {
        try {

            JSONObject mJsonObject = new JSONObject(PersistentUser.getUserDetails(mContext));
            if (mJsonObject.getBoolean("success")) {

                JSONObject data = mJsonObject.getJSONObject("data");
                String fname = data.getString("fname");
                String lname = data.getString("lname");
                String email = data.getString("email");
                country_code = data.getString("country_code");
                String country = data.getString("country");
                String phone = data.getString("phone");
                String profile_pic = data.getString("profile_pic");
                String bio = data.getString("bio");
                country_code_name = data.getString("country_code_name");
                String gender_perf = data.getString("gender_pref");
                String country_perf = data.getString("country_pref");
                if (gender_perf.equalsIgnoreCase("Male")) {
                    gender_select = "Nale";
                    male.setBackground(getResources().getDrawable(R.drawable.gender_select_bg_btn));
                    male.setTextColor(getColor(R.color.white));
                }
                if (gender_perf.equalsIgnoreCase("Female")) {
                    gender_select = "Female";
                    female.setBackground(getResources().getDrawable(R.drawable.gender_select_bg_btn));
                    female.setTextColor(getColor(R.color.white));
                }
                if (gender_perf.equalsIgnoreCase("Both")) {
                    gender_select = "Both";
                    both.setBackground(getResources().getDrawable(R.drawable.gender_select_bg_btn));
                    both.setTextColor(getColor(R.color.white));
                }
                edittextFname.setText(fname);
                edittextLname.setText(lname);
                edittextEmail.setText(email);
                edittextphone.setText(phone);

                for (int i = 0; i < arrayList.length; i++) {
                    if (arrayList[i].equals(country_perf)) {
                        edittextcountry_pref.setSelection(i);
                        break;
                    }
                }

                for (int i = 0; i < arrayListSignup.length; i++) {
                    if (arrayListSignup[i].equals(country)) {
                        edittextcountry.setSelection(i);
                        break;
                    }
                }
                edittextnio.setText(Logger.EmptyString(bio));

                if (profile_pic != null || !profile_pic.equalsIgnoreCase("")) {
                    ConstantFunctions.loadImageForCircel(profile_pic, userImage);

                }
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());

        }

    }

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
                String selectedImagePath = ImageFilePath.getPath(mContext, selectedImageUri);
                File file = new File(selectedImagePath);
                final Uri selectedImageUriCrop = Uri.fromFile(file);
                selectedImagePath = ImageFilePath.getPath(mContext, selectedImageUri);

                Logger.debugLog("selectedImagePath", "are" + selectedImagePath);
//                Glide.with(mContext)
//                        .load(new File(selectedImagePath))
//                        .apply(requestOptionsForRadious)
//                        .circleCrop()
//                        .error(R.drawable.user_icon)
//                        .placeholder(R.drawable.user_icon)
//                        .into(userImage);
                CropImage.activity(selectedImageUriCrop)
                        .start(this);

            } else if (requestCode == CAMERA_TAKE_PHOTO) {
                File file = new File(PersistentUser.getImagePath(mContext));
                final Uri selectedImageUri = Uri.fromFile(file);
                selectedImagePath = ImageFilePath.getPath(mContext, selectedImageUri);
//                Glide.with(mContext)
//                        .load(new File(selectedImagePath))
//                        .apply(requestOptionsForRadious)
//                        .circleCrop()
//                        .error(R.drawable.user_icon)
//                        .placeholder(R.drawable.user_icon)
//                        .into(userImage);
                CropImage.activity(selectedImageUri)
                        .start(this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                selectedImagePath = ImageFilePath.getPath(mContext, resultUri);
                Glide.with(mContext)
                        .load(new File(selectedImagePath))
                        .apply(requestOptionsForRadious)
                        .into(userImage);


            }
        }
    }

    private void setInterestList() {
        interestsAdapter = new InterestsAdapter(this, InterestList.getInterestList(), this);
        interestRv.setLayoutManager(new LinearLayoutManager(this));
        interestRv.setItemAnimator(new DefaultItemAnimator());
        interestRv.setAdapter(interestsAdapter);
    }

    public void validation() {
        String fname = edittextFname.getText().toString().trim();
        String lname = edittextLname.getText().toString().trim();
        String email = edittextEmail.getText().toString().trim();
        String phone = edittextphone.getText().toString().trim();

        String bio = edittextnio.getText().toString().trim();


        if (fname.equalsIgnoreCase("")) {
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
        }
//        else if (edittextcountry_pref.getSelectedItem().toString().equalsIgnoreCase("")) {
//            edittextcountry_pref.setError("Enter Country");
//            return;
//        }
        else {
            HashMap<String, String> allHashMap = new HashMap<>();
            allHashMap.put("fname", fname);
            allHashMap.put("lname", lname);
            allHashMap.put("email", email);
            allHashMap.put("phone", phone);
            allHashMap.put("country_code", country_code);
            allHashMap.put("country", edittextcountry.getSelectedItem().toString());
            allHashMap.put("country_code_name", country_code_name);
            allHashMap.put("bio", bio);
            allHashMap.put("isnotification", isnotification);

            allHashMap.put("church", "church");
            allHashMap.put("device_token", PersistentUser.getPushToken(mContext));
            allHashMap.put("latitude", PersistentUser.getLatitude(mContext));
            allHashMap.put("longitude", PersistentUser.getLongitude(mContext));
            allHashMap.put("gender_pref", gender_select);
            allHashMap.put("country_pref", edittextcountry_pref.getSelectedItem().toString());
            allHashMap.put("interest1", interest1.getText().toString());
            allHashMap.put("interest2", interest2.getText().toString());
            allHashMap.put("interest3", interest3.getText().toString());
            allHashMap.put("interest4", interest4.getText().toString());
            allHashMap.put("interest5", interest5.getText().toString());
            sigupServerRequest(allHashMap);
        }
    }

    public void sigupServerRequest(HashMap<String, String> allHashMap) {

        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }

        HashMap<String, String> headerParams = new HashMap<>();
        headerParams.put("appKey", AllUrls.APP_KEY);
        headerParams.put("authToken", PersistentUser.getUserToken(mContext));
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        String url = AllUrls.BASEURL + "updateMobileUser";
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
                        ToastHelper.showToast(mContext, "Update profile successfully");
                        PersistentUser.setUserDetails(mContext, responseServer);
                        FirebaseAuth.getInstance().getCurrentUser().getUid();
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


                        JSONObject mJsonObject1 = new JSONObject(PersistentUser.getUserDetails(mContext));
                        if (mJsonObject.getBoolean("success")) {

                            JSONObject data = mJsonObject.getJSONObject("data");

                            String profile_pic = data.getString("profile_pic");
                            contactsNode.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imageurl").setValue(profile_pic);
                        }
                    } else {
                        ToastHelper.showToast(mContext, "Update profile fail");

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

    private void ServerRequest(HashMap<String, String> allHashMap) {
        Logger.debugLog("allHashMap", allHashMap.toString());

        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "updateMobileUser";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        PersistentUser.setUserDetails(mContext, responseServer);

                    }
                } catch (Exception e) {
                    Logger.debugLog("Exception", e.getMessage());

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {

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
}
