package nu.pto.androidapp.base.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import nu.pto.androidapp.base.db.dao.ClientDao;
import nu.pto.androidapp.base.db.dao.ClientPackageDao;
import nu.pto.androidapp.base.db.dao.DietCategoryDao;
import nu.pto.androidapp.base.db.dao.DietDao;
import nu.pto.androidapp.base.db.dao.ExerciseDao;
import nu.pto.androidapp.base.db.dao.ExerciseTypeDao;
import nu.pto.androidapp.base.db.dao.ImageDao;
import nu.pto.androidapp.base.db.dao.MessageDao;
import nu.pto.androidapp.base.db.dao.MuscleGroupDao;
import nu.pto.androidapp.base.db.dao.PackageCategoryDao;
import nu.pto.androidapp.base.db.dao.PackageDao;
import nu.pto.androidapp.base.db.dao.PackageLevelDao;
import nu.pto.androidapp.base.db.dao.PackagePriceDao;
import nu.pto.androidapp.base.db.dao.PackageWeekDao;
import nu.pto.androidapp.base.db.dao.QuestionDao;
import nu.pto.androidapp.base.db.dao.SetDao;
import nu.pto.androidapp.base.db.dao.SetSessionDao;
import nu.pto.androidapp.base.db.dao.TrainerDao;
import nu.pto.androidapp.base.db.dao.WorkoutCategoryDao;
import nu.pto.androidapp.base.db.dao.WorkoutDao;
import nu.pto.androidapp.base.db.dao.WorkoutSessionDao;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME = "db.sqlite";
    public static final int DATABASE_VERSION = 2;

    private Context context;

    private ConnectionSource connectionSource;

    private PackageLevelDao packageLevelDao = null;
    private PackagePriceDao packagePriceDao = null;
    private PackageWeekDao packageWeekDao = null;

    private MuscleGroupDao muscleGroupDao = null;

    private ImageDao imageDao = null;

    private TrainerDao trainerDao = null;
    private ClientDao clientDao = null;

    private PackageCategoryDao packageCategoryDao = null;
    private PackageDao packageDao = null;
    private ClientPackageDao clientPackageDao = null;

    private DietDao dietDao = null;
    private DietCategoryDao dietCategoryDao = null;


    private WorkoutCategoryDao workoutCategoryDao = null;
    private WorkoutDao workoutDao = null;

    private ExerciseTypeDao exerciseTypeDao = null;
    private ExerciseDao exerciseDao = null;

    private SetDao setDao = null;

    private WorkoutSessionDao workoutSessionDao = null;
    private SetSessionDao setSessionDao = null;

    private MessageDao messageDao = null;

    private QuestionDao questionDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        connectionSource = getConnectionSource();
    }


    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        // TODO this should not
//		String dbPath = DATABASE_PATH + DATABASE_NAME;
//		mDataBase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        Log.i("DATABASE", "CREATE database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // TODO this should not
        Log.i("___DATABASE", "upgrade database");
        try {
            db.execSQL("ALTER TABLE `message` ADD COLUMN `received` INTEGER NOT NULL DEFAULT 0");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ImageDao getImageDao() throws SQLException {
        if (imageDao == null) {
            imageDao = new ImageDao(this.connectionSource);
        }
        return imageDao;
    }


    /**
     * @return
     * @throws java.sql.SQLException
     */
    public TrainerDao getTrainerDao() throws SQLException {
        if (trainerDao == null) {
            trainerDao = new TrainerDao(this.connectionSource);
        }
        return trainerDao;
    }

    /**
     * returns client DAO
     *
     * @return Client Dao object
     * @throws java.sql.SQLException
     */
    public ClientDao getClientDao() throws SQLException {
        if (clientDao == null) {
            clientDao = new ClientDao(this.connectionSource);
        }
        return clientDao;
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public PackageCategoryDao getPackageCategoryDao() throws SQLException {
        if (packageCategoryDao == null) {
            packageCategoryDao = new PackageCategoryDao(this.connectionSource);
        }
        return packageCategoryDao;
    }

    /**
     * @return package dao
     * @throws java.sql.SQLException
     */
    public PackageDao getPackageDao() throws SQLException {
        if (packageDao == null) {
            packageDao = new PackageDao(this.connectionSource);
        }
        return packageDao;
    }


    public PackageLevelDao getPackageLevelDao() throws SQLException {
        if (packageLevelDao == null) {
            packageLevelDao = new PackageLevelDao(this.connectionSource);
        }
        return packageLevelDao;
    }

    public PackagePriceDao getPackagePriceDao() throws SQLException {
        if (packagePriceDao == null) {
            packagePriceDao = new PackagePriceDao(this.connectionSource);
        }
        return packagePriceDao;
    }

    public PackageWeekDao getPackageWeekDao() throws SQLException {
        if (packageWeekDao == null) {
            packageWeekDao = new PackageWeekDao(this.connectionSource);
        }
        return packageWeekDao;
    }

    public MuscleGroupDao getMuscleGroupDao() throws SQLException {
        if (muscleGroupDao == null) {
            muscleGroupDao = new MuscleGroupDao(this.connectionSource);
        }
        return muscleGroupDao;
    }

    public ClientPackageDao getClientPackageDao() throws SQLException {
        if (clientPackageDao == null) {
            clientPackageDao = new ClientPackageDao(this.connectionSource);
        }
        return clientPackageDao;
    }

    public DietCategoryDao getDietCategoryDao() throws SQLException {
        if (dietCategoryDao == null) {
            dietCategoryDao = new DietCategoryDao(this.connectionSource);
        }
        return dietCategoryDao;
    }

    public DietDao getDietDao() throws SQLException {
        if (dietDao == null) {
            dietDao = new DietDao(this.connectionSource);
        }
        return dietDao;
    }

    public WorkoutCategoryDao getWorkoutCategoryDao() throws SQLException {
        if (workoutCategoryDao == null) {
            workoutCategoryDao = new WorkoutCategoryDao(this.connectionSource);
        }
        return workoutCategoryDao;
    }

    public WorkoutDao getWorkoutDao() throws SQLException {
        if (workoutDao == null) {
            workoutDao = new WorkoutDao(this.connectionSource);
        }
        return workoutDao;
    }

    public ExerciseTypeDao getExerciseTypeDao() throws SQLException {
        if (exerciseTypeDao == null) {
            exerciseTypeDao = new ExerciseTypeDao(this.connectionSource);
        }
        return exerciseTypeDao;
    }

    public ExerciseDao getExerciseDao() throws SQLException {
        if (exerciseDao == null) {
            exerciseDao = new ExerciseDao(this.connectionSource);
        }
        return exerciseDao;
    }

    public SetDao getSetDao() throws SQLException {
        if (setDao == null) {
            setDao = new SetDao(this.connectionSource);
        }
        return setDao;
    }


    public SetSessionDao getSetSessionDao() throws SQLException {
        if (setSessionDao == null) {
            setSessionDao = new SetSessionDao(this.connectionSource);
        }
        return setSessionDao;
    }

    public WorkoutSessionDao getWorkoutSessionDao() throws SQLException {
        if (workoutSessionDao == null) {
            workoutSessionDao = new WorkoutSessionDao(this.connectionSource);
        }
        return workoutSessionDao;
    }

    public MessageDao getMessageDao() throws SQLException {
        if (messageDao == null) {
            messageDao = new MessageDao(this.connectionSource);
        }
        return messageDao;
    }


    public QuestionDao getQuestionDao() throws SQLException {
        if (questionDao == null) {
            questionDao = new QuestionDao(this.connectionSource);
        }
        return questionDao;
    }

    @Override
    public void close() {
        super.close();
        imageDao = null;
        trainerDao = null;
    }


}
