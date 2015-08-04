package nu.pto.androidapp.base.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    public static String KEY_DEVICE_ID = "device_id";
    public static String KEY_ACCESS_TOKEN = "access_token";
    public static String KEY_REG_ID = "registration_id";
    public static String KEY_EMAIL = "email";

    public static String KEY_APP_VERSION = "app_version";

    public static String KEY_IS_EXPIRED_USER = "expired_user";
    public static String KEY_USER_TYPE = "user_type";

    public static String KEY_GP_DIALOG_IGNORED = "gp_dialog_ignored";

    public static String KEY_SERVER_CLIENT_ID = "server_client_id";
    public static String KEY_CLIENT_ID = "client_id";

    public static String KEY_SERVER_TRAINER_ID = "server_trainer_id";
    public static String KEY_TRAINER_ID = "trainer_id";


//	public static String KEY_APP_VERSION = "app_version";
//	public static String KEY_APP_VERSION = "app_version";

    /**
     * returns shared prefs
     *
     * @param context context of application
     * @return null|SharedPreferences
     */
    public static SharedPreferences getPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        if (context != null) {
            return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        } else {
            return null;
        }
    }


    public static void putString(Context mContext, String key, String value) {
        if (mContext != null) {
            SharedPreferences mySharedPreferences = getPreferences(mContext);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    public static String getString(Context mContext, String key) {
        String result = "";
        if (mContext != null) {
            SharedPreferences mySharedPreferences = getPreferences(mContext);
            result = mySharedPreferences.getString(key, "");
        }
        return result;
    }

    public static void putBoolean(Context mContext, String key, Boolean value) {
        if (mContext != null) {
            SharedPreferences mySharedPreferences = getPreferences(mContext);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public static Boolean loadBoolean(Context mContext, String key) {
        Boolean result = false;
        if (mContext != null) {
            SharedPreferences mySharedPreferences = getPreferences(mContext);
            result = mySharedPreferences.getBoolean(key, false);
        }
        return result;
    }

    public static void putInt(Context mContext, String key, int value) {
        if (mContext != null) {
            SharedPreferences mySharedPreferences = getPreferences(mContext);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }

    public static int getInt(Context mContext, String key) {
        int defaultResult = Integer.MIN_VALUE;
        if (mContext != null) {
            SharedPreferences mySharedPreferences = getPreferences(mContext);
            return mySharedPreferences.getInt(key, defaultResult);
        }
        return defaultResult;
    }

    public static void putFloat(Context mContext, String key, float value) {
        if (mContext != null) {
            SharedPreferences mySharedPreferences = getPreferences(mContext);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putFloat(key, value);
            editor.apply();
        }
    }

    public static float getFloat(Context mContext, String key) {
        float result = 0f;
        if (mContext != null) {
            SharedPreferences mySharedPreferences = getPreferences(mContext);
            result = mySharedPreferences.getFloat(key, 0f);
        }
        return result;
    }

    public static void putLong(Context mContext, String key, long value) {
        if (mContext != null) {
            SharedPreferences mySharedPreferences = getPreferences(mContext);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putLong(key, value);
            editor.apply();
        }
    }

    public static long getLong(Context mContext, String key) {
        long result = 0;
        if (mContext != null) {
            SharedPreferences mySharedPreferences = getPreferences(mContext);
            result = mySharedPreferences.getLong(key, 0);
        }
        return result;
    }

    public static void clearAfterLogout(Context mContext) {
        SharedPreferences mySharedPreferences = getPreferences(mContext);
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        editor.remove(KEY_TRAINER_ID);
        editor.remove(KEY_CLIENT_ID);
        editor.remove(KEY_SERVER_CLIENT_ID);
        editor.remove(KEY_SERVER_TRAINER_ID);
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_USER_TYPE);
        editor.remove(KEY_GP_DIALOG_IGNORED);
        editor.remove(KEY_IS_EXPIRED_USER);
        editor.remove("Last-Sync-Timestamp");

        editor.apply();
    }

    public static void clearAll(Context mContext) {
        SharedPreferences mySharedPreferences = getPreferences(mContext);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
