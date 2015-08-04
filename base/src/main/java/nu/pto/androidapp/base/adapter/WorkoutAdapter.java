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
import nu.pto.androidapp.base.ui.PtoTextView;
import nu.pto.androidapp.base.util.DbImageLoader;
import nu.pto.androidapp.base.util.Utils;

import java.sql.SQLException;
import java.util.List;

public class WorkoutAdapter extends BaseAdapter {

    private Activity mActivity;
    private List<Workout> workouts;

    public WorkoutAdapter(Activity mActivity, List<Workout> workouts) {
        this.mActivity = mActivity;
        this.workouts = workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return workouts.size();
    }

    @Override
    public Workout getItem(int i) {
        return workouts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_workout, null);
        }
        ImageView ivWorkoutImage = (ImageView) view.findViewById(R.id.iv_workout_image_list_item_workout);
        PtoTextView ptvWorkoutName = (PtoTextView) view.findViewById(R.id.ptv_workout_name_lit_item_workout);
        PtoTextView ptvWorkoutEndDate = (PtoTextView) view.findViewById(R.id.ptv_workout_end_date_lit_item_workout);
        ptvWorkoutName.setText(workouts.get(position).workoutName);
        ptvWorkoutEndDate.setText(Utils.secondFormatToDate(workouts.get(position).createdDate));


        view.findViewById(R.id.ib_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mActivity.findViewById(R.id.fl_workout_desc).setVisibility(View.VISIBLE);
                if (getItem(position).workoutDescription != null && getItem(position).workoutDescription.length() > 0) {
                    ((TextView) mActivity.findViewById(R.id.tv_workout_desc)).setText(getItem(position).workoutDescription);
                } else {
                    ((TextView) mActivity.findViewById(R.id.tv_workout_desc)).setText("");
                }
                ((TextView) mActivity.findViewById(R.id.tv_workout_desc)).setMovementMethod(new ScrollingMovementMethod());
                ((TextView) mActivity.findViewById(R.id.tv_workout_title)).setText(getItem(position).workoutName);

            }
        });
        view.findViewById(R.id.ib_info).setClickable(false);

        try {
            String firstExerciseImageId = ((BaseActivity) mActivity).getDatabaseHelper().getExerciseDao().getFirstExerciseImageIdByWorkoutId(workouts.get(position).workoutId);
            ivWorkoutImage.setTag(firstExerciseImageId);
            if (firstExerciseImageId.trim().length() > 0) {
                DbImageLoader.getInstance().load(mActivity, firstExerciseImageId, ivWorkoutImage);
            } else {
                ivWorkoutImage.setImageResource(R.drawable.workout_default_back);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return view;
    }


}
