package nu.pto.androidapp.base.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.db.model.SetSession;
import nu.pto.androidapp.base.model.ExerciseSession;
import nu.pto.androidapp.base.ui.PtoTextView;
import nu.pto.androidapp.base.util.DbImageLoader;

public class LogExpandableListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<ExerciseSession> mExerciseSessions;

    public LogExpandableListAdapter(Context mContext, ArrayList<ExerciseSession> exerciseSessions) {
        this.mContext = mContext;
        this.mExerciseSessions = exerciseSessions;
    }

    @Override
    public int getGroupCount() {
        return mExerciseSessions.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mExerciseSessions.get(groupPosition).getSetSessions().size();
    }

    @Override
    public ArrayList<SetSession> getGroup(int groupPosition) {
        Log.i("___SetSession", mExerciseSessions.get(groupPosition).getSetSessions().toString());
        return mExerciseSessions.get(groupPosition).getSetSessions();

    }

    @Override
    public SetSession getChild(int groupPosition, int childPosition) {
        return mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item_session_exercise, null);
        ImageView imageViewCollapseIndicator = (ImageView) convertView.findViewById(R.id.image_view_expandable_indicator);
        if (isExpanded) {
            imageViewCollapseIndicator.setImageResource(R.drawable.arrow_collapse);
        } else {
            imageViewCollapseIndicator.setImageResource(R.drawable.arrow);
        }
        ((PtoTextView) convertView.findViewById(R.id.text_view_exercise_name)).setText(mExerciseSessions.get(groupPosition).getExercise().exerciseName);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view_exercise_image);
        imageView.setTag(mExerciseSessions.get(groupPosition).getImageId());
        imageView.setTag(mExerciseSessions.get(groupPosition).getImageId());
        if (mExerciseSessions.get(groupPosition).getImageId().equals("")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.workout_default_back));
        } else {
            DbImageLoader.getInstance().load(mContext, mExerciseSessions.get(groupPosition).getImageId(), imageView);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item_session_set, null);
        PtoTextView textViewSetName = (PtoTextView) convertView.findViewById(R.id.pto_text_view_set_name);
        textViewSetName.setText("SET " + (childPosition + 1));
        textViewSetName.setVisibility(View.VISIBLE);
        if (mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).repetitions != 0) {
            ((PtoTextView) convertView.findViewById(R.id.repeat_set_value_workout_expandable_list_group)).setText(Integer.toString(mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).repetitions));
            ((PtoTextView) convertView.findViewById(R.id.repeat_set_unit_workout_expandable_list_group)).setText("times");
        } else {
            ((RelativeLayout) convertView.findViewById(R.id.repeat_relative_layout_workout_expandable_list_group)).setVisibility(View.GONE);
        }
        if (mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).weight != null && mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).weight != 0 && mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).weightUnit != null) {
            ((PtoTextView) convertView.findViewById(R.id.weight_set_value_workout_expandable_list_group)).setText(Double.toString(mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).weight));
            ((PtoTextView) convertView.findViewById(R.id.weight_set_unit_workout_expandable_list_group)).setText(mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).weightUnit);
        } else {
            ((RelativeLayout) convertView.findViewById(R.id.weight_relative_layout_workout_expandable_list_group)).setVisibility(View.GONE);
        }
        if (mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).rest != null && mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).rest != 0 && mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).restUnit != null) {
            ((PtoTextView) convertView.findViewById(R.id.rest_set_value_workout_expandable_list_group)).setText(Double.toString(mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).rest));
            ((PtoTextView) convertView.findViewById(R.id.rest_set_unit_workout_expandable_list_group)).setText(mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).restUnit);
        } else {
            ((RelativeLayout) convertView.findViewById(R.id.rest_relative_layout_workout_expandable_list_group)).setVisibility(View.GONE);
        }
        if (mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).distance != null && mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).distance != 0 && mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).distanceUnit != null) {
            ((PtoTextView) convertView.findViewById(R.id.distance_set_value_workout_expandable_list_group)).setText(Double.toString(mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).distance));
            ((PtoTextView) convertView.findViewById(R.id.distance_set_unit_workout_expandable_list_group)).setText(mExerciseSessions.get(groupPosition).getSetSessions().get(childPosition).distanceUnit);
        } else {
            ((RelativeLayout) convertView.findViewById(R.id.distance_relative_layout_workout_expandable_list_group)).setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


}
