package nu.pto.androidapp.base;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nu.pto.androidapp.base.adapter.MenuAdapter;
import nu.pto.androidapp.base.db.dao.ImageDao;
import nu.pto.androidapp.base.db.dao.MessageDao;
import nu.pto.androidapp.base.db.model.Diet;
import nu.pto.androidapp.base.db.model.Image;
import nu.pto.androidapp.base.db.model.Workout;
import nu.pto.androidapp.base.fragment.DietFragment;
import nu.pto.androidapp.base.fragment.DietListFragment;
import nu.pto.androidapp.base.fragment.LogoutFragment;
import nu.pto.androidapp.base.fragment.MessagesFragment;
import nu.pto.androidapp.base.fragment.SettingsFragment;
import nu.pto.androidapp.base.fragment.StartFragment;
import nu.pto.androidapp.base.fragment.StatisticsChartFragment;
import nu.pto.androidapp.base.fragment.TechnicalSupportFragment;
import nu.pto.androidapp.base.fragment.WorkoutsFragment;
import nu.pto.androidapp.base.model.MenuItem;
import nu.pto.androidapp.base.receiver.GcmBroadcastReceiver;
import nu.pto.androidapp.base.ui.PtoDrawerLayout;
import nu.pto.androidapp.base.util.Constants;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;
import nu.pto.androidapp.base.util.Utils;

public class AppActivity extends BaseActivity {

    PtoDrawerLayout drawerLayout;
    ListView menuList;
    MenuItem messagesMenuItem;

    private BroadcastReceiver networkStateBroadcastReceiver;

    private BroadcastReceiver syncCompleteBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(BaseActivity.LOG_TAG, "SYNC COMPLETE-APP ACTIVITY");
            try {
                int clientId = SharedPreferencesHelper.getInt(AppActivity.this, SharedPreferencesHelper.KEY_CLIENT_ID);
                MessageDao messageDao = getDatabaseHelper().getMessageDao();
                int unreadMessagesCount = messageDao.getUnreadMessagesCount(clientId);
                setUnreadMessagesCount(unreadMessagesCount);


                ImageDao imageDao = getDatabaseHelper().getImageDao();
                List<Image> imagesToDownload = new ArrayList<Image>();
                imagesToDownload = imageDao.getImagesWithEmptyContent();

                if (imagesToDownload != null && imagesToDownload.size() > 0) {
                    DownloadImageTask downloadImageTask = new BaseActivity.DownloadImageTask();
                    downloadImageTask.execute(imagesToDownload);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    };

    private BroadcastReceiver pushReceivedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(BaseActivity.LOG_TAG, "PUSH RECEIVED");
            doSync();
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        configureDrawerLayout();

        if (getIntent().hasExtra(GcmBroadcastReceiver.KEY_OPEN_FRAGMENT)) {
            onNewIntent(getIntent());
        } else {
            setContent(StartFragment.class, null, false);
        }

    }


    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);

        if (!intent.hasExtra(GcmBroadcastReceiver.KEY_OPEN_FRAGMENT)) {
            return;
        }

        if (intent.getIntExtra(GcmBroadcastReceiver.KEY_OPEN_FRAGMENT, -1) == GcmBroadcastReceiver.OPEN_FRAGMENT_MESSAGES) {
            setContent(MessagesFragment.class, null, true);
        } else if (intent.getIntExtra(GcmBroadcastReceiver.KEY_OPEN_FRAGMENT, -1) == GcmBroadcastReceiver.OPEN_FRAGMENT_DIETS) {
            Bundle bundle = new Bundle();
            int serverDietId = intent.getIntExtra(GcmBroadcastReceiver.KEY_SERVER_DIET_ID, -1);

            Diet diet = null;
            try {
                diet = getDatabaseHelper().getDietDao().getDietByServerDietId(serverDietId);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (diet != null) {
                bundle.putInt("diet_id", diet.dietId);
                setContent(DietFragment.class, bundle, true);
            } else {
                setContent(DietListFragment.class, bundle, true);
            }

        } else if (intent.getIntExtra(GcmBroadcastReceiver.KEY_OPEN_FRAGMENT, -1) == GcmBroadcastReceiver.OPEN_FRAGMENT_WORKOUTS) {
            Bundle bundle = new Bundle();
            int serverWorkoutId = intent.getIntExtra(GcmBroadcastReceiver.KEY_SERVER_WORKOUT_ID, -1);
            bundle.putInt("server_workout_id", serverWorkoutId);
            Workout workout = null;
            try {
                workout = getDatabaseHelper().getWorkoutDao().getWorkoutByServerWorkoutId(serverWorkoutId);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (workout != null) {
                Intent workoutIntent = new Intent(AppActivity.this, CustomActionBarActivity.class);
                workoutIntent.putExtra("FRAGMENT_NAME", "WorkoutFragment");
                workoutIntent.putExtra("FRAGMENT_WORKOUT_ID", workout.workoutId);
                startActivity(workoutIntent);

            } else {
                setContent(WorkoutsFragment.class, bundle, true);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkStateBroadcastReceiver = new AppBroadcastReceiver();
        registerReceiver(networkStateBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        LocalBroadcastManager.getInstance(this).registerReceiver(syncCompleteBroadcastReceiver, new IntentFilter(BaseActivity.ACTION_SYNC_COMPLETE));
        registerReceiver(pushReceivedBroadcastReceiver, new IntentFilter(BaseActivity.ACTION_PUSH_RECEIVER));
    }

    @Override
    protected void onPause() {
        try {
            if (networkStateBroadcastReceiver != null){
                unregisterReceiver(networkStateBroadcastReceiver);
                networkStateBroadcastReceiver = null;
            }
        } catch (IllegalArgumentException ignored) {

        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncCompleteBroadcastReceiver);
        unregisterReceiver(pushReceivedBroadcastReceiver);
        super.onPause();
    }


    private void configureDrawerLayout() {
        drawerLayout = (PtoDrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        drawerLayout.setDrawerListener(new PtoDrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                hideKeyboard();
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        menuList = (ListView) findViewById(R.id.menu_list);

        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        final float screenWidth = displayMetrics.widthPixels;
        PtoDrawerLayout.LayoutParams lp = (PtoDrawerLayout.LayoutParams) menuList.getLayoutParams();
        lp.width = (int) (screenWidth * 0.7);
        menuList.setLayoutParams(lp);

        menuList.setAdapter(new MenuAdapter(this, getMenuItems(), new MenuAdapter.MenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position) {
                switch (position) {
                    case 6:
                        sendGoogleAnalyticsData(Constants.ANALYTICS_EVENT_SETTINGS);
                        break;
                    case 3:
                        sendGoogleAnalyticsData(Constants.ANALYTICS_EVENT_STATISTICS);
                }
                MenuItem item = (MenuItem) menuList.getAdapter().getItem(position);
                drawerLayout.closeDrawer(Gravity.START);
                if (item.getFragmentClass() != null) {
                    if (item.getFragmentClass().equals(LogoutFragment.class) && !Utils.isInternetConnected(AppActivity.this)) {
                        AlertDialog logoutNoInternetAlertDialog = new AlertDialog.Builder(AppActivity.this)
                                .setTitle(getString(R.string.logout_no_internet_alert_title))
                                .setMessage(R.string.logout_no_internet_alert_message)
                                .setPositiveButton(android.R.string.yes, null)
                                .create();
                        logoutNoInternetAlertDialog.show();
                    } else {
                        setContent(item.getFragmentClass(), null);
                    }

                }
            }
        }));
    }

    private List<MenuItem> getMenuItems() {
        List<MenuItem> menuItems = new ArrayList<MenuItem>();

        menuItems.add(new MenuItem(R.drawable.icon_house, R.drawable.icon_house_white, R.string.menu_title_home, StartFragment.class));
        menuItems.add(new MenuItem(R.drawable.icon_workout, R.drawable.icon_workout_white, R.string.menu_title_workouts, WorkoutsFragment.class));
        menuItems.add(new MenuItem(R.drawable.icon_food, R.drawable.icon_food_white, R.string.menu_title_diet, DietListFragment.class));
        menuItems.add(new MenuItem(R.drawable.icon_stats, R.drawable.icon_stats_white, R.string.menu_title_statistics, StatisticsChartFragment.class));
        messagesMenuItem = new MenuItem(R.drawable.icon_mail, R.drawable.icon_mail_white, R.string.menu_title_messages, MessagesFragment.class);
        menuItems.add(messagesMenuItem);


        menuItems.add(new MenuItem());
        menuItems.add(new MenuItem(0, 0, R.string.menu_title_settings, SettingsFragment.class));
        menuItems.add(new MenuItem(0, 0, R.string.menu_title_tech_support, TechnicalSupportFragment.class));
        menuItems.add(new MenuItem(0, 0, R.string.menu_title_logout, LogoutFragment.class));

        return menuItems;
    }


    public void onMenuIconClick(View v) {

        hideKeyboard();

        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            drawerLayout.openDrawer(Gravity.START);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    public void setUnreadMessagesCount(int count) {
        messagesMenuItem.setBadgeValue(count);
        ((BaseAdapter) menuList.getAdapter()).notifyDataSetChanged();
    }


    public class AppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                boolean available = info.isAvailable();
                Log.i("BROADCAST", "Network Type: " + info.getTypeName()
                        + ", subtype: " + info.getSubtypeName()
                        + ", available: " + available);


                if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    doSync();
                }
            }

        }
    }
}
