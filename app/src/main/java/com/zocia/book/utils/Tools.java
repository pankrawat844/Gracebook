package com.zocia.book.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Tools<returnString> {


    // Static final String
    public static final String EMPTY_SPACE = "";
    public static final String SINGLE_SPACE = " ";
    public static final String UNDERSCORE = "_";
    public static final String COMA = ",";
    public static final String SEMICOLON = ";";
    public static final String COLON = ":";
    public static final String SINGLE_QUOTE = "'";
    public static final String DOT = ".";

    public static final String LEFT_PARENTHESIS = "(";
    public static final String RIGHT_PARENTHESIS = ")";

    public static final String BACK_SLASH = "/";
    public static final String RIGHT_SLASH = "\\";

    // Static Date Pattern
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String EEEE_DD_MMMM = "EEEE, dd MMMM";
    public static final String MMMM_yyyy = "EEEE";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String dd_MMMM_yyyy = "dd MMMM yyyy";
    public static final String DD_MM_YY_HH_MM_SS = "dd/MM/yy HH:mm:ss";
    public static final String DD_MM_ = "HH:mm";
    public static final String DD_MM_SS = "HH:mm:ss";
    public static final String E_MMM_DD_HH_MM_SS_Z_YYYY = "E MMM dd HH:mm:ss Z yyyy";
    public static final String EEE_D_MMM_YYYY_HH_MM = "EEEE, d MMM yyyy";

    // Static Final Numeric
    public static final long ONE_DAY = 24 * 60 * 60 * 1000;

    // static Final SystemDate
    public static final long TIME_MILLISECOND = System.currentTimeMillis();

    // Static File Extensions
    public static final String PNG = ".png";
    public static final String PDF = ".pdf";
    public static final String MP3 = ".mp3";
    public static final String MP4 = ".mp4";
    public static final String DOC = ".doc";
    public static final String JPEG = ".jpeg";
    public static final String VIDEO = ".avi";
    public static final String XLS = ".xls";
    public static final String TXT = ".txt";
    public static final String DOCX = ".docx";
    public static final String MPG = ".mpg";
    public static final String XLSX = ".xlsx";
    public static final String JPG = ".jpg";

    // Static mime type
    public static final String PDF_MIME_TYPE = "application/pdf";
    public static final String MP3_MIME_TYPE = "audio/mp3";
    public static final String MP4_MIME_TYPE = "video/mp4";

    public static final String DOC_MIME_TYPE = "application/msword";
    public static final String JPEG_MIME_TYPE = "image/jpeg";
    public static final String VIDEO_MIME_TYPE = "video/*";
    public static final String XLS_MIME_TYPE = "application/vnd.ms-powerpoint";
    public static final String TXT_MIME_TYPE = "text/plain";

    public static String buildCommaSeparatedValue(List<String> lstString) {
        String commaSeparatedString = EMPTY_SPACE;
        for (int i = 0; i < lstString.size(); i++) {
            if ((i + 1) == lstString.size()) {
                commaSeparatedString = commaSeparatedString + SINGLE_QUOTE + lstString.get(i) + SINGLE_QUOTE;
            } else {
                commaSeparatedString = commaSeparatedString + SINGLE_QUOTE + lstString.get(i) + SINGLE_QUOTE + COMA;
            }
        }
        Log.w("commaSeparatedString ", commaSeparatedString);
        return commaSeparatedString;
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateConvertion(String fromFormate, String toFormate, String dateinString) {

        SimpleDateFormat sdf = new SimpleDateFormat(fromFormate);
        final Format formatter = new SimpleDateFormat(toFormate);
        String result = "";
        try {

            Date date = sdf.parse(dateinString);
            System.out.println(date);
            System.out.println(formatter.format(date));
            result = formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            result = null;
        }
        return result;

    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

        Bitmap bm = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public int calculateInSampleSize(

            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }
    public static String leadingZero(int number) {
        String returnString="";
        DecimalFormat df = new DecimalFormat("000");
         returnString = df.format(number);
            return returnString;

    }

    public static String createJson(String Name, String gender, String bio, String bdate) {
        String returnString="";
        JSONObject student2 = new JSONObject();
        try {
            student2.put("name", Name);
            student2.put("bdate", bdate);
            student2.put("gender", gender);
            student2.put("bio", bio);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(student2);

        returnString = jsonArray.toString();
        return returnString;

    }

}
