package nu.pto.androidapp.base.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utils {

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static String formatSecondsToDateForMessages(long seconds) {
        Date date = new Date(seconds * 1000);

        GregorianCalendar messageCalendar = new GregorianCalendar();
        messageCalendar.setTime(date);

        Calendar todayCalendar = Calendar.getInstance();

        SimpleDateFormat dateFormat;

        if (messageCalendar.get(Calendar.ERA) == todayCalendar.get(Calendar.ERA) &&
                messageCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
                messageCalendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR)){

            dateFormat = new SimpleDateFormat("'Today' HH:mm");
        }else{
            dateFormat = new SimpleDateFormat();
        }

        return dateFormat.format(date);
    }

    public static String secondFormatToDate(long seconds) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        return dateFormat.format(new Date(seconds * 1000));
    }


    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public static String getTimeFromSeconds(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        String result = twoDigitString(minutes) + " : " + twoDigitString(seconds);
        if (hours != 0) {
            result = twoDigitString(hours) + " : " + result;
        }
        return result;
    }

    private static String twoDigitString(long number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

}