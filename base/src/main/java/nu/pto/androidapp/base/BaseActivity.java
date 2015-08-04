package nu.pto.androidapp.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.GsonRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.misc.TransactionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import nu.pto.androidapp.base.db.DatabaseHelper;
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
import nu.pto.androidapp.base.db.model.Client;
import nu.pto.androidapp.base.db.model.ClientPackage;
import nu.pto.androidapp.base.db.model.Diet;
import nu.pto.androidapp.base.db.model.DietCategory;
import nu.pto.androidapp.base.db.model.Exercise;
import nu.pto.androidapp.base.db.model.ExerciseType;
import nu.pto.androidapp.base.db.model.Image;
import nu.pto.androidapp.base.db.model.Message;
import nu.pto.androidapp.base.db.model.MuscleGroup;
import nu.pto.androidapp.base.db.model.Package;
import nu.pto.androidapp.base.db.model.PackageCategory;
import nu.pto.androidapp.base.db.model.PackageLevel;
import nu.pto.androidapp.base.db.model.PackagePrice;
import nu.pto.androidapp.base.db.model.PackageWeek;
import nu.pto.androidapp.base.db.model.Question;
import nu.pto.androidapp.base.db.model.QuestionAnswer;
import nu.pto.androidapp.base.db.model.Set;
import nu.pto.androidapp.base.db.model.SetSession;
import nu.pto.androidapp.base.db.model.Trainer;
import nu.pto.androidapp.base.db.model.Workout;
import nu.pto.androidapp.base.db.model.WorkoutCategory;
import nu.pto.androidapp.base.db.model.WorkoutSession;
import nu.pto.androidapp.base.model.RequestGson;
import nu.pto.androidapp.base.model.ResponseGson;
import nu.pto.androidapp.base.ui.LoadingDialog;
import nu.pto.androidapp.base.util.Constants;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;
import nu.pto.androidapp.base.util.SyncStatus;


public class BaseActivity extends Activity implements Response.ErrorListener {

    public static BaseActivity staticInstance;

    public static String ACTION_SYNC_COMPLETE = "nu.pto.androidapp.sync.complete";
    public static String ACTION_PUSH_RECEIVER = "nu.pto.androidapp.push.received";
    public static String ACTION_NO_TRAINER = "nu.pto.androidapp.no.trainer";


    public static String LOG_TAG = "PTO";

    private static final int SYNC_INTERVAL = 40 * 1000; // in milliseconds

    static String API_BASE_URL = "";

    protected RequestQueue requestQueue;

    private boolean doLogoutSyncNow;

    private Map<String, String> commonHeaders;

    private LoadingDialog loadingDialog;

    private Runnable syncRunnable = new Runnable() {
        @Override
        public void run() {
            doSync();
        }
    };

    private Handler syncScheduleHandler;

    public DatabaseHelper getDatabaseHelper() {
        return PtoApplication.getInstance().getDatabaseHelper();
    }


    private synchronized Tracker getGoogleAnalyticsTracker() {
        if (getString(R.string.is_lofsan).equals("1")) {
            return null;
        }
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//      "UA-51064052-1"
        return analytics.getTracker("UA-46857589-1");
    }

    public void sendGoogleAnalyticsData(String eventName) {
        if (getString(R.string.is_lofsan).equals("1")) {
            return;
        }

        Tracker t = getGoogleAnalyticsTracker();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("t", "event");
        data.put("categoryId", Constants.ANALYTICS_CATEGORY);
        data.put("labelId", eventName);
        t.send(data);
    }


    public void setContent(Class<? extends Fragment> fragmentClass, Bundle arguments) {
        setContent(fragmentClass, arguments, true);
    }

    public void setContent(Class<? extends Fragment> fragmentClass, Bundle arguments, boolean addToBackStack) {
        Fragment fragment = null;
        try {
            fragment = fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        setContent(fragment, arguments, addToBackStack);
    }

    public void setContent(Fragment fragment, Bundle arguments) {
        setContent(fragment, arguments, true);
    }

    public void setContent(Fragment fragment, Bundle arguments, boolean addToBackStack) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        fragment.setArguments(arguments);

        transaction.replace(R.id.content_layout, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }

        transaction.commit();
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        staticInstance = this;

        API_BASE_URL = getString(R.string.api_base_url);

        loadingDialog = new LoadingDialog(this, R.style.LoadingDialog);

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();

        final String deviceId = SharedPreferencesHelper.getString(this, SharedPreferencesHelper.KEY_DEVICE_ID);
        final String deviceToken = SharedPreferencesHelper.getString(this, SharedPreferencesHelper.KEY_REG_ID);

        String versionCode = "";
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            // Huh? Really?
        }
        final String isEleiko = getString(R.string.is_eleiko);
        final String isLofsan = getString(R.string.is_lofsan);

        commonHeaders = new HashMap<String, String>();
        commonHeaders.put("Device-Id", deviceId);
        commonHeaders.put("Version-Code", versionCode);
        commonHeaders.put("Is-Eleiko", isEleiko);
        commonHeaders.put("Is-Lofsan", isLofsan);
        commonHeaders.put("Device-Token", deviceToken);
        commonHeaders.put("Platform", "android");
        commonHeaders.put("Cookie", "XDEBUG_SESSION=19137");

        String creds = String.format("%s:%s", getString(R.string.basic_auth_login), getString(R.string.basic_auth_password));
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        commonHeaders.put("Authorization", auth);

        syncScheduleHandler = new Handler();
    }

    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
        super.onDestroy();
    }

    /**
     * login request
     *
     * @param email    email
     * @param password password
     * @param listener success response listener
     */
    public void doLoginRequest(String email, String password, Response.Listener<JSONObject> listener) {
        String loginUrl = API_BASE_URL + "/syncv2/auth/login";
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // create request for login
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, loginUrl, params, listener, this) {

            /**
             * override get headers to add custom headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.putAll(commonHeaders);
                headers.putAll(super.getHeaders());
                return headers;
            }

            /**
             * overriding parse network response to get response headers
             */
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                String accessToken = response.headers.get("Access-Token");
                // store access token in shared prefs
                SharedPreferencesHelper.putString(BaseActivity.this, SharedPreferencesHelper.KEY_ACCESS_TOKEN, accessToken);
                return super.parseNetworkResponse(response);
            }
        };
        // set tag auth
        loginRequest.setTag("login");
        loginRequest.setShouldCache(false);
        requestQueue.add(loginRequest);
    }

    /**
     * request for forgot password
     *
     * @param email    user email
     * @param listener response listener
     */
    public void doForgotPasswordRequest(String email, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String passwordChangeUrl = API_BASE_URL + "/syncv2/auth/forgetpassword";
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // create request for login
        JsonObjectRequest forgotPasswordRequest = new JsonObjectRequest(Request.Method.POST, passwordChangeUrl, params, listener, errorListener) {

            /**
             * override get headers to add custom headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.putAll(commonHeaders);
                headers.putAll(super.getHeaders());
                return headers;
            }
        };
        forgotPasswordRequest.setTag("forgot_password");
        forgotPasswordRequest.setShouldCache(false);
        requestQueue.add(forgotPasswordRequest);
        showLoadingDialog();
    }

    /**
     * change password request
     *
     * @param oldPassword old password
     * @param newPassword new password
     * @param listener    success response listener
     */
    public void doChangePasswordRequest(String oldPassword, String newPassword, Response.Listener<JSONObject> listener) {
        String passwordChangeUrl = API_BASE_URL + "/syncv2/auth/changepassword";
        JSONObject params = new JSONObject();
        try {
            params.put("old_password", oldPassword);
            params.put("password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // create request for login
        JsonObjectRequest changePasswordRequest = new JsonObjectRequest(Request.Method.POST, passwordChangeUrl, params, listener, this) {

            /**
             * override get headers to add custom headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.putAll(commonHeaders);
                headers.put("Access-Token", SharedPreferencesHelper.getString(BaseActivity.this, SharedPreferencesHelper.KEY_ACCESS_TOKEN));
                headers.putAll(super.getHeaders());
                return headers;
            }
        };
        changePasswordRequest.setShouldCache(false);
        changePasswordRequest.setTag("change_password");
        requestQueue.add(changePasswordRequest);
        showLoadingDialog();
    }


    public void doUnSubscribeRequest(Response.Listener<JSONObject> listener) {
        String passwordChangeUrl = API_BASE_URL + "/syncv2/client/unsubscribe";
        JSONObject params = new JSONObject();

        // create request for login
        JsonObjectRequest unSubscribeRequest = new JsonObjectRequest(Request.Method.POST, passwordChangeUrl, params, listener, this) {

            /**
             * override get headers to add custom headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.putAll(commonHeaders);
                headers.put("Access-Token", SharedPreferencesHelper.getString(BaseActivity.this, SharedPreferencesHelper.KEY_ACCESS_TOKEN));
                headers.putAll(super.getHeaders());
                return headers;
            }
        };
        unSubscribeRequest.setShouldCache(false);
        unSubscribeRequest.setTag("un_subscribe");
        requestQueue.add(unSubscribeRequest);
    }

    public void doFullSync(SyncCompleteListener syncCompleteListener) {
        try {
            doSync(true, false, false, syncCompleteListener);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doLogoutSync(SyncCompleteListener syncCompleteListener) {
        try {
            syncScheduleHandler.removeCallbacks(syncRunnable);
            doSync(false, true, false, syncCompleteListener);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doForceLogoutSync(SyncCompleteListener syncCompleteListener) {
        try {
            syncScheduleHandler.removeCallbacks(syncRunnable);
            doSync(false, true, true, syncCompleteListener);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doSync() {
        try {
            String accessToken = SharedPreferencesHelper.getString(this, SharedPreferencesHelper.KEY_ACCESS_TOKEN);
            if (!accessToken.isEmpty()) {
                doSync(false, false, false, null);
                syncScheduleHandler.removeCallbacks(syncRunnable);
                syncScheduleHandler.postDelayed(syncRunnable, SYNC_INTERVAL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param doFullSync        boolean
     * @param doLogoutSync      boolean
     * @param doForceLogoutSync boolean
     */
    public void doSync(final boolean doFullSync, final boolean doLogoutSync, final boolean doForceLogoutSync, final SyncCompleteListener syncCompleteListener) throws SQLException {

        Log.d(BaseActivity.LOG_TAG, "Start Sync");

        doLogoutSyncNow = doLogoutSync;

        final PackagePriceDao packagePriceDao = getDatabaseHelper().getPackagePriceDao();
        final PackageWeekDao packageWeekDao = getDatabaseHelper().getPackageWeekDao();
        final PackageLevelDao packageLevelDao = getDatabaseHelper().getPackageLevelDao();
        final MuscleGroupDao muscleGroupDao = getDatabaseHelper().getMuscleGroupDao();

        final ImageDao imageDao = getDatabaseHelper().getImageDao();
        final PackageCategoryDao packageCategoryDao = getDatabaseHelper().getPackageCategoryDao();
        final PackageDao packageDao = getDatabaseHelper().getPackageDao();
        final TrainerDao trainerDao = getDatabaseHelper().getTrainerDao();
        final ClientDao clientDao = getDatabaseHelper().getClientDao();
        final ClientPackageDao clientPackageDao = getDatabaseHelper().getClientPackageDao();

        final DietDao dietDao = getDatabaseHelper().getDietDao();
        final DietCategoryDao dietCategoryDao = getDatabaseHelper().getDietCategoryDao();

        final WorkoutCategoryDao workoutCategoryDao = getDatabaseHelper().getWorkoutCategoryDao();
        final WorkoutDao workoutDao = getDatabaseHelper().getWorkoutDao();

        final ExerciseTypeDao exerciseTypeDao = getDatabaseHelper().getExerciseTypeDao();
        final ExerciseDao exerciseDao = getDatabaseHelper().getExerciseDao();

        final SetDao setDao = getDatabaseHelper().getSetDao();

        final WorkoutSessionDao workoutSessionDao = getDatabaseHelper().getWorkoutSessionDao();
        final SetSessionDao setSessionDao = getDatabaseHelper().getSetSessionDao();

        final MessageDao messageDao = getDatabaseHelper().getMessageDao();

        final QuestionDao questionDao = getDatabaseHelper().getQuestionDao();

        String url;

        // getting user type
        String userType = SharedPreferencesHelper.getString(this, SharedPreferencesHelper.KEY_USER_TYPE);
        // getting user's access token
        final String accessToken = SharedPreferencesHelper.getString(this, SharedPreferencesHelper.KEY_ACCESS_TOKEN);

        if (accessToken.isEmpty()) {
            return;
        }

        if (userType.equals("client")) {
            url = API_BASE_URL + "/syncv2/client";
        } else {
            url = API_BASE_URL + "/syncv2/trainer";
        }

        RequestGson requestGson = new RequestGson();

        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();


        if (doLogoutSync) {
            requestGson.setLogout(1);
        }

        List<Client> clients = clientDao.getUpdatedClients();
        List<Trainer> trainers = trainerDao.getUpdatedTrainers();
        List<SetSession> setSessions = setSessionDao.getUpdatedOrNewSetSessions();
        List<WorkoutSession> workoutSessions = workoutSessionDao.getUpdatedOrNewWorkoutSessions();
        List<Message> messages = messageDao.getUpdatedOrNewMessages();

        // add question answers to client object
        List<Question> questions = questionDao.queryForAll();
        for (Client client : clients) {
            ClientPackage clientPackage = clientPackageDao.getClientPackageByClientIdAndPackageId(client.clientId, client.packageId);

            if (clientPackage != null) {

                for (Question question : questions) {
                    QuestionAnswer questionAnswer = new QuestionAnswer();
                    questionAnswer.question = question.question;

                    if (question.id == 1) questionAnswer.answer = clientPackage.answer1;
                    if (question.id == 2) questionAnswer.answer = clientPackage.answer2;
                    if (question.id == 3) questionAnswer.answer = clientPackage.answer3;
                    if (question.id == 4) questionAnswer.answer = clientPackage.answer4;
                    if (question.id == 5) questionAnswer.answer = clientPackage.answer5;
                    if (question.id == 6) questionAnswer.answer = clientPackage.answer6;
                    if (question.id == 7) questionAnswer.answer = clientPackage.answer7;
                    if (question.id == 8) questionAnswer.answer = clientPackage.answer8;
                    if (question.id == 9) questionAnswer.answer = clientPackage.answer9;
                    if (question.id == 10) questionAnswer.answer = clientPackage.answer10;
                    if (question.id == 11) questionAnswer.answer = clientPackage.answer11;
                    if (question.id == 12) questionAnswer.answer = clientPackage.answer12;
                    if (question.id == 13) questionAnswer.answer = clientPackage.answer13;
                    if (question.id == 14) questionAnswer.answer = clientPackage.answer14;
                    if (question.id == 15) questionAnswer.answer = clientPackage.answer15;
                    if (question.id == 16) questionAnswer.answer = clientPackage.answer16;
                    if (question.id == 17) questionAnswer.answer = clientPackage.answer17;
                    if (question.id == 18) questionAnswer.answer = clientPackage.answer18;

                    client.questionsAnswers.add(questionAnswer);
                }
            }
        }

        requestGson.setTrainers(trainers);
        requestGson.setClients(clients);
        requestGson.setWorkoutSessions(workoutSessions);
        requestGson.setSetSessions(setSessions);
        requestGson.setMessages(messages);

        String paramsJsonString = gson.toJson(requestGson);

        Log.i(LOG_TAG, paramsJsonString);

        // create request for login
        GsonRequest<ResponseGson> syncRequest = new GsonRequest<ResponseGson>(Request.Method.POST, url, paramsJsonString, ResponseGson.class, new Response.Listener<ResponseGson>() {

            @Override
            public void onResponse(final ResponseGson responseGson) {

                if (doLogoutSync || doForceLogoutSync || responseGson == null) {
                    if (syncCompleteListener != null) {
                        syncCompleteListener.onComplete();
                    }
                    return;
                }

                // check for null response
                // this is error case and should not appear
//                if (responseGson == null){
//                    return;
//                }

                new AsyncTask<ResponseGson, Void, Void>() {

                    @Override
                    protected Void doInBackground(ResponseGson... params) {
                        ResponseGson responseGson = params[0];

                        final List<PackageLevel> packageLevels = responseGson.getPackageLevels();
                        final List<PackageWeek> packageWeeks = responseGson.getPackageWeeks();
                        final List<PackagePrice> packagePrices = responseGson.getPackagePrices();
                        final List<MuscleGroup> muscleGroups = responseGson.getMuscleGroups();


                        final List<Image> images = responseGson.getImages();

                        final List<Trainer> trainers = responseGson.getTrainers();
                        final List<Client> clients = responseGson.getClients();

                        final List<PackageCategory> packageCategories = responseGson.getPackageCategories();
                        final List<Package> packages = responseGson.getPackages();
                        final List<ClientPackage> clientPackages = responseGson.getClientPackages();


                        final List<DietCategory> dietCategories = responseGson.getDietCategories();
                        final List<Diet> diets = responseGson.getDiets();

                        final List<WorkoutCategory> workoutCategories = responseGson.getWorkoutCategories();
                        final List<Workout> workouts = responseGson.getWorkouts();

                        final List<ExerciseType> exerciseTypes = responseGson.getExerciseTypes();
                        final List<Exercise> exercises = responseGson.getExercises();

                        final List<Set> sets = responseGson.getSets();

                        final List<SetSession> setSessions = responseGson.getSetSessions();
                        final List<WorkoutSession> workoutSessions = responseGson.getWorkoutSessions();

                        final List<Message> messages = responseGson.getMessages();

                        try {
                            TransactionManager.callInTransaction(getDatabaseHelper().getConnectionSource(), new Callable<Object>() {
                                @Override
                                public Object call() throws Exception {
                                    Log.wtf("*********", "CALLLABLEEE");

                                    /***************************** P A C K A G E   P R I C E S ********************/
                                    Log.wtf("*********", "Storing Package Prices");
                                    for (PackagePrice newPackagePrice : packagePrices) {
                                        PackagePrice packagePrice = packagePriceDao.queryForId(newPackagePrice.id);
                                        if (packagePrice == null) {
                                            packagePriceDao.create(newPackagePrice);
                                        } else {
                                            packagePrice.price = newPackagePrice.price;
                                            packagePriceDao.update(packagePrice);
                                        }

                                    }
                                    packagePrices.clear();

                                    /*************************** P A C K A G E   L E V E L ************************/
                                    Log.wtf("*********", "Storing Package Levels");
                                    for (PackageLevel newPackageLevel : packageLevels) {
                                        PackageLevel packageLevel = packageLevelDao.queryForId(newPackageLevel.id);
                                        if (packageLevel == null) {
                                            packageLevelDao.create(newPackageLevel);
                                        } else {
                                            packageLevel.levelName = newPackageLevel.levelName;
                                            packageLevelDao.update(packageLevel);
                                        }
                                    }

                                    packageLevels.clear();

                                    /*************************** P A C K A G E   W E E K **************************/
                                    Log.wtf("*********", "Storing Package Weeks");
                                    for (PackageWeek newPackageWeek : packageWeeks) {
                                        PackageWeek packageWeek = packageWeekDao.queryForId(newPackageWeek.id);
                                        if (packageWeek == null) {
                                            packageWeekDao.create(newPackageWeek);
                                        } else {
                                            packageWeek.duration = newPackageWeek.duration;
                                            packageWeekDao.update(packageWeek);
                                        }
                                    }

                                    packageWeeks.clear();

                                    /*************************** M U S C U L E   G R O U P ************************/
                                    Log.wtf("*********", "Storing Muscle Groups");
                                    for (MuscleGroup newMuscleGroup : muscleGroups) {
                                        MuscleGroup muscleGroup = muscleGroupDao.queryForId(newMuscleGroup.id);
                                        if (muscleGroup == null) {
                                            muscleGroupDao.create(newMuscleGroup);
                                        } else {
                                            muscleGroup.muscleGroupName = newMuscleGroup.muscleGroupName;
                                            muscleGroup.image = newMuscleGroup.image;
                                            muscleGroupDao.update(muscleGroup);
                                        }
                                    }

                                    muscleGroups.clear();

                                    /************************* E X E R C I S E   T Y P E S  ***********************/
                                    Log.wtf("*********", "Storing Exercise Types");
                                    for (ExerciseType newExerciseType : exerciseTypes) {
                                        ExerciseType exerciseType = exerciseTypeDao.queryForId(newExerciseType.id);

                                        if (exerciseType != null) {
                                            newExerciseType.exerciseTypeName = exerciseType.exerciseTypeName;
                                            newExerciseType.id = exerciseType.id;
                                            exerciseTypeDao.update(newExerciseType);
                                        } else {
                                            exerciseTypeDao.create(newExerciseType);
                                        }

                                    }

                                    exerciseTypes.clear();

                                    /***************************** I M A G E S *************************************/
                                    Log.wtf("*********", "Storing Images");

//                                    List<Image> imagesToDownload = new ArrayList<Image>();
                                    for (Image newImage : images) {
                                        if (newImage._id.trim().isEmpty()) {
                                            continue;
                                        }
                                        Image image = imageDao.queryForId(newImage.get_id());
                                        if (image == null) {
                                            imageDao.create(newImage);
//                                            imagesToDownload.add(newImage);
                                        }
                                    }
                                    images.clear();


                                    /******************************* T R A I N E R **********************************/
                                    Log.wtf("*********", "Storing Trainer");

                                    Trainer loggedClientsTrainer;
                                    Client loggedClient;

                                    for (Trainer newTrainer : trainers) {

                                        if (newTrainer.serverTrainerId != null) {
                                            newTrainer.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            newTrainer.syncStatus = SyncStatus.NEW;
                                        }

                                        Trainer trainer = trainerDao.getTrainerByServerTrainerId(newTrainer.serverTrainerId);

                                        if (trainer != null) {
                                            newTrainer.trainerId = trainer.trainerId;
                                            trainerDao.update(newTrainer);
                                        } else {
                                            trainerDao.create(newTrainer);
                                        }

                                        // add id and updated date to confirm sync array object
                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", newTrainer._id);
                                        jsonObject.put("updated_date", newTrainer.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */
                                    }

                                    trainers.clear();

                                    int serverTrainerId = SharedPreferencesHelper.getInt(BaseActivity.this, SharedPreferencesHelper.KEY_SERVER_TRAINER_ID);
                                    loggedClientsTrainer = trainerDao.getTrainerByServerTrainerId(serverTrainerId);

                                    /*************************************************************************************/
                                    Log.wtf("*********", "Storing Package Categories");

                                    for (PackageCategory newPackageCategory : packageCategories) {

                                        if (newPackageCategory.serverPackageCategoryId != null) {
                                            newPackageCategory.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            newPackageCategory.syncStatus = SyncStatus.NEW;
                                        }

                                        PackageCategory packageCategory = packageCategoryDao.getPackageCategoryByServerPackageCategoryId(newPackageCategory.serverPackageCategoryId);
                                        if (newPackageCategory.serverTrainerId != null) {
                                            if (loggedClientsTrainer != null) {
                                                newPackageCategory.trainerId = loggedClientsTrainer.trainerId;
                                            }
                                        }

                                        if (packageCategory != null) {
                                            newPackageCategory.packageCategoryId = packageCategory.packageCategoryId;
                                            packageCategoryDao.update(newPackageCategory);
                                        } else {
                                            packageCategoryDao.create(newPackageCategory);
                                        }

                                        // add id and updated date to confirm sync array object
                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", newPackageCategory._id);
                                        jsonObject.put("updated_date", newPackageCategory.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */
                                    }

                                    packageCategories.clear();

                                    /*************************************************************************************/
                                    Log.wtf("*********", "Storing Packages");
                                    for (Package aNewPackage : packages) {

                                        if (aNewPackage.serverPackageCategoryId != null) {
                                            PackageCategory packageCategory = packageCategoryDao.getPackageCategoryByServerPackageCategoryId(aNewPackage.serverPackageCategoryId);
                                            aNewPackage.packageCategoryId = packageCategory.packageCategoryId;
                                        }

                                        if (loggedClientsTrainer != null) {
                                            aNewPackage.trainerId = loggedClientsTrainer.trainerId;
                                        }


                                        if (aNewPackage.serverPackageId != null) {
                                            aNewPackage.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            aNewPackage.syncStatus = SyncStatus.NEW;
                                        }

                                        Package aPackage = packageDao.getPackageByServerPackageId(aNewPackage.serverPackageId);

                                        if (aPackage != null) {
                                            aNewPackage.packageId = aPackage.packageId;
                                            packageDao.update(aNewPackage);
                                        } else {
                                            packageDao.create(aNewPackage);
                                        }

                                        // add id and updated date to confirm sync array object
                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", aNewPackage._id);
                                        jsonObject.put("updated_date", aNewPackage.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */
                                    }

                                    packages.clear();

                                    /*************************************************************************************/

                                    Log.wtf("*********", "Storing Clients");
                                    for (Client newClient : clients) {

                                        Package aPackage = packageDao.getPackageByServerPackageId(newClient.serverPackageId);

                                        if (loggedClientsTrainer != null) {
                                            newClient.trainerId = loggedClientsTrainer.trainerId;
                                        }
                                        newClient.packageId = aPackage.packageId;

                                        if (newClient.serverClientId != null) {
                                            newClient.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            newClient.syncStatus = SyncStatus.NEW;
                                        }

                                        Client client = clientDao.getClientByServerClientId(newClient.serverClientId);

                                        if (client != null) {
                                            newClient.clientId = client.clientId;
                                            clientDao.update(newClient);
                                        } else {
                                            clientDao.create(newClient);
                                        }

                                        // add id and updated date to confirm sync array object
                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", newClient._id);
                                        jsonObject.put("updated_date", newClient.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */
                                    }

                                    clients.clear();

                                    // fetch logged client
                                    // todo set client in dictionary

                                    int serverClientId = SharedPreferencesHelper.getInt(BaseActivity.this, SharedPreferencesHelper.KEY_SERVER_CLIENT_ID);
                                    loggedClient = clientDao.getClientByServerClientId(serverClientId);

                                    /*************************************************************************************/
                                    Log.wtf("*********", "Storing Client Packages");
                                    for (ClientPackage newClientPackage : clientPackages) {

                                        Package aPackage = packageDao.getPackageByServerPackageId(newClientPackage.serverPackageId);

                                        if (loggedClientsTrainer != null) {
                                            newClientPackage.trainerId = loggedClientsTrainer.trainerId;
                                        }
                                        newClientPackage.clientId = loggedClient.clientId;
                                        newClientPackage.packageId = aPackage.packageId;

                                        if (newClientPackage.serverClientPackageId != null) {
                                            newClientPackage.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            newClientPackage.syncStatus = SyncStatus.NEW;
                                        }

                                        ClientPackage clientPackage = clientPackageDao.getClientPackageByServerClientPackageId(newClientPackage.serverClientPackageId);

                                        if (clientPackage != null) {
                                            // update client package
                                            newClientPackage.clientPackageId = clientPackage.clientPackageId;
                                            clientPackageDao.update(newClientPackage);
                                        } else {
                                            clientPackageDao.create(newClientPackage);
                                        }

                                        // add id and updated date to confirm sync array object
                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", newClientPackage._id);
                                        jsonObject.put("updated_date", newClientPackage.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */
                                    }

                                    clientPackages.clear();

                                    /*************************************************************************************/
                                    Log.wtf("*********", "Storing Diet Categories");
                                    for (DietCategory newDietCategory : dietCategories) {

                                        if (newDietCategory.serverDietCategoryId != null) {
                                            newDietCategory.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            newDietCategory.syncStatus = SyncStatus.NEW;
                                        }

                                        DietCategory dietCategory = dietCategoryDao.getDietCategoryByServerDietCategoryId(newDietCategory.serverDietCategoryId);

                                        if (dietCategory != null) {
                                            newDietCategory.dietCategoryId = dietCategory.dietCategoryId;
                                            dietCategoryDao.update(newDietCategory);
                                        } else {
                                            dietCategoryDao.create(newDietCategory);
                                        }

                                        // add id and updated date to confirm sync array object
                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", newDietCategory._id);
                                        jsonObject.put("updated_date", newDietCategory.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */
                                    }

                                    dietCategories.clear();

                                    /********************************** D I E T S ***********************************/
                                    Log.wtf("*********", "Storing Diets");
                                    for (Diet newDiet : diets) {
                                        DietCategory dietCategory = dietCategoryDao.getDietCategoryByServerDietCategoryId(newDiet.serverDietCategoryId);

                                        newDiet.clientId = loggedClient.clientId;
                                        if (loggedClientsTrainer != null) {
                                            newDiet.trainerId = loggedClientsTrainer.trainerId;
                                        }
                                        newDiet.dietCategoryId = dietCategory.dietCategoryId;

                                        if (newDiet.serverDietId != null) {
                                            newDiet.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            newDiet.syncStatus = SyncStatus.NEW;
                                        }

                                        Diet diet = dietDao.getDietByServerDietId(newDiet.serverDietId);

                                        if (diet != null) {
                                            newDiet.dietId = diet.dietId;
                                            dietDao.update(newDiet);
                                        } else {
                                            dietDao.create(newDiet);
                                        }

                                        // add id and updated date to confirm sync array object
                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", newDiet._id);
                                        jsonObject.put("updated_date", newDiet.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */
                                    }
                                    diets.clear();

                                    /*************************************************************************************/
                                    Log.wtf("*********", "Storing Workout Categories");
                                    for (WorkoutCategory newWorkoutCategory : workoutCategories) {

                                        if (loggedClientsTrainer != null) {
                                            newWorkoutCategory.trainerId = loggedClientsTrainer.trainerId;
                                        }

                                        if (newWorkoutCategory.serverWorkoutCategoryId != null) {
                                            newWorkoutCategory.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            newWorkoutCategory.syncStatus = SyncStatus.NEW;
                                        }

                                        WorkoutCategory workoutCategory = workoutCategoryDao.getWorkoutCategoryByServerWorkoutCategoryId(newWorkoutCategory.serverWorkoutCategoryId);

                                        if (workoutCategory != null) {
                                            newWorkoutCategory.workoutCategoryId = workoutCategory.workoutCategoryId;
                                            workoutCategoryDao.update(newWorkoutCategory);
                                        } else {
                                            workoutCategoryDao.create(newWorkoutCategory);
                                        }

                                        // add id and updated date to confirm sync array object
                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", newWorkoutCategory._id);
                                        jsonObject.put("updated_date", newWorkoutCategory.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */
                                    }
                                    workoutCategories.clear();

                                    /*************************************************************************************/
                                    Log.wtf("*********", "Storing Workouts");
                                    HashMap<Integer, Workout> workoutHashMap = new HashMap<Integer, Workout>();

                                    for (Workout newWorkout : workouts) {

                                        WorkoutCategory workoutCategory = workoutCategoryDao.getWorkoutCategoryByServerWorkoutCategoryId(newWorkout.serverWorkoutCategoryId);

                                        if (loggedClientsTrainer != null) {
                                            newWorkout.trainerId = loggedClientsTrainer.trainerId;
                                        }
                                        newWorkout.clientId = loggedClient.clientId;
                                        newWorkout.workoutCategoryId = workoutCategory.workoutCategoryId;

                                        if (newWorkout.serverWorkoutId != null) {
                                            newWorkout.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            newWorkout.syncStatus = SyncStatus.NEW;
                                        }

                                        Workout workout = workoutDao.getWorkoutByServerWorkoutId(newWorkout.serverWorkoutId);

                                        if (workout != null) {
                                            newWorkout.workoutId = workout.workoutId;
                                            workoutDao.update(newWorkout);
                                        } else {
                                            workoutDao.create(newWorkout);
                                        }

                                        // put workout
                                        workoutHashMap.put(newWorkout.serverWorkoutId, newWorkout);

                                        // add id and updated date to confirm sync array object
                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", newWorkout._id);
                                        jsonObject.put("updated_date", newWorkout.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */
                                    }

                                    workouts.clear();

                                    /*************************************************************************************/
                                    Log.wtf("*********", "Storing Exercises");
                                    HashMap<Integer, Exercise> exerciseHashMap = new HashMap<Integer, Exercise>();

                                    for (Exercise newExercise : exercises) {

                                        Workout workout;
                                        workout = workoutHashMap.get(newExercise.serverWorkoutId);
                                        if (workout == null) {
                                            workout = workoutDao.getWorkoutByServerWorkoutId(newExercise.serverWorkoutId);
                                        }

                                        newExercise.workoutId = workout.workoutId;
                                        if (loggedClientsTrainer != null) {
                                            newExercise.trainerId = loggedClientsTrainer.trainerId;
                                        }

                                        if (newExercise.serverExerciseId != null) {
                                            newExercise.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            newExercise.syncStatus = SyncStatus.NEW;
                                        }

                                        Exercise exercise = exerciseDao.getExerciseByServerExerciseId(newExercise.serverExerciseId);

                                        if (exercise != null) {
                                            newExercise.exerciseId = exercise.exerciseId;
                                            exerciseDao.update(newExercise);
                                        } else {
                                            exerciseDao.create(newExercise);
                                        }

                                        // put in exercise hash map
                                        exerciseHashMap.put(newExercise.serverExerciseId, newExercise);

                                        // add id and updated date to confirm sync array object
                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", newExercise._id);
                                        jsonObject.put("updated_date", newExercise.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */
                                    }

                                    exercises.clear();

                                    /*************************************************************************************/
                                    Log.wtf("*********", "Storing Sets");
                                    HashMap<Integer, Set> setHashMap = new HashMap<Integer, Set>();
                                    for (Set newSet : sets) {

                                        Workout workout;
                                        workout = workoutHashMap.get(newSet.serverWorkoutId);
                                        if (workout == null) {
                                            workout = workoutDao.getWorkoutByServerWorkoutId(newSet.serverWorkoutId);
                                        }


                                        Exercise exercise;
                                        exercise = exerciseHashMap.get(newSet.serverExerciseId);
                                        if (exercise == null) {
                                            exercise = exerciseDao.getExerciseByServerExerciseId(newSet.serverExerciseId);
                                        }

                                        if (loggedClientsTrainer != null) {
                                            newSet.trainerId = loggedClientsTrainer.trainerId;
                                        }
                                        newSet.workoutId = workout.workoutId;
                                        newSet.exerciseId = exercise.exerciseId;

                                        if (newSet.serverSetId != null) {
                                            newSet.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            newSet.syncStatus = SyncStatus.NEW;
                                        }

                                        Set set = setDao.getSetByServerSetId(newSet.serverSetId);

                                        if (set != null) {
                                            newSet.setId = set.setId;
                                            setDao.update(newSet);
                                        } else {
                                            setDao.create(newSet);
                                        }

                                        // put in sets hash map
                                        setHashMap.put(newSet.serverSetId, newSet);

                                        // add id and updated date to confirm sync array object

                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", newSet._id);
                                        jsonObject.put("updated_date", newSet.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */
                                    }

                                    sets.clear();

                                    /*************************************************************************************/
                                    Log.wtf("*********", "Storing Workout Sessions");
                                    HashMap<Integer, WorkoutSession> workoutSessionHashMap = new HashMap<Integer, WorkoutSession>();
                                    for (WorkoutSession newWorkoutSession : workoutSessions) {

                                        Workout workout;
                                        workout = workoutHashMap.get(newWorkoutSession.serverWorkoutId);
                                        if (workout == null) {
                                            workout = workoutDao.getWorkoutByServerWorkoutId(newWorkoutSession.serverWorkoutId);
                                        }

                                        newWorkoutSession.clientId = loggedClient.clientId;
                                        newWorkoutSession.workoutId = workout.workoutId;

                                        if (newWorkoutSession.serverWorkoutSessionId != null) {
                                            newWorkoutSession.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            newWorkoutSession.syncStatus = SyncStatus.NEW;
                                        }

                                        WorkoutSession workoutSession;
                                        if (newWorkoutSession.workoutSessionId != null) {
                                            workoutSession = workoutSessionDao.getWorkoutSessionByWorkoutSessionId(newWorkoutSession.workoutSessionId);
                                        } else {
                                            workoutSession = workoutSessionDao.getWorkoutSessionByServerWorkoutSessionId(newWorkoutSession.serverWorkoutSessionId);
                                        }

                                        if (workoutSession != null) {
                                            newWorkoutSession.workoutSessionId = workoutSession.workoutSessionId;
                                            workoutSessionDao.update(newWorkoutSession);
                                        } else {
                                            workoutSessionDao.create(newWorkoutSession);
                                        }

                                        // put in hashmap
                                        workoutSessionHashMap.put(newWorkoutSession.serverWorkoutSessionId, newWorkoutSession);

                                        // add id and updated date to confirm sync array object
                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", newWorkoutSession._id);
                                        jsonObject.put("updated_date", newWorkoutSession.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */
                                    }

                                    workoutSessions.clear();

                                    workoutHashMap.clear();

                                    /*************************************************************************************/
                                    Log.wtf("*********", "Storing Set Sessions");
                                    for (SetSession newSetSession : setSessions) {

                                        WorkoutSession workoutSession;
                                        workoutSession = workoutSessionHashMap.get(newSetSession.serverWorkoutSessionId);
                                        if (workoutSession == null) {
                                            workoutSession = workoutSessionDao.getWorkoutSessionByServerWorkoutSessionId(newSetSession.serverWorkoutSessionId);
                                        }

                                        Set set;
                                        set = setHashMap.get(newSetSession.serverSetId);
                                        if (set == null) {
                                            set = setDao.getSetByServerSetId(newSetSession.serverSetId);
                                        }

                                        newSetSession.clientId = loggedClient.clientId;
                                        newSetSession.workoutSessionId = workoutSession.workoutSessionId;
                                        newSetSession.setId = set.setId;

                                        if (newSetSession.serverSetSessionId != null) {
                                            newSetSession.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            newSetSession.syncStatus = SyncStatus.NEW;
                                        }

                                        SetSession setSession;
                                        if (newSetSession.setSessionId != null) {
                                            setSession = setSessionDao.getSetSessionBySetSessionId(newSetSession.setSessionId);
                                        } else {
                                            setSession = setSessionDao.getSetSessionByServerSetSessionId(newSetSession.serverSetSessionId);
                                        }

                                        if (setSession != null) {
                                            newSetSession.setSessionId = setSession.setSessionId;
                                            setSessionDao.update(newSetSession);
                                        } else {
                                            setSessionDao.create(newSetSession);
                                        }

                                        // add id and updated date to confirm sync array object
                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", newSetSession._id);
                                        jsonObject.put("updated_date", newSetSession.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */

                                    }

                                    setSessions.clear();
                                    workoutSessionHashMap.clear();
                                    setHashMap.clear();
                                    /******************************* M E S S A GE S **************************/
                                    Log.wtf("*********", "Storing Messages");
                                    for (Message newMessage : messages) {
                                        /*
                                        Client client;
                                        Trainer trainer;

                                        if (newMessage.fromTrainer == 1) {
                                            client = clientDao.getClientByServerClientId(newMessage.serverToId);
                                            trainer = trainerDao.getTrainerByServerTrainerId(newMessage.serverFromId);
                                            newMessage.fromId = trainer.trainerId;
                                            newMessage.toId = client.clientId;
                                        } else {
                                            client = clientDao.getClientByServerClientId(newMessage.serverFromId);
                                            trainer = trainerDao.getTrainerByServerTrainerId(newMessage.serverToId);
                                            newMessage.fromId = client.clientId;
                                            newMessage.toId = trainer.trainerId;
                                        }
                                        */

                                        if (newMessage.fromTrainer == 1) {
                                            if (loggedClientsTrainer != null) {
                                                newMessage.fromId = loggedClientsTrainer.trainerId;
                                            }
                                            newMessage.toId = loggedClient.clientId;
                                        } else {
                                            newMessage.fromId = loggedClient.clientId;
                                            if (loggedClientsTrainer != null) {
                                                newMessage.toId = loggedClientsTrainer.trainerId;
                                            }
                                        }

                                        if (newMessage.serverMessageId != null) {
                                            newMessage.syncStatus = SyncStatus.SYNCED;
                                        } else {
                                            newMessage.syncStatus = SyncStatus.NEW;
                                        }

                                        Message message;

                                        if (newMessage.messageId != null) {
                                            message = messageDao.getMessageByMessageId(newMessage.messageId);
                                        } else {
                                            message = messageDao.getMessageByServerMessageId(newMessage.serverMessageId);
                                        }

                                        if (message != null) {
                                            newMessage.messageId = message.messageId;
                                            messageDao.update(newMessage);
                                        } else {
                                            messageDao.create(newMessage);
                                        }

                                        // add id and updated date to confirm sync array object
                                        /*
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("_id", newMessage._id);
                                        jsonObject.put("updated_date", newMessage.updatedDate);
                                        confirmationObjects.put(jsonObject);
                                        */
                                    }

                                    messages.clear();

                                    /*************************************************************************************/
                                    Log.wtf("*********", "Storing End");
                                    return null;
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {

                        //TODO  change this part and send broadcast
                        Fragment currentFragment = BaseActivity.this.getFragmentManager().findFragmentById(R.id.content_layout);
                        if (currentFragment != null) {
                            if (currentFragment instanceof Updateable) {
                                ((Updateable) currentFragment).updateView();
                            }
                        }


                        if (syncCompleteListener != null) {
                            syncCompleteListener.onComplete();
                        }

                        Intent intent = new Intent();
                        intent.setAction(BaseActivity.ACTION_SYNC_COMPLETE);
                        LocalBroadcastManager.getInstance(BaseActivity.this).sendBroadcast(intent);
                    }

                }.execute(responseGson);

            }
        }, this) {

            /**
             * override get headers to add custom headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                // put access token

                headers.putAll(commonHeaders);
                headers.putAll(super.getHeaders());

                headers.put("Access-Token", accessToken);

                if (doFullSync) {
                    headers.put("Full-Sync", "1");
//				}else{
//					headers.put("Full-Sync", "0");
                }

                long lastSyncTimestamp = SharedPreferencesHelper.getLong(BaseActivity.this, "Last-Sync-Timestamp");
                if (lastSyncTimestamp == 0) {
                    lastSyncTimestamp = -1;
                }

                headers.put("Last-Sync-Timestamp", lastSyncTimestamp + "");


                if (doForceLogoutSync) {
                    headers.put("Force-Logout", "1");
//				}else{
//					headers.put("Force-Logout", "0");
                }

                return headers;
            }

            @Override
            protected Response<ResponseGson> parseNetworkResponse(NetworkResponse response) {

                SharedPreferencesHelper.putLong(BaseActivity.this, "Last-Sync-Timestamp", Long.parseLong(response.headers.get("Last-Sync-Timestamp")));
                return super.parseNetworkResponse(response);
            }
        };
        // set tag auth
        syncRequest.setTag("sync");
        syncRequest.setShouldCache(false);

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        syncRequest.setRetryPolicy(policy);

        requestQueue.add(syncRequest);
    }

    class DownloadImageTask extends AsyncTask<List<Image>, Integer, Object> {

        private List<Image> images;

        private TextView progressView;
        private String imageBaseUrl;

        public DownloadImageTask() {
            imageBaseUrl = BaseActivity.this.getString(R.string.api_base_url) + "/picture/";
        }

        @Override
        protected Object doInBackground(List<Image>... params) {

            images = params[0];

            for (int i = 0; i < images.size(); i++) {
                downloadImage(images.get(i), i);

            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int index = values[0];

            if (progressView == null) {
                progressView = (TextView) BaseActivity.this.findViewById(R.id.image_download_progress);
                if (progressView != null) {
                    progressView.setText("");
                    progressView.setVisibility(View.VISIBLE);
                }
            }


            if (progressView != null) {
                progressView.setText("Downloading images " + index * 100 / images.size() + "%. Please wait.");
            }

        }

        @Override
        protected void onPostExecute(Object result) {
            if (progressView != null) {
                progressView.setText("");
                progressView.setVisibility(View.GONE);
            }
        }

        private void downloadImage(Image image, int index) {
            String url = imageBaseUrl + image._id;
            Log.d("DOW IMAGE", url);
            //---------------------------------------------------
            Bitmap bitmap = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(BaseActivity.this.getString(R.string.basic_auth_login), BaseActivity.this.getString(R.string.basic_auth_password).toCharArray());
                    }
                });
                InputStream inputStream = conn.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);


                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                options.inPurgeable = true;

                bitmap = BitmapFactory.decodeStream(bufferedInputStream, null, options);
                bufferedInputStream.close();
                inputStream.close();

                if (bitmap == null) {
                    publishProgress(index);
                    return;
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                ImageDao imageDao = getDatabaseHelper().getImageDao();

                image.setPhotoBase64(encoded);
                imageDao.update(image);

            } catch (IOException e) {
                e.printStackTrace();

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            publishProgress(index);
        }

    }

    /**
     * Callback method that an error has been occurred with the
     * provided error code and optional user-readable message.
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        dismissLoadingDialog();
        if (error instanceof AuthFailureError || error instanceof NoConnectionError && error.getMessage().toLowerCase().contains("No authentication challenges found".toLowerCase())) {
            // LogOut from application
            try {
                logoutFromApp();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (error instanceof NoConnectionError) {
            showNetworkErrorAlert(getString(R.string.error_no_internet_dialog_title), getString(R.string.error_no_internet_dialog_descroption));

        } else {

            if (error.networkResponse != null) {
                byte[] data = error.networkResponse.data;
                String responseString = new String(data);

                int statusCode = error.networkResponse.statusCode;
                try {
                    JSONObject messageObject = new JSONObject(responseString);
                    String message = messageObject.getString("message");
                    if (statusCode == 409) {
                        if (message.equals("Invalid username or password")) {

                            showLoginFailedErrorDialog();

                        } else if (message.equals("version code error")) {
                            if (this instanceof AuthActivity) {
                                showVersionCodeErrorDialog();
                            } else {
                                showLoadingDialog();
                                doForceLogoutSync(new SyncCompleteListener() {
                                    @Override
                                    public void onComplete() {
                                        try {
                                            logoutFromApp(true);
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        dismissLoadingDialog();
                                    }
                                });
                            }
                        }

                    } else if (statusCode == 410) {
                        if (!doLogoutSyncNow) {
                            showNoTrainerView();
                        }
                    } else {
                        showNetworkErrorAlert(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                showNetworkErrorAlert(getString(R.string.something_goes_wring));
            }
        }
    }

    private Dialog versionCodeErrorDialog = null;

    public void showVersionCodeErrorDialog() {
        if (versionCodeErrorDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_url)));
                    startActivity(browserIntent);
                }
            });
            builder.setTitle(getString(R.string.version_code_error_dialog_title));
            builder.setMessage(getString(R.string.version_code_error_dialog_message));
            versionCodeErrorDialog = builder.create();
        }
        versionCodeErrorDialog.show();
    }

    private Dialog loginFailedDialog = null;

    public void showLoginFailedErrorDialog() {
        if (loginFailedDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.login_failed_dialog_title));
            builder.setMessage(getString(R.string.login_failed_wrong_user));
            builder.setPositiveButton("OK", null);
            loginFailedDialog = builder.create();
        }
        loginFailedDialog.show();
    }

    private AlertDialog networkErrorAlertDialog = null;

    public void showNetworkErrorAlert(String title, String message) {
        if (networkErrorAlertDialog == null) {
            networkErrorAlertDialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    }).create();
        }

        // do not show no internet connection alert dialog if user already logged in
        if (this instanceof AuthActivity) {
            try {
                networkErrorAlertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void showNetworkErrorAlert(String message) {
        if (message.equals(getString(R.string.something_goes_wring))) {
            return;
        }
        showNetworkErrorAlert("Error", message);
    }


    public void showLoadingDialog() {
        loadingDialog.show();
    }

    public void dismissLoadingDialog() {
        loadingDialog.dismiss();
    }

    public void setTitle(String title) {
        if (title != null) {
            if (title.length() > 20) {
                title = title.substring(0, 20) + "...";
            }
        }
        findViewById(R.id.fragment_title_tv_home).setVisibility(View.GONE);
        findViewById(R.id.fragment_title_tv).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.fragment_title_tv)).setText(title);
    }

    public void setTitleHome() {
        findViewById(R.id.fragment_title_tv).setVisibility(View.GONE);
        findViewById(R.id.fragment_title_tv_home).setVisibility(View.VISIBLE);
    }


    public void logoutFromApp(int noTrainerCase) throws SQLException {
        logoutFromApp(false);
    }

    /**
     * @throws java.sql.SQLException
     */
    public void logoutFromApp() throws SQLException {
        logoutFromApp(false);
    }

    /**
     * logout from application
     * <p/>
     * * remove all db data
     * * remove scheduled sync
     * * remove data from shared prefs
     * * finish activity
     *
     * @throws java.sql.SQLException
     */
    public void logoutFromApp(boolean showVersionCodeDialog) throws SQLException {
        SharedPreferencesHelper.clearAfterLogout(BaseActivity.this);

        getDatabaseHelper().getTrainerDao().deleteBuilder().delete();
        getDatabaseHelper().getClientDao().deleteBuilder().delete();
        getDatabaseHelper().getClientPackageDao().deleteBuilder().delete();
        getDatabaseHelper().getPackageDao().deleteBuilder().delete();
        getDatabaseHelper().getDietDao().deleteBuilder().delete();
        getDatabaseHelper().getDietCategoryDao().deleteBuilder().delete();
        getDatabaseHelper().getExerciseDao().deleteBuilder().delete();
        getDatabaseHelper().getExerciseTypeDao().deleteBuilder().delete();
        getDatabaseHelper().getImageDao().deleteBuilder().delete();
        getDatabaseHelper().getMessageDao().deleteBuilder().delete();
        getDatabaseHelper().getMuscleGroupDao().deleteBuilder().delete();
        getDatabaseHelper().getPackageCategoryDao().deleteBuilder().delete();
        getDatabaseHelper().getPackageLevelDao().deleteBuilder().delete();
        getDatabaseHelper().getPackagePriceDao().deleteBuilder().delete();
        getDatabaseHelper().getPackageWeekDao().deleteBuilder().delete();
        getDatabaseHelper().getSetDao().deleteBuilder().delete();
        getDatabaseHelper().getSetSessionDao().deleteBuilder().delete();
        getDatabaseHelper().getWorkoutDao().deleteBuilder().delete();
        getDatabaseHelper().getWorkoutCategoryDao().deleteBuilder().delete();
        getDatabaseHelper().getWorkoutSessionDao().deleteBuilder().delete();


        finish();
        dismissLoadingDialog();
        Intent intent = new Intent(BaseActivity.this, AuthActivity.class);

        if (showVersionCodeDialog) {
            intent.putExtra("SHOW_VERSION_CODE_ERROR", true);
        }

        startActivity(intent);

        if (syncRunnable != null && syncScheduleHandler != null) {
            syncScheduleHandler.removeCallbacks(syncRunnable);
        }

        syncRunnable = null;
        syncScheduleHandler = null;
    }

    private void showNoTrainerView() {

        final View noTrainerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_start_no_trainer, null, false);
        noTrainerView.findViewById(R.id.bt_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog();
                ((ViewGroup) findViewById(android.R.id.content)).removeView(noTrainerView);
                doLogoutSync(new SyncCompleteListener() {
                    @Override
                    public void onComplete() {
                        try {
                            logoutFromApp();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        ((ViewGroup) findViewById(android.R.id.content)).addView(noTrainerView);
    }

    public interface SyncCompleteListener {
        public void onComplete();
    }


}