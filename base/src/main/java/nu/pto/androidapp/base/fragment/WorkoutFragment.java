package nu.pto.androidapp.base.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.CustomActionBarActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.adapter.WorkoutExpandableListAdapter;
import nu.pto.androidapp.base.adapter.WorkoutPagerAdapter;
import nu.pto.androidapp.base.db.dao.ExerciseDao;
import nu.pto.androidapp.base.db.dao.SetDao;
import nu.pto.androidapp.base.db.dao.SetSessionDao;
import nu.pto.androidapp.base.db.dao.WorkoutSessionDao;
import nu.pto.androidapp.base.db.model.Exercise;
import nu.pto.androidapp.base.db.model.Set;
import nu.pto.androidapp.base.db.model.SetSession;
import nu.pto.androidapp.base.db.model.Workout;
import nu.pto.androidapp.base.db.model.WorkoutSession;
import nu.pto.androidapp.base.ui.PtoTextView;
import nu.pto.androidapp.base.ui.WorkoutScrollViewItem;
import nu.pto.androidapp.base.util.Constants;
import nu.pto.androidapp.base.util.DbImageLoader;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;
import nu.pto.androidapp.base.util.SyncStatus;
import nu.pto.androidapp.base.util.Utils;


public class WorkoutFragment extends BaseFragment {

    private ViewPager viewPager;
    private Integer selectedExerciseId = -1;
    private WorkoutExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private Context mContext;
    private Workout workout;
    private WorkoutSession tempWorkoutSession;
    private ArrayList<SetSession> tempSetSessions;
    private int previousGroup = -1;
    private ArrayList<Exercise> exercises;
    private ArrayList<WorkoutScrollViewItem> workoutScrollViewItems;
    private long timerPauseTotal;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("workoutId", workout.workoutId);
        super.onSaveInstanceState(outState);
    }


    private void init(Integer workoutId) {
        this.mContext = getBaseActivity();
        try {
            workout = ((BaseActivity) mContext).getDatabaseHelper().getWorkoutDao().getWorkoutById(workoutId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.workoutScrollViewItems = new ArrayList<WorkoutScrollViewItem>();

        // TODO check end dates
        WorkoutSession workoutSession = new WorkoutSession();
        workoutSession.workoutId = workoutId;
        workoutSession.createdDate = Utils.getCurrentTimestamp();
        workoutSession.updatedDate = Utils.getCurrentTimestamp();
        workoutSession.deleted = 0;
        workoutSession.syncStatus = SyncStatus.NEW;

        this.tempWorkoutSession = workoutSession;
        this.tempSetSessions = new ArrayList<SetSession>();
        this.timerPauseTotal = 0;
        getBaseActivity().setTitle(workout.workoutName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        int workoutId = bundle.getInt("workout_id");
        init(workoutId);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_workout, container, false);

        final ImageView ivBack = (ImageView) getBaseActivity().findViewById(R.id.btn_cancel_custom_action_bar_activity);
        ivBack.setImageResource(R.drawable.arrow_white_fliped);
        ivBack.setPadding(30, 10, 20, 10);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWorkout();
            }
        });

        final ImageView ivPause = (ImageView) getBaseActivity().findViewById(R.id.btn_save_custom_action_bar_activity);
        ivPause.setImageResource(R.drawable.icon_pause_white);
        ivPause.setPadding(20, 10, 30, 10);
        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivPause.setEnabled(false);
                final RelativeLayout timerViewRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.workout_session_timer_view);
                timerViewRelativeLayout.setVisibility(View.VISIBLE);
                final long timerPausedAt = Utils.getCurrentTimestamp();
                PtoTextView timerTextView = (PtoTextView) timerViewRelativeLayout.findViewById(R.id.workout_session_timer_view_time_passed);
                String timeElapsed = "00 : 00";
                if (tempWorkoutSession.sessionStartDate != 0) {
                    timeElapsed = Utils.getTimeFromSeconds(Utils.getCurrentTimestamp() - tempWorkoutSession.sessionStartDate - timerPauseTotal);
                }
                timerTextView.setText(timeElapsed);

                ImageView resumeImageView = (ImageView) timerViewRelativeLayout.findViewById(R.id.workout_session_timer_view_resume);
                resumeImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ivPause.setEnabled(true);
                        timerViewRelativeLayout.setVisibility(View.GONE);
                        timerPauseTotal = Utils.getCurrentTimestamp() - timerPausedAt;
                    }
                });
            }
        });

        rootView.findViewById(R.id.ll_instructions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = rootView.findViewById(R.id.view_pager_image_name);
                TextView readHere = (TextView) rootView.findViewById(R.id.tv_read_here);
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                    readHere.setText(R.string.read_here);
                } else {
                    view.setVisibility(View.VISIBLE);
                    readHere.setText(R.string.close);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {

            if (workout == null) {
                init(savedInstanceState.getInt("workoutId"));
            }

            ExerciseDao exerciseDao = getBaseActivity().getDatabaseHelper().getExerciseDao();
            this.exercises = exerciseDao.getExercisesByWorkoutId(workout.workoutId);

            List<Exercise> filteredExercises = new ArrayList<Exercise>();
            for (Exercise exercise : exercises) {
                SetDao setDao = ((BaseActivity) mContext).getDatabaseHelper().getSetDao();
                if (setDao.getSetsByExerciseId(exercise.exerciseId).size() > 0) {
                    filteredExercises.add(exercise);
                }
            }
            exercises = (ArrayList<Exercise>) filteredExercises;


            // set first as selected by default
            if (selectedExerciseId == -1 && exercises.size() > 0 && exercises.get(0) != null) {
                selectedExerciseId = exercises.get(0).exerciseId;
                redrawMainContent(getView(), exercises.get(0));
//                setViewPagerSelectedItem();
            }


            for (Exercise exercise : exercises) {

                String exerciseImagesEncoded = exercise.imagesIds;
                String[] exerciseImagesDecoded = exerciseImagesEncoded.split("#|#");

                if (exerciseImagesDecoded.length > 0) {
                    exercise.images = exerciseImagesDecoded[0];
                }
            }

            addWorkoutScrollViewItems(getView(), exercises);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void addWorkoutScrollViewItems(final View rootView, List<Exercise> exercises) {
        HorizontalScrollView horizontalScrollView = (HorizontalScrollView) rootView.findViewById(R.id.scroll_view_fragment_workout);
        LinearLayout linearLayout = new LinearLayout(rootView.getContext());

        for (final Exercise exercise : exercises) {
            final WorkoutScrollViewItem workoutScrollViewItem = new WorkoutScrollViewItem(rootView.getContext());
            workoutScrollViewItem.setId(exercise.exerciseId);
            ImageView ivExerciseItem = (ImageView) workoutScrollViewItem.findViewById(R.id.iv_workout_scroll_view_item);
            if (exercise.images != null && !exercise.images.equals("")) {
                ivExerciseItem.setTag(exercise.images);
                DbImageLoader.getInstance().load(getBaseActivity(), exercise.images, ivExerciseItem);
            }
            workoutScrollViewItem.showHideHUDView(false);
            linearLayout.addView(workoutScrollViewItem);
            if (selectedExerciseId.equals(exercise.exerciseId)) {
                workoutScrollViewItem.setSelectedState(true);
            }
            ivExerciseItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // set old selected none selected (size), change current to selected
                    if (selectedExerciseId != exercise.exerciseId) {
                        ((WorkoutScrollViewItem) rootView.findViewById(selectedExerciseId)).setSelectedState(false);
                        workoutScrollViewItem.setSelectedState(true);
                        selectedExerciseId = exercise.exerciseId;
                        // redraw main content depending serverClientId
                        redrawMainContent(rootView, exercise);
                    }
                }
            });
            if (workoutScrollViewItems == null) {
                workoutScrollViewItems = new ArrayList<WorkoutScrollViewItem>();
            }
            workoutScrollViewItem.setExerciseId(exercise.exerciseId);
            workoutScrollViewItems.add(workoutScrollViewItem);
        }
        horizontalScrollView.addView(linearLayout);
    }

    /**
     * gets current selected exercise and puts it to the main content
     *
     * @param rootView
     * @param exercise
     */


    private void redrawMainContent(final View rootView, final Exercise exercise) {

        // init sets expandable list view
        expListView = (ExpandableListView) rootView.findViewById(R.id.lv_expandable_fragment_workout);
        if (listAdapter != null) {
            listAdapter.killTimer();
        }
        listAdapter = new WorkoutExpandableListAdapter(getBaseActivity(), selectedExerciseId, tempWorkoutSession, tempSetSessions, new WorkoutExpandableListAdapter.OnSetChangeInterface() {
            @Override
            public void onTimerStart(int groupPosition, Set set, int exerciseId) {
                // update setSession date data
                if (tempWorkoutSession.sessionStartDate == 0) {
                    tempWorkoutSession.sessionStartDate = Utils.getCurrentTimestamp();
                }
                boolean tempSetSessionFound = false;
                for (SetSession setSession : tempSetSessions) {
                    if (setSession.setId.equals(set.setId)) {
                        tempSetSessionFound = true;
                        setSession.createdDate = Utils.getCurrentTimestamp();
                        setSession.updatedDate = 0;
                        break;
                    }
                }
                if (!tempSetSessionFound) {
                    SetSession setSession = listAdapter.createSetSessionForSet(set.setId);
                    setSession.createdDate = Utils.getCurrentTimestamp();
                    setSession.updatedDate = 0;
                }
                previousGroup = groupPosition;
            }

            @Override
            public void onTimerStop(int groupPosition, Set set, int exerciseId, long elapsedTime) {
                // update setSession date data
                for (SetSession setSession : tempSetSessions) {
                    if (setSession.setId.equals(set.setId)) {
                        setSession.createdDate = Utils.getCurrentTimestamp();
                        setSession.updatedDate = Utils.getCurrentTimestamp();
                        setSession.time = (int) elapsedTime;
                        break;
                    }
                }
                expListView.collapseGroup(groupPosition);
                previousGroup = groupPosition + 1;
                if (previousGroup < listAdapter.getGroupCount()) {
                    expListView.expandGroup(previousGroup);
                } else {
                    previousGroup = -1;
                }
                updateWorkoutScrollViewItem(exerciseId);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onClear(int groupPosition, Set set, int exerciseId) {
                // update setSession date data
                for (SetSession setSession : tempSetSessions) {
                    if (setSession.setId.equals(set.setId)) {
                        setSession.createdDate = 0;
                        setSession.updatedDate = 0;
                        setSession.time = set.time;
                        break;
                    }
                }

                if (previousGroup != -1) {
                    expListView.collapseGroup(previousGroup);
                }
                expListView.expandGroup(groupPosition);
                previousGroup = groupPosition;

                updateWorkoutScrollViewItem(exerciseId);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSetComplete(int groupPosition, Set set, int exerciseId) {
                if (tempWorkoutSession.sessionStartDate == 0) {
                    tempWorkoutSession.sessionStartDate = Utils.getCurrentTimestamp();
                }
                for (SetSession setSession : tempSetSessions) {
                    if (setSession.setId.equals(set.setId)) {
                        setSession.createdDate = Utils.getCurrentTimestamp();
                        setSession.updatedDate = Utils.getCurrentTimestamp();
                        break;
                    }
                }
                if (previousGroup != -1) {
                    expListView.collapseGroup(previousGroup);
                }
                expListView.collapseGroup(groupPosition);
                previousGroup = groupPosition + 1;
                if (previousGroup < listAdapter.getGroupCount()) {
                    expListView.expandGroup(previousGroup);
                } else {
                    previousGroup = -1;
                }
                updateWorkoutScrollViewItem(exerciseId);
                listAdapter.notifyDataSetChanged();
            }
        });
        expListView.setAdapter(listAdapter);


        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (previousGroup == -1) {
                    previousGroup = groupPosition;
                    expListView.expandGroup(groupPosition);
                } else {
                    if (previousGroup != groupPosition) {
                        expListView.collapseGroup(previousGroup);
                        expListView.expandGroup(groupPosition);
                        previousGroup = groupPosition;
                    } else {
                        expListView.collapseGroup(previousGroup);
                        previousGroup = -1;
                    }
                }
                return true;
            }
        });

        // set exercise name
        ((PtoTextView) rootView.findViewById(R.id.ptv_exercise_name_fragment_workout)).setText(exercise.exerciseName);

        // redraw workout pager
        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager_fragment_workout);
        viewPager.setVisibility(View.VISIBLE);
        viewPager.setAdapter(new WorkoutPagerAdapter(getBaseActivity(), exercise));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                setViewPagerSelectedItem(i, viewPager.getAdapter().getCount(), ((WorkoutPagerAdapter) viewPager.getAdapter()).getImageDescriptions(i), rootView);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        setViewPagerSelectedItem(0, viewPager.getAdapter().getCount(), ((WorkoutPagerAdapter) viewPager.getAdapter()).getImageDescriptions(0), rootView);

    }


    private void updateWorkoutScrollViewItem(int exerciseId) {
        for (WorkoutScrollViewItem workoutScrollViewItem : workoutScrollViewItems) {
            if (workoutScrollViewItem.getExerciseId() == exerciseId) {
                float completionLevel = listAdapter.getCompletionLevel(exerciseId);
                if (completionLevel == 0) {
                    workoutScrollViewItem.showHideHUDView(false);
                } else {
                    workoutScrollViewItem.showHideHUDView(true);
                    workoutScrollViewItem.updateHUDView(completionLevel);
                }
                break;
            }
        }

    }

    private void setViewPagerSelectedItem(int selected, int total, String text, View rootView) {
        if (text == null) {
            text = "";
        }
        if (text.length() > 50) {
//            text = text.substring(0, 50) + "...";
        }
        LinearLayout viewPagerLinearLayout = (LinearLayout) rootView.findViewById(R.id.view_pager_indicator);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        viewPagerLinearLayout.removeAllViews();
        for (int j = 0; j < total; j++) {
            if (j == selected) {
                ImageView activeImageView = new ImageView(getBaseActivity());
                activeImageView.setBackgroundResource(R.drawable.pagination_selected);
                activeImageView.setLayoutParams(params);
                viewPagerLinearLayout.addView(activeImageView);
                PtoTextView viewPagerImageName = (PtoTextView) rootView.findViewById(R.id.view_pager_image_name);
                viewPagerImageName.setText(text);
                if (text.trim().length() > 0) {
                    rootView.findViewById(R.id.ll_instructions).setVisibility(View.VISIBLE);
                } else {
                    rootView.findViewById(R.id.ll_instructions).setVisibility(View.GONE);
                }
            } else {
                ImageView normalImageView = new ImageView(getBaseActivity());
                normalImageView.setBackgroundResource(R.drawable.pagination_normal);
                normalImageView.setLayoutParams(params);
                viewPagerLinearLayout.addView(normalImageView);
            }
        }
    }


    public void finishWorkout() {
        if (hasWorkoutAnyFinishedSet()) {
            final long timerPausedAt = Utils.getCurrentTimestamp();
            new AlertDialog.Builder(getActivity())
                    .setMessage(mContext.getString(R.string.string_leave_workout))
                    .setPositiveButton(mContext.getString(R.string.string_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                /*
                                 *TODO save workout session data and finish activity
                                */

                            tempWorkoutSession.sessionEndDate = timerPausedAt;
                            int clientId = SharedPreferencesHelper.getInt(mContext, SharedPreferencesHelper.KEY_CLIENT_ID);
                            int serverClientId = SharedPreferencesHelper.getInt(mContext, SharedPreferencesHelper.KEY_SERVER_CLIENT_ID);
                            tempWorkoutSession.clientId = clientId;
                            tempWorkoutSession.serverClientId = serverClientId;
                            tempWorkoutSession.serverWorkoutId = workout.serverWorkoutId;


                            try {
                                WorkoutSessionDao workoutSessionDao = getDatabaseHelper().getWorkoutSessionDao();
                                workoutSessionDao.create(tempWorkoutSession);
                                SetSessionDao setSessionDao = getDatabaseHelper().getSetSessionDao();
                                if (tempSetSessions.size() != 0) {
                                    int doneSetSessionCount = 0;
                                    for (SetSession tempSetSession : tempSetSessions) {
                                        if (!(tempSetSession.createdDate == 0 && tempSetSession.updatedDate == 0)) {
                                            tempSetSession.workoutSessionId = tempWorkoutSession.workoutSessionId;
                                            if (tempWorkoutSession.serverWorkoutSessionId != null && tempWorkoutSession.serverWorkoutSessionId != 0) {
                                                tempSetSession.serverWorkoutSessionId = tempWorkoutSession.serverWorkoutSessionId;
                                            }
                                            tempSetSession.serverClientId = serverClientId;
                                            tempSetSession.clientId = clientId;
                                            setSessionDao.create(tempSetSession);
                                            doneSetSessionCount++;
                                        }
                                    }
                                    if (doneSetSessionCount == 0) {
                                        workoutSessionDao.delete(tempWorkoutSession);
                                        ((CustomActionBarActivity) mContext).sendIntentResult(0);
                                    } else {
                                        getBaseActivity().sendGoogleAnalyticsData(Constants.ANALYTICS_EVENT_WORKOUT_SAVE);
                                        ((CustomActionBarActivity) mContext).sendIntentResult(1);
                                    }
                                }
                                ((CustomActionBarActivity) mContext).sendIntentResult(0);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(mContext.getString(R.string.string_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO continue workout session
                            timerPauseTotal = timerPausedAt - Utils.getCurrentTimestamp();
                        }
                    })
                    .show();
        } else {
            ((CustomActionBarActivity) mContext).sendIntentResult(0);
        }
    }

    private boolean hasWorkoutAnyFinishedSet() {
        if (tempSetSessions != null && tempSetSessions.size() > 0) {
            for (SetSession setSession : tempSetSessions) {
                if (setSession.createdDate != 0 && setSession.updatedDate != 0) {
                    return true;
                }
            }
        }

        return false;
    }

}