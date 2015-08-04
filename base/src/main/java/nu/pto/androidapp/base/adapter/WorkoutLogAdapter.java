package nu.pto.androidapp.base.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.db.model.Workout;
import nu.pto.androidapp.base.db.model.WorkoutSession;
import nu.pto.androidapp.base.ui.PtoTextView;
import nu.pto.androidapp.base.util.DbImageLoader;
import nu.pto.androidapp.base.util.Utils;

import java.sql.SQLException;
import java.util.ArrayList;

public class WorkoutLogAdapter extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<WorkoutSession> workoutSessions;
    private ArrayList<Workout> workouts;

    public WorkoutLogAdapter(Activity mActivity, ArrayList<Workout> workouts, ArrayList<WorkoutSession> workoutSessions) {
        this.mActivity = mActivity;
        this.workoutSessions = workoutSessions;
        this.workouts = workouts;
    }

    @Override
    public int getCount() {
        return workoutSessions.size();
    }

    @Override
    public WorkoutSession getItem(int i) {
        return workoutSessions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        view = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_workout, null);
        final ImageView ivWorkoutImage = (ImageView) view.findViewById(R.id.iv_workout_image_list_item_workout);
        PtoTextView ptvWorkoutName = (PtoTextView) view.findViewById(R.id.ptv_workout_name_lit_item_workout);
        PtoTextView ptvWorkoutEndDate = (PtoTextView) view.findViewById(R.id.ptv_workout_end_date_lit_item_workout);

        view.findViewById(R.id.ib_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = "";
                String name = "";
                Integer workoutId = workoutSessions.get(position).workoutId;
                for (Workout workout : workouts) {
                    if (workout.workoutId.equals(workoutId)) {
                        description = workout.workoutDescription;
                        name = workout.workoutName;
                        break;
                    }
                }

                if(description != null && description.length() > 0) {
                    mActivity.findViewById(R.id.fl_workout_desc).setVisibility(View.VISIBLE);
                    ((TextView) mActivity.findViewById(R.id.tv_workout_desc)).setText(description);
                    ((TextView) mActivity.findViewById(R.id.tv_workout_desc)).setMovementMethod(new ScrollingMovementMethod());
                    ((TextView) mActivity.findViewById(R.id.tv_workout_title)).setText(name);
                }
            }
        });
        view.findViewById(R.id.ib_info).setClickable(false);

        String workoutName = "";
        Integer workoutId = workoutSessions.get(position).workoutId;
        for (Workout workout : workouts) {
            if (workout.workoutId.equals(workoutId)) {
                workoutName = workout.workoutName;
                break;
            }

            ptvWorkoutName.setText(workoutName);
            ptvWorkoutEndDate.setText(Utils.secondFormatToDate(workoutSessions.get(position).updatedDate));

        }
        try {
            final String firstExerciseImageId = ((BaseActivity) mActivity).getDatabaseHelper().getExerciseDao().getFirstExerciseImageIdByWorkoutId(workoutSessions.get(position).workoutId);
            ivWorkoutImage.setTag(firstExerciseImageId);
            DbImageLoader.getInstance().load(mActivity, firstExerciseImageId, ivWorkoutImage);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ptvWorkoutName.setText(workoutName);
        ptvWorkoutEndDate.setText(Utils.secondFormatToDate(workoutSessions.get(position).updatedDate));


        return view;
    }
}
