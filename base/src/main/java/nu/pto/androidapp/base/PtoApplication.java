package nu.pto.androidapp.base;

import android.app.Application;
import android.content.Context;

import nu.pto.androidapp.base.db.DatabaseHelper;

public class PtoApplication extends Application {

    private static PtoApplication ptoApplication;

    private static DatabaseHelper databaseHelper = null;

    @Override
    public void onCreate() {
        super.onCreate();

        ptoApplication = this;

    }

    public static PtoApplication getInstance() {
        return ptoApplication;
    }

    public Context getContext() {
        return ptoApplication.getApplicationContext();
    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(this);
        }
        return databaseHelper;
    }


}
