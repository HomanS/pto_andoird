package nu.pto.androidapp.base.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.adapter.LogExpandableListAdapter;
import nu.pto.androidapp.base.db.dao.ExerciseDao;
import nu.pto.androidapp.base.db.dao.SetDao;
import nu.pto.androidapp.base.db.dao.SetSessionDao;
import nu.pto.androidapp.base.db.model.Exercise;
import nu.pto.androidapp.base.db.model.Set;
import nu.pto.androidapp.base.db.model.SetSession;
import nu.pto.androidapp.base.db.model.Workout;
import nu.pto.androidapp.base.model.ExerciseSession;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;

import java.sql.SQLException;
import java.util.ArrayList;


public class WorkoutExerciseFragment extends Fragment {
    private int workoutId;
    private int workoutSessionId;
    private int previousGroupPosition = -1;
    private String workoutName;
    private Context mContext;

    public WorkoutExerciseFragment( int workoutId, int workoutSessionId) {
        this.workoutId = workoutId;
        this.workoutSessionId = workoutSessionId;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workout_exercises, container, false);
        final ExpandableListView expandableListView = (ExpandableListView) rootView.findViewById(R.id.expandable_fragment_workout_exercises);
        expandableListView.setGroupIndicator(null);
        int clientId = SharedPreferencesHelper.getInt(mContext, SharedPreferencesHelper.KEY_CLIENT_ID);

        try {
            SetSessionDao setSessionDao = ((BaseActivity) getActivity()).getDatabaseHelper().getSetSessionDao();
            ExerciseDao exerciseDao = ((BaseActivity) getActivity()).getDatabaseHelper().getExerciseDao();
            SetDao setDao = ((BaseActivity) getActivity()).getDatabaseHelper().getSetDao();
            ArrayList<ExerciseSession> exerciseSessions = new ArrayList<ExerciseSession>();
            ArrayList<SetSession> setSessions = setSessionDao.getSetSessionByWorkoutSessionIdAndClientId(workoutSessionId, clientId);
            Workout workout = ((BaseActivity)getActivity()).getDatabaseHelper().getWorkoutDao().getWorkoutById(workoutId);
            workoutName = workout.workoutName;
            for (SetSession setSession : setSessions) {
                Set set = setDao.getSetBySetId(setSession.setId);
                if (set != null) {
                    Exercise exercise = exerciseDao.getExerciseByExerciseId(set.exerciseId);
                    boolean exerciseFound = false;
                    for (ExerciseSession exerciseSession : exerciseSessions) {
                        if (exerciseSession.getExercise().exerciseId == exercise.exerciseId) {
                            exerciseSession.addSetSession(setSession);
                            exerciseFound = true;
                            break;
                        }
                    }
                    if (!exerciseFound) {
                        ExerciseSession exerciseSession = new ExerciseSession();
                        exerciseSession.setExercise(exercise);
                        exerciseSession.addSetSession(setSession);
                        exerciseSessions.add(exerciseSession);
                    }
                }
            }
            expandableListView.setAdapter(new LogExpandableListAdapter(getActivity(), exerciseSessions));
            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {
                    if (previousGroupPosition == -1) {
                        previousGroupPosition = groupPosition;
                        expandableListView.expandGroup(groupPosition);
                    } else {
                        if (previousGroupPosition != groupPosition) {
                            expandableListView.collapseGroup(previousGroupPosition);
                            previousGroupPosition = groupPosition;
                            expandableListView.expandGroup(groupPosition);
                        }
                    }
                }
            });


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BaseActivity)mContext).setTitle(workoutName);
    }
}
