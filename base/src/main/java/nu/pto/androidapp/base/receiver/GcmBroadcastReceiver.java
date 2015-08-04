package nu.pto.androidapp.base.receiver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import nu.pto.androidapp.base.AppActivity;
import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;


public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    public static int NOTIFICATION_ID = 237;
    public static int MESSAGE_NOTIFICATION_ID = 236;

    public static String KEY_OPEN_FRAGMENT = "key_open_fragment";
    public static String KEY_SERVER_DIET_ID = "key_server_diet_id";
    public static String KEY_SERVER_WORKOUT_ID = "key_server_workout_id";

    public static int OPEN_FRAGMENT_MESSAGES = 1;
    public static int OPEN_FRAGMENT_DIETS = 2;
    public static int OPEN_FRAGMENT_WORKOUTS = 3;

    private enum PushType {

        MESSAGE("message"),
        MESSAGE_RECEIVED("message_received"),

        WORKOUT("workout"),
        WORKOUT_SESSION("workout_session"),

        DIET("diet");

        String mName;

        PushType(String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }

    }

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);

        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            setResultCode(Activity.RESULT_CANCELED);
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            setResultCode(Activity.RESULT_CANCELED);
        } else {

            setResultCode(Activity.RESULT_OK);

            String userType = SharedPreferencesHelper.getString(context, SharedPreferencesHelper.KEY_USER_TYPE);
            if (userType != null && !userType.isEmpty()) {
                sendNotification(intent);

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(BaseActivity.ACTION_PUSH_RECEIVER);
                context.sendBroadcast(broadcastIntent);
            }
        }
    }

    public void sendNotification(Intent pushIntent) {

        Intent intent = new Intent(context, AppActivity.class);

        String message = pushIntent.getStringExtra("message");

        String type = pushIntent.getStringExtra("type");

        if (type.equals(PushType.MESSAGE_RECEIVED.toString())) {
            return;
        }

        boolean isMessage = false;
        boolean isWorkout = false;
        boolean isDiet = false;

        if (type.equals(PushType.MESSAGE.toString())) {
            isMessage = true;
            intent.putExtra(KEY_OPEN_FRAGMENT, OPEN_FRAGMENT_MESSAGES);
        } else if (type.equals(PushType.WORKOUT.toString())) {
            isWorkout = true;

            if (pushIntent.hasExtra("wid")) {
                String workoutId = pushIntent.getStringExtra("wid");
                intent.putExtra(KEY_SERVER_WORKOUT_ID, Integer.parseInt(workoutId));
                intent.putExtra(KEY_OPEN_FRAGMENT, OPEN_FRAGMENT_WORKOUTS);
            }

        } else if (type.equals(PushType.DIET.toString())) {
            if (pushIntent.hasExtra("did")) {
                String dietId = pushIntent.getStringExtra("did");
                intent.putExtra(KEY_SERVER_DIET_ID, Integer.parseInt(dietId));
                intent.putExtra(KEY_OPEN_FRAGMENT, OPEN_FRAGMENT_DIETS);
            }
            isDiet = true;
        }


        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 600, 100, 600})
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setOnlyAlertOnce(false)
                .setSmallIcon(isMessage ? R.drawable.icon_mail_white :
                        (isWorkout ? R.drawable.icon_workout_white :
                                R.drawable.icon_food_white))
                .setContentTitle(context.getString(R.string.app_name))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentText(message);

        mBuilder.setContentIntent(contentIntent);

        Notification notification = mBuilder.build();
        notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (isMessage) {
            notificationManager.notify(MESSAGE_NOTIFICATION_ID, notification);
        } else {
            notificationManager.notify(++NOTIFICATION_ID, notification);
        }

    }

}
