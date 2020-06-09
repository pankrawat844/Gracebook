package com.grace.book.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtility {

    /**
     * Parse string date to formatted date object
     *
     * @param dateString
     * @param dateFormat
     * @return parseDate - Date object or null
     */
    public static Date parseDate(String dateString, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            Date parsedDate = sdf.parse(dateString);
            return parsedDate;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static Date parseDateForLocation(String dateString, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            Date parsedDate = sdf.parse(dateString);
            return parsedDate;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Format simple date formatted object to string
     *
     * @param date
     * @param dateFormat
     * @return formatDate - Formatted date string
     */
    public static String formatSDF(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatDate = sdf.format(date).trim();
        return formatDate;
    }

    /**
     * Format date object to string
     *
     * @param date
     * @param dateFormat
     * @return formatDate - formatted date string
     */
    public static String formatDate(Date date, String dateFormat) {
        Format formatter = new SimpleDateFormat(dateFormat);
        String formatDate = formatter.format(date).trim();
        return formatDate;
    }

    public static String dateToTime(Date date) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Tools.DD_MM_);
        String dateToTime = DATE_FORMAT.format(date);
        return dateToTime;
    }


    public static long dateToMillisecond(String fromDateString, String format) {
        Log.i("Fucking faasasas", "" + fromDateString);
        SimpleDateFormat sdf;
        Date date = null;
        sdf = new SimpleDateFormat(format);
        try {
            date = sdf.parse(fromDateString);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long currentMillisec = date.getTime();
        Log.i("Fucking faasasas", "" + currentMillisec);
        return currentMillisec;

    }


    public static String dateFromDayDifference(String currentDate, long oneday) {
        SimpleDateFormat sdf;
        Date date = null;
        String finalDateString;
        sdf = new SimpleDateFormat(Tools.YYYY_MM_DD_HH_MM_SS);
        try {
            date = sdf.parse(currentDate);
            long currentMillisec = date.getTime();
            Date resultExpectDate = new Date((currentMillisec + oneday));
            String finalDate = sdf.format(resultExpectDate).toString().trim();
            date = sdf.parse(finalDate);
            finalDateString = formatDate(date, Tools.YYYY_MM_DD_HH_MM_SS);
            return finalDateString;
        } catch (Exception e) {
            return "NULL";
        }

    }

    public static String durationHave(String dateString, String dateFormat) {
        String text = "";

        try {
            Date expiredate = parseDate(dateString, dateFormat);
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

            Date mDate = new Date();
            TimeZone tz = TimeZone.getTimeZone("Asia/Dubai");
            sdf.setTimeZone(tz);
            String date = sdf.format(mDate);
            Date currentDate = parseDate(date, dateFormat);


            if (expiredate.compareTo(currentDate) < 0) {
                text = (String) GetTimeCovertAgo.getNewsFeeTimeAgo(expiredate.getTime(), currentDate);

            } else if (expiredate.compareTo(currentDate) > 0) {
                long milliseconds = expiredate.getTime() - currentDate.getTime();


//                int seconds = (int) (milliseconds / 1000) % 60;
//                int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
//                int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);


                long seconds = milliseconds / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                String time = days + ":" + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;


//                long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
//                long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
//                long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
//                long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);


                String totalDays = "";
                String totalHours = "";
                String totalMIn = "";
                String totalSec = "";

                if (9 >= days)
                    totalDays = "0" + days;
                else
                    totalDays = "" + days;


                if (9 >= hours)
                    totalHours = "0" + hours;
                else
                    totalHours = "" + hours;


                if (9 >= minutes)
                    totalMIn = "0" + minutes;
                else
                    totalMIn = "" + minutes;

                if (9 >= seconds)
                    totalSec = "0" + seconds;
                else
                    totalSec = "" + seconds;

                text = totalDays + ": " + totalHours + ":" + totalMIn + ":" + totalSec;

                text = time;

            } else {
                text = GetTimeCovertAgo.getNewsFeeTimeAgo(expiredate.getTime(), currentDate);

            }
        } catch (Exception ex) {
            Logger.debugLog("Exception timer", ex.getMessage());
        }
        return text;


    }


    public static long getminutes(String dateString, String dateFormat) {
        long minutes = 0;
        try {
            Date expiredate = parseDate(dateString, dateFormat);
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            Date mDate = new Date();
            TimeZone tz = TimeZone.getTimeZone("Asia/Dubai");
            sdf.setTimeZone(tz);
            String date = sdf.format(mDate);
            Date currentDate = parseDate(date, dateFormat);
            if (expiredate.compareTo(currentDate) < 0) {
                minutes = -1;

            } else if (expiredate.compareTo(currentDate) > 0) {
                long milliseconds = expiredate.getTime() - currentDate.getTime();
                minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
                ///minutes = (int) ((milliseconds / (1000 * 60)) % 60);

            } else {
                minutes = -1;

            }
        } catch (Exception ex) {
            minutes = -1;
        }
        return minutes;


    }


    public static String getCurrentTime() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public static String getCurrentTimeHHmm() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public static String getTodaysDate() {
        DateFormat df = new SimpleDateFormat(Tools.EEE_D_MMM_YYYY_HH_MM);
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public static String getDayofWeek() {
        DateFormat df = new SimpleDateFormat(Tools.MMMM_yyyy);
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public static String getDayofWeek(String input) {
        //2019-06-14
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputformat = new SimpleDateFormat(Tools.MMMM_yyyy);
        Date date = null;
        String output = null;
        try {
            date = df.parse(input);
            output = outputformat.format(date);
            System.out.println(output);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output;

    }

    public static String getDateFortop(String input) {
        //2019-06-14
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputformat = new SimpleDateFormat(Tools.EEE_D_MMM_YYYY_HH_MM);
        Date date = null;
        String output = null;
        try {
            date = df.parse(input);
            output = outputformat.format(date);
            System.out.println(output);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output;

    }

    public static String getCurrentday() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public static String converTdate(String input) {
        DateFormat df = new SimpleDateFormat("HH:mm");
        DateFormat outputformat = new SimpleDateFormat("hh:mm aa");
        Date date = null;
        String output = null;
        try {
            date = df.parse(input);
            output = outputformat.format(date);
            System.out.println(output);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output;
    }

    public static String getCurrentdayFor() {
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public static String getCurrentLinkUp(String input) {
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat outputformat = new SimpleDateFormat("EEEE, MMM dd");
        Date date = null;
        String output = null;
        try {
            date = df.parse(input);
            output = outputformat.format(date);
            System.out.println(output);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output;
    }

    public static String getCurrentConvert(String input) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputformat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        String output = null;
        try {
            date = df.parse(input);
            output = outputformat.format(date);
            System.out.println(output);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output;
    }

    public static String getCurrentConvertFromMMDDYYY(String input) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat outputformat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        String output = null;
        try {
            date = df.parse(input);
            output = outputformat.format(date);
            System.out.println(output);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output;
    }

    public static String getAge(String dateString) {
        String[] parts = dateString.split("/");
        String part1 = parts[0];
        String part2 = parts[1];
        String part3 = parts[2];
        int mYear = Integer.parseInt(part3);
        int mMonth = Integer.parseInt(part2);
        int mDay = Integer.parseInt(part1);
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(mYear, mMonth, mDay);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}
