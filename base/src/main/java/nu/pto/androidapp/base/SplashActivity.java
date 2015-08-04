package nu.pto.androidapp.base;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import nu.pto.androidapp.base.db.DatabaseInitializer;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;


public class SplashActivity extends BaseActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID;

    GoogleCloudMessaging gcm;

    String regId;

    Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        context = getApplicationContext();

        // init database. Copy from assets folder if not exists
        DatabaseInitializer initializer = new DatabaseInitializer(context);
        try {
            initializer.createDatabase();
            initializer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get device unique id and store it in shared prefs
        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferencesHelper.putString(this, SharedPreferencesHelper.KEY_DEVICE_ID, android_id);

        SENDER_ID = getString(R.string.gcm_sender_id);
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = getRegistrationId(context);

            if (regId.isEmpty()) {
                registerInBackground();
            } else {
                gotoApplication();
            }
        } else {
            Log.i(LOG_TAG, "No valid Google Play Services APK found.");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Boolean isPlayServicesUpdateDialogIgnored = SharedPreferencesHelper.loadBoolean(context, SharedPreferencesHelper.KEY_GP_DIALOG_IGNORED);
                if (!isPlayServicesUpdateDialogIgnored) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // save in shared prefs that usr ignored update, not to show next time
                            SharedPreferencesHelper.putBoolean(context, SharedPreferencesHelper.KEY_GP_DIALOG_IGNORED, true);
                            // go to application
                            gotoApplication();

                        }
                    }).show();
                } else {
                    gotoApplication();
                }

            } else {
                Log.i(LOG_TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        String registrationId = SharedPreferencesHelper.getString(context, SharedPreferencesHelper.KEY_REG_ID);
        if (registrationId.isEmpty()) {
            Log.i(LOG_TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = SharedPreferencesHelper.getInt(context, SharedPreferencesHelper.KEY_APP_VERSION);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(LOG_TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                   
                    regId = gcm.register(SENDER_ID);
                    msg = regId;

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regId);

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(LOG_TAG, msg);
                SplashActivity.this.gotoApplication();
            }
        }.execute(null, null, null);

    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.i(LOG_TAG, "Saving regId on app version " + appVersion);

        SharedPreferencesHelper.putString(context, SharedPreferencesHelper.KEY_REG_ID, regId);
        SharedPreferencesHelper.putInt(context, SharedPreferencesHelper.KEY_APP_VERSION, appVersion);
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    /**
     *
     */
    private void sendRegistrationIdToBackend() {
        Log.d(LOG_TAG, "SEND REG ID");
        SharedPreferences prefs = SharedPreferencesHelper.getPreferences(context);
        Map<String, ?> keys = prefs.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    /**
     * go to activity and finish
     * if access token exists in shared prefs go to app activity
     * if not: go to Auth activity
     * finish current activity to remove from backstage
     */
    private void gotoApplication() {
        // TODO open in production
        String accessToken = SharedPreferencesHelper.getString(context, SharedPreferencesHelper.KEY_ACCESS_TOKEN);
//		String accessToken = "";
        final Intent intent;
        if (accessToken.isEmpty()) {
            intent = new Intent(SplashActivity.this, AuthActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, AppActivity.class);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

}