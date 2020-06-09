package com.grace.book.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class GetTimeCovertAgo {

    public static String getNewsFeeTimeAgo(long milisecond, Date currentDate) {

        Logger.debugLog("milisecond", "are" + milisecond);

        String inputString = "";
        Date date = null;
        long totalday = 0;
        int millisec = 0, sec = 0, min = 0, hour = 0, day = 0, week = 0, month = 0, year = 0;
        int miniute = 60;// 60 second
        int houre = 60 * 60; //
        int dayTime = 60 * 60 * 24;
        int WeakIme = 60 * 60 * 24 * 7;
        int monthTime = 60 * 60 * 24 * 30;
        int yearTime = 60 * 60 * 24 * 30 * 12;


        long currentDuration = currentDate.getTime();
        long mainday = currentDuration - milisecond;

        if (mainday > 1000) {

            sec = (int) (mainday / 1000);

            if (sec < 60) {
                if (sec > 1)
                    inputString = Integer.toString(sec) + " Seconds ago";
                else
                    inputString = Integer.toString(sec) + " secound ago";
            } else if (sec >= 60 && sec < houre) {
                min = sec / 60;
                if (min > 1)
                    inputString = Integer.toString(min) + " minutes ago";
                else
                    inputString = Integer.toString(min) + " minute ago";
            } else if (sec >= houre && sec < dayTime) {
                hour = sec / houre;
                if (hour > 1)
                    inputString = Integer.toString(hour) + " hours ago";
                else
                    inputString = Integer.toString(hour) + " hour ago";

            } else if (sec >= dayTime && sec < WeakIme) {
                day = sec / dayTime;
                if (day > 1)
                    inputString = Integer.toString(day) + " days ago";
                else
                    inputString = Integer.toString(day) + " day ago";

            } else if (sec >= WeakIme && sec < monthTime) {
                week = sec / WeakIme;
                if (week > 1)
                    inputString = Integer.toString(week) + " weeks ago";
                else
                    inputString = Integer.toString(week) + " week ago";

            } else if (sec >= monthTime && sec < yearTime) {
                month = sec / monthTime;
                if (month > 1)
                    inputString = Integer.toString(month) + " months ago";
                else
                    inputString = Integer.toString(month) + " month ago";

            } else if (sec >= yearTime) {
                year = sec / yearTime;
                if (year > 1)
                    inputString = Integer.toString(month) + " years ago";
                else
                    inputString = Integer.toString(month) + " year ago";
            }

        } else {
            inputString = "5 Seconds ago";
        }

        return inputString;

    }

    public static String getNewsFeeTimeAgoFromNotification(long milisecond) {

        String inputString = "";
        Date date = null;
        long totalday = 0;
        int millisec = 0, sec = 0, min = 0, hour = 0, day = 0, week = 0, month = 0, year = 0;
        int miniute = 60;// 60 second
        int houre = 60 * 60; //
        int dayTime = 60 * 60 * 24;
        int WeakIme = 60 * 60 * 24 * 7;
        int monthTime = 60 * 60 * 24 * 30;
        int yearTime = 60 * 60 * 24 * 30 * 12;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dateformateCurrent = new Date();
        try {
            dateformateCurrent = sdf.parse(getTime("UTC"));
            Log.w("dateformateCurrent", "d" + dateformateCurrent);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long lDateTime = dateformateCurrent.getTime();

        long mainday = lDateTime - milisecond;

        if (mainday > 1000) {

            sec = (int) (mainday / 1000);

            if (sec < 60) {

                if (sec > 1)
                    inputString = Integer.toString(sec) + " Seconds ago";
                else
                    inputString = Integer.toString(sec) + " Second ago";

            } else if (sec >= 60 && sec < houre) {
                min = sec / 60;
                if (min > 1)
                    inputString = Integer.toString(min) + " minutes ago";
                else
                    inputString = Integer.toString(min) + " minute ago";
            } else if (sec >= houre && sec < dayTime) {
                hour = sec / houre;
                if (hour > 1)
                    inputString = Integer.toString(hour) + " hours ago";
                else
                    inputString = Integer.toString(hour) + " hour ago";

            } else if (sec >= dayTime && sec < WeakIme) {
                day = sec / dayTime;
                if (day > 1)
                    inputString = Integer.toString(day) + " days ago";
                else
                    inputString = Integer.toString(day) + " day ago";

            } else if (sec >= WeakIme && sec < monthTime) {
                week = sec / WeakIme;
                if (week > 1)
                    inputString = Integer.toString(week) + " weeks ago";
                else
                    inputString = Integer.toString(week) + " week ago";

            } else if (sec >= monthTime && sec < yearTime) {
                month = sec / monthTime;
                if (month > 1)
                    inputString = Integer.toString(month) + " months ago";
                else
                    inputString = Integer.toString(month) + " month ago";

            } else if (sec >= yearTime) {
                year = sec / yearTime;
                if (year > 1)
                    inputString = Integer.toString(month) + " years ago";
                else
                    inputString = Integer.toString(month) + " year ago";
            }

        } else {
            inputString = "5 Seconds ago";
        }

        return inputString;

    }

    public static String getTime(String timezone) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone(timezone));
        Date date = c.getTime(); // current date and time in UTC
        String year = "" + c.get(Calendar.YEAR);
        int Moth = c.get(Calendar.MONTH);
        String Day = "" + c.get(Calendar.DATE);
        Moth = Moth + 1;
        String mothString = "";
        if (9 >= Moth)
            mothString = "0" + Moth;
        else
            mothString = "" + Moth;

        String timeFormate = year + "-" + mothString + "-" + Day + " "
                + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE)
                + ":" + c.get(Calendar.SECOND);

        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //
        // timezone
        // String strDate = df.format(date);

        return timeFormate;
    }

    public static long datetimeconvert(String givenDateString) {
        long timeInMilliseconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date mDate = sdf.parse(givenDateString);
            timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }
}
