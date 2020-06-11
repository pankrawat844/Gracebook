package com.grace.book.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.grace.book.R;
import com.grace.book.myapplication.Myapplication;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class ConstantFunctions {
    private static final String TAG = ConstantFunctions.class.getSimpleName();
    public static RequestOptions requestOptionsForRadious = new RequestOptions()
            .bitmapTransform(new RoundedCornersTransformation(8, 2, RoundedCornersTransformation.CornerType.TOP))
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public static RequestOptions requestOptionsRadioMatch = new RequestOptions()
            .bitmapTransform(new RoundedCornersTransformation(10, 1, RoundedCornersTransformation.CornerType.ALL))
            .diskCacheStrategy(DiskCacheStrategy.ALL);


    public static RequestOptions requestOptionsCircel = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public static RequestOptions requestOptionsNormal = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public static RequestOptions requestOptionsForRadiousHeader = new RequestOptions()
            .bitmapTransform(new RoundedCornersTransformation(10, 2, RoundedCornersTransformation.CornerType.ALL))
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public static RequestOptions requestOptionsForRadiousHeader2 = new RequestOptions()
            .bitmapTransform(new RoundedCornersTransformation(10, 2, RoundedCornersTransformation.CornerType.BOTTOM))
            .diskCacheStrategy(DiskCacheStrategy.ALL);


    public static String getDeviceIMEI() {
        return Settings.Secure.getString(Myapplication.getInstance().getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getApplicationVersion() {
        String version = "1.0";
        try {
            PackageInfo pInfo = Myapplication.getInstance().getContext().getPackageManager().getPackageInfo(Myapplication.getInstance().getContext().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = "Android V" + version;
        return version;
    }

    public static String getDeviceType() {
        String deviceType = "Android";
        return deviceType;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static String getPhoneName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    public static int getScreenHeight() {

        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    public static int getGridItemSize(int NUM_OF_COLUMNS) {
        return (int) (Resources.getSystem().getDisplayMetrics().widthPixels - 50) / NUM_OF_COLUMNS;
    }

    public static void loadImage(String url, final ImageView iv) {
        if (url.startsWith("https") || url.startsWith("https")) {
            Glide.with(Myapplication.getContext()).asBitmap()
                    .load(url)
                    .fitCenter()
                    .apply(requestOptionsForRadious)
                    .into(iv);
        }
    }

    public static void loadImageForCircel(String url, final ImageView iv) {
        if (url.toString().equalsIgnoreCase("")) {

        } else {
            Glide.with(Myapplication.getContext()).asBitmap()
                    .load(url)
                    .apply(requestOptionsCircel.circleCrop())
                    .into(iv);
        }

    }

    public static void loadImageNomal(String url, final ImageView iv) {
        Glide.with(Myapplication.getContext()).asBitmap()
                .load(url)
                .fitCenter()
                .apply(requestOptionsNormal)
                .into(iv);
    }

    public static void loadImageHeaderFirst(String url, final ImageView iv) {
        Glide.with(Myapplication.getContext()).asBitmap()
                .load(url)
                .fitCenter()
                .apply(requestOptionsForRadiousHeader2)
                .into(iv);
    }

    public static void loadImageHeader(String url, final ImageView iv) {
        Glide.with(Myapplication.getContext()).asBitmap()
                .load(url)
                .fitCenter()
                .apply(requestOptionsForRadiousHeader)
                .into(iv);
    }

    public static void loadImageMatch(String url, final ImageView iv) {
        Glide.with(Myapplication.getContext())
                .asBitmap()
                .load(url)
                .fitCenter()
                .apply(requestOptionsRadioMatch)
                .into(iv);
    }


    public static Bitmap createImage(int width, int height, int color, int colorText, String name) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint2 = new Paint();
        paint2.setColor(color);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint2);
        Paint paint = new Paint();
        paint.setColor(colorText);
        paint.setTextSize(72);
        paint.setTextScaleX(1);
        canvas.drawText(name, 75 - 25, 75 + 20, paint);
        return bitmap;
    }

    public static void openIntentForShare(Context mContext, String textMail) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        } else {
            try {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, textMail);
                sendIntent.setType("text/plain");
                mContext.startActivity(sendIntent);

            } catch (Exception ex) {
            }
        }
    }

    public static String getCompleteAddressString(double latitude, double longitude) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(Myapplication.getInstance().getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    public static void webIntntCall(String web_url) {
        Intent newIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(web_url));
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Myapplication.getContext().startActivity(newIntent);

    }

    public static String[] getAddress() {
        double latitude = Double.parseDouble(PersistentUser.getLatitude(Myapplication.getContext())); //mGpsTracker.getLatitude();
        double longitude = Double.parseDouble(PersistentUser.getLongitude(Myapplication.getContext()));
        Geocoder geocoder = new Geocoder(Myapplication.getInstance().getContext(), Locale.getDefault());
        List<Address> addresses = null;
        String[] address = new String[2];
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                String city = addresses.get(0).getLocality();
                //String state = addresses.get(0).getAdminArea();
                //String zip = addresses.get(0).getPostalCode();
                String country = addresses.get(0).getCountryName();
//                if (mStateMap.containsKey(state)) {
//                    state = mStateMap.get(state);
//                }
                //  Logger.debugLog("state", state);
                address[0] = city;
                address[1] = country;
                Log.e("city",city);
                Log.e("country",country);

                //address[1] = state;
                // address[2] = zip;

            }


        } catch (IOException e) {
            Log.e("IOException",e.getMessage());
            e.printStackTrace();
        }
        return address;
    }


}
