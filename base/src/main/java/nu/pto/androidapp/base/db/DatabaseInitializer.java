package nu.pto.androidapp.base.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseInitializer extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/%1/databases/";

    private SQLiteDatabase database;
    private final Context context;

    public DatabaseInitializer(Context context) {
        super(context, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION);
        this.context = context;

        DB_PATH = DB_PATH.replace("%1",context.getPackageName());
    }



    public void createDatabase() throws IOException {

        boolean dbExist = checkDatabase();

        if (!dbExist) {
            database = this.getReadableDatabase();
            try {

                InputStream myInput = context.getAssets().open(DatabaseHelper.DATABASE_NAME);

                String outFileName = DB_PATH + DatabaseHelper.DATABASE_NAME;

                OutputStream myOutput = new FileOutputStream(outFileName);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();

            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
        } else {
            database = this.getReadableDatabase();

            String queryString = "SELECT * FROM `exercise` LIMIT 1";
            Cursor cursor = database.rawQuery(queryString, null);
            if (cursor.getColumnIndex("video_description") == -1) {
                String queryString2 = "ALTER TABLE `exercise` ADD COLUMN \"video_description\" TEXT ;";
                database.execSQL(queryString2);

                close();

                database = this.getReadableDatabase();

            }
        }
    }

    private boolean checkDatabase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DatabaseHelper.DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }


    @Override
    public synchronized void close() {
        if (database != null)
            database.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("___DATABASE", "upgrade database");
        try {
            db.execSQL("ALTER TABLE `message` ADD COLUMN `received` INTEGER NOT NULL DEFAULT 0");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
