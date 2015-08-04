package nu.pto.androidapp.base.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.db.dao.SetDao;
import nu.pto.androidapp.base.db.model.Set;
import nu.pto.androidapp.base.db.model.SetSession;
import nu.pto.androidapp.base.db.model.WorkoutSession;
import nu.pto.androidapp.base.helper.Timer;
import nu.pto.androidapp.base.ui.PtoTextView;
import nu.pto.androidapp.base.ui.TimerView;
import nu.pto.androidapp.base.util.SyncStatus;
import nu.pto.androidapp.base.util.Utils;

public class WorkoutExpandableListAdapter extends BaseExpandableListAdapter {
    public static interface OnSetChangeInterface {
        public void onTimerStart(int groupPosition, Set setId, int exerciseId);

        public void onTimerStop(int groupPosition, Set setId, int exerciseId, long elapsedTime);

        public void onClear(int groupPosition, Set Set, int exerciseId);

        public void onSetComplete(int groupPosition, Set setId, int exerciseId);
    }


    private Context mContext;
    private ArrayList<Set> sets;
    private String[] repeatTypes = new String[]{"times"};
    private String[] weightTypes = new String[]{"kg", "g"};
    private String[] timerTypes = new String[]{"s", "m", "h"};
    private String[] restTypes = new String[]{"s", "m", "h"};
    private String[] distanceTypes = new String[]{"m", "km"};
    private WorkoutSession tempWorkoutSession;
    private ArrayList<SetSession> tempSetSessions;
    private boolean timerStarted;
    private OnSetChangeInterface onSetChangeInterface;
    private Timer activeTimer;

    public WorkoutExpandableListAdapter(Context mContext, int selectedExerciseId, WorkoutSession tempWorkoutSession, ArrayList<SetSession> tempSetSessions, OnSetChangeInterface onSetChangeInterface) {
        this.mContext = mContext;
        try {
            SetDao setDao = ((BaseActivity) mContext).getDatabaseHelper().getSetDao();
            this.sets = setDao.getSetsByExerciseId(selectedExerciseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.tempWorkoutSession = tempWorkoutSession;
        this.tempSetSessions = tempSetSessions;
        this.onSetChangeInterface = onSetChangeInterface;
    }

    @Override
    public Set getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            this.timerStarted = false;
        }
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.workout_expandablelist_item, null);
        final Set finalSet = sets.get(groupPosition);

        if (activeTimer != null && timerStarted) {
            if (!activeTimer.getIdentifier().equals(finalSet.setId)) {
                activeTimer.killTimer();
                timerStarted = false;
                activeTimer = null;
            }
        }

        ///// set all values and units

        final TimerView timerView = (TimerView) convertView.findViewById(R.id.timer_view_workout_expandable_list_group);

        // check if setSession already exists and display setSession data

        for (SetSession setSession : tempSetSessions) {
            if(activeTimer != null){
                if(setSession.setId.equals(activeTimer.getIdentifier())){
//                    setSession.createdDate = 0;
                    setSession.updatedDate = 0;
                }
            }
            if (setSession.setId.equals(finalSet.setId)) {

                finalSet.repetitions = setSession.repetitions;
                finalSet.weight = setSession.weight;
                finalSet.weightUnit = setSession.weightUnit;
//                finalSet.time = setSession.time;
                finalSet.timeUnit = setSession.timeUnit;
                finalSet.distance = setSession.distance;
                finalSet.distanceUnit = setSession.distanceUnit;
                finalSet.rest = setSession.rest;
                finalSet.restUnit = setSession.restUnit;
                /*
                *************************************************************************************************************************
                 */
                if (setSession.createdDate == 0 && setSession.updatedDate == 0) {
                    timerView.setVisibility(View.VISIBLE);
                } else {
                    if (!timerStarted) {
                        timerView.setVisibility(View.GONE);
                    }else{
                        timerView.setVisibility(View.VISIBLE);
                    }
                }
                /*
                *************************************************************************************************************************
                 */
                break;
            }
        }
        /*
        *************************************************************************************************************************
         */
        timerView.setTimerChangeInterface(new TimerView.TimerChangeInterface() {
            @Override
            public void onTimerComplete() {
                onSetChangeInterface.onSetComplete(groupPosition, finalSet, finalSet.exerciseId);
            }
        });
        if (finalSet.time != null && finalSet.time != 0) {
            timerView.setRequireSeconds(finalSet.time);
            timerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (timerStarted) {
                        long elapsedTime = activeTimer.getElapsedSeconds();
                        activeTimer.killTimer();
                        timerStarted = false;
                        activeTimer = null;
                        timerView.pause();
                        onSetChangeInterface.onTimerStop(groupPosition, finalSet, finalSet.exerciseId,elapsedTime);
                    } else {
                        activeTimer = timerView.getTimer();
                        activeTimer.setIdentifier(finalSet.setId);
                        timerView.start();
                        timerStarted = true;
                        onSetChangeInterface.onTimerStart(groupPosition, finalSet, finalSet.exerciseId);
                    }
                }
            });
        } else {
            if (!timerStarted) {
                timerView.setVisibility(View.GONE);
            }else{
                timerView.setVisibility(View.VISIBLE);
            }
        }
        /*
        *************************************************************************************************************************
         */

        if (finalSet.repetitions != 0) {
//            show repetitions
            PtoTextView repetitionsTextView = (PtoTextView) convertView.findViewById(R.id.repeat_set_value_workout_expandable_list_group);
            PtoTextView repetitionsUnitTextView = (PtoTextView) convertView.findViewById(R.id.repeat_set_unit_workout_expandable_list_group);
            repetitionsTextView.setText(Integer.toString(finalSet.repetitions));
            repetitionsUnitTextView.setText("times");
            ((RelativeLayout) convertView.findViewById(R.id.repeat_relative_layout_workout_expandable_list_group)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNumberPicker(0, finalSet.repetitions, "times", groupPosition, v);
                }
            });
        } else {
            ((RelativeLayout) convertView.findViewById(R.id.repeat_relative_layout_workout_expandable_list_group)).setVisibility(View.GONE);
        }
        if (finalSet.weight != null && finalSet.weightUnit != null) {
//            show weight
            PtoTextView weightTextView = (PtoTextView) convertView.findViewById(R.id.weight_set_value_workout_expandable_list_group);
            PtoTextView weightUnitTextView = (PtoTextView) convertView.findViewById(R.id.weight_set_unit_workout_expandable_list_group);
            weightTextView.setText(Double.toString(finalSet.weight));
            weightUnitTextView.setText(finalSet.weightUnit);
            ((RelativeLayout) convertView.findViewById(R.id.weight_relative_layout_workout_expandable_list_group)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNumberPicker(1, finalSet.weight.intValue(), finalSet.weightUnit, groupPosition, v);
                }
            });
        } else {
            ((RelativeLayout) convertView.findViewById(R.id.weight_relative_layout_workout_expandable_list_group)).setVisibility(View.GONE);
        }
        if (finalSet.rest != null && finalSet.restUnit != null) {
//            show rest
            PtoTextView restTextView = (PtoTextView) convertView.findViewById(R.id.rest_set_value_workout_expandable_list_group);
            PtoTextView restUnitTextView = (PtoTextView) convertView.findViewById(R.id.rest_set_unit_workout_expandable_list_group);
            restTextView.setText(Double.toString(finalSet.rest));
            restUnitTextView.setText(finalSet.restUnit);
            ((RelativeLayout) convertView.findViewById(R.id.rest_relative_layout_workout_expandable_list_group)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNumberPicker(3, finalSet.rest.intValue(), finalSet.restUnit, groupPosition, v);
                }
            });
        } else {
            ((RelativeLayout) convertView.findViewById(R.id.rest_relative_layout_workout_expandable_list_group)).setVisibility(View.GONE);
        }
        if (finalSet.distance != null && finalSet.distanceUnit != null) {
//            show weight
            PtoTextView distanceTextView = (PtoTextView) convertView.findViewById(R.id.distance_set_value_workout_expandable_list_group);
            PtoTextView distanceUnitTextView = (PtoTextView) convertView.findViewById(R.id.distance_set_unit_workout_expandable_list_group);
            distanceTextView.setText(Double.toString(finalSet.distance));
            distanceUnitTextView.setText(finalSet.distanceUnit);
            ((RelativeLayout) convertView.findViewById(R.id.distance_relative_layout_workout_expandable_list_group)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNumberPicker(4, finalSet.distance.intValue(), finalSet.distanceUnit, groupPosition, v);
                }
            });
        } else {
            ((RelativeLayout) convertView.findViewById(R.id.distance_relative_layout_workout_expandable_list_group)).setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        return 1;
    }

    @Override
    public Set getGroup(int groupPosition) {
        return this.sets.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.sets.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // sets header title
        String setName = "SET " + Integer.toString(groupPosition + 1);
        LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.workout_expandable_list_group, null);
        final Set set = sets.get(groupPosition);

//        if (set.time == null || set.time == 0) {
        SetSession tempSetSessionFound = null;

        for (SetSession setSession : tempSetSessions) {
            if (setSession.setId.equals(set.setId)) {
                tempSetSessionFound = setSession;
                break;
            }
        }

        // temp set session found, i.e. color must be blue,
        // TODO collapse after click

        // clickable area
        LinearLayout llViewStop = (LinearLayout) convertView.findViewById(R.id.linear_layout_stop_workout_expandable);


        if (tempSetSessionFound == null) {
            llViewStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createSetSessionForSet(set.setId);
                    onSetChangeInterface.onSetComplete(groupPosition, set, set.exerciseId);
                }
            });
        } else {
            if (tempSetSessionFound.createdDate == 0 && tempSetSessionFound.updatedDate == 0) {
                llViewStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSetChangeInterface.onSetComplete(groupPosition, set, set.exerciseId);
                    }
                });
            } else {
                llViewStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSetChangeInterface.onClear(groupPosition, sets.get(groupPosition), sets.get(groupPosition).exerciseId);
                    }
                });
            }
        }
//        }

        // draw colors for completed set sessions
        for (SetSession setSession : tempSetSessions) {
            if (setSession.setId.equals(set.setId)) {
                if (!(setSession.createdDate == 0 && setSession.updatedDate == 0)) {
                    ((LinearLayout) convertView.findViewById(R.id.ll_workout_expandable_list_group)).setBackgroundColor(mContext.getResources().getColor(R.color.hud_color));
                    ((ImageView) convertView.findViewById(R.id.iv_incomplete_set_status_workout_expandable_list_group)).setBackgroundResource(R.drawable.icon_checkmark_set_white);
                    ((PtoTextView) convertView.findViewById(R.id.set_title_workout_expandable_list_group)).setTextColor(mContext.getResources().getColor(R.color.white));
                    ((PtoTextView) convertView.findViewById(R.id.text_view_state)).setTextColor(mContext.getResources().getColor(R.color.white));
                } else {
                    ((LinearLayout) convertView.findViewById(R.id.ll_workout_expandable_list_group)).setBackgroundColor(mContext.getResources().getColor(R.color.white));
                    ((ImageView) convertView.findViewById(R.id.iv_incomplete_set_status_workout_expandable_list_group)).setBackgroundResource(R.drawable.icon_checkmark_set_gray);
                    ((PtoTextView) convertView.findViewById(R.id.set_title_workout_expandable_list_group)).setTextColor(mContext.getResources().getColor(R.color.gray));
                    ((PtoTextView) convertView.findViewById(R.id.text_view_state)).setTextColor(mContext.getResources().getColor(R.color.light_gray));
                }
                break;
            }
        }
        PtoTextView textViewSetTitle = (PtoTextView) convertView.findViewById(R.id.set_title_workout_expandable_list_group);
        textViewSetTitle.setText(setName);

        // TODO check for usage
//        PtoTextView textViewStatus = (PtoTextView) convertView.findViewById(R.id.text_view_state);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private void showNumberPicker(int type, int value, String typeValue, int groupPosition, View rlView) {
        final RelativeLayout rl = (RelativeLayout) rlView;
        final int finalType = type;
        final int finalGroupPosition = groupPosition;
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        AlertDialog dialog = alert.create();
        final NumberPicker npValues = new NumberPicker(mContext);
        final NumberPicker npTypes = new NumberPicker(mContext);
        switch (type) {
            case 0:
                alert.setTitle("Repeat");
                npValues.setMinValue(1);
                npValues.setMaxValue(100);
                npValues.setValue(value);
                npTypes.setMinValue(0);
                npTypes.setMaxValue(repeatTypes.length - 1);
                npTypes.setValue(Arrays.asList(repeatTypes).indexOf(typeValue));
                npTypes.setDisplayedValues(repeatTypes);
                break;
            case 1:
                alert.setTitle("Weight");
                npValues.setMinValue(1);
                npValues.setMaxValue(100);
                npValues.setValue(value);
                npTypes.setMinValue(0);
                npTypes.setMaxValue(weightTypes.length - 1);
                npTypes.setValue(Arrays.asList(weightTypes).indexOf(typeValue));
                npTypes.setDisplayedValues(weightTypes);
                break;
            case 3:
                alert.setTitle("Rest");
                npValues.setMinValue(1);
                npValues.setMaxValue(100);
                npValues.setValue(value);
                npTypes.setMinValue(0);
                npTypes.setMaxValue(restTypes.length - 1);
                npTypes.setValue(Arrays.asList(restTypes).indexOf(typeValue));
                npTypes.setDisplayedValues(restTypes);
                break;
            case 4:
                alert.setTitle("Distance");
                npValues.setMinValue(1);
                npValues.setMaxValue(100);
                npValues.setValue(value);
                npTypes.setMinValue(0);
                npTypes.setMaxValue(distanceTypes.length - 1);
                npTypes.setValue(Arrays.asList(distanceTypes).indexOf(typeValue));
                npTypes.setDisplayedValues(distanceTypes);
                break;
        }

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeSetData(finalType, npValues.getValue(), npTypes.getValue(), finalGroupPosition, rl);
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        LinearLayout linearLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(npTypes);
        linearLayout.addView(npValues);

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.setView(linearLayout);
        dialog.show();
    }

    private void changeSetData(int type, int value, int typeValue, int groupPosition, RelativeLayout updateView) {
        PtoTextView updateTextView = (PtoTextView) updateView.getChildAt(2);
        PtoTextView updateUnitTextView = (PtoTextView) updateView.getChildAt(3);
        Double valueDouble = Double.valueOf(Integer.toString(value));
        String valueDoubleString = Double.toString(valueDouble);
        Set originalSet = sets.get(groupPosition);
        boolean tempSetSessionExists = false;
        for (SetSession setSession : tempSetSessions) {
            if (setSession.setId.equals(originalSet.setId)) {
                // tempSetSession is already existing
                tempSetSessionExists = true;
                switch (type) {
                    case 0:
                        setSession.repetitions = value;
                        updateTextView.setText(valueDoubleString);
                        break;
                    case 1:
                        setSession.weight = valueDouble;
                        setSession.weightUnit = weightTypes[typeValue];
                        updateTextView.setText(valueDoubleString);
                        updateUnitTextView.setText(setSession.weightUnit);
                        break;
                    case 3:
                        setSession.rest = value;
                        setSession.restUnit = restTypes[typeValue];
                        updateTextView.setText(valueDoubleString);
                        updateUnitTextView.setText(setSession.restUnit);
                        break;
                    case 4:
                        setSession.distance = valueDouble;
                        setSession.distanceUnit = distanceTypes[typeValue];
                        updateTextView.setText(valueDoubleString);
                        updateUnitTextView.setText(setSession.distanceUnit);
                        break;
                }
            }
        }
        if (!tempSetSessionExists) {
            // create new tempSetSession
            SetSession newSetSession = createSetSessionForSet(originalSet.setId);

            switch (type) {
                case 0:
                    newSetSession.repetitions = value;
                    updateTextView.setText(valueDoubleString);
                    break;
                case 1:
                    newSetSession.weight = valueDouble;
                    newSetSession.weightUnit = weightTypes[typeValue];
                    updateTextView.setText(valueDoubleString);
                    updateUnitTextView.setText(newSetSession.weightUnit);
                    break;
                case 3:
                    newSetSession.rest = value;
                    newSetSession.restUnit = restTypes[typeValue];
                    updateTextView.setText(valueDoubleString);
                    updateUnitTextView.setText(newSetSession.restUnit);
                    break;
                case 4:
                    newSetSession.distance = valueDouble;
                    newSetSession.distanceUnit = distanceTypes[typeValue];
                    updateTextView.setText(valueDoubleString);
                    updateUnitTextView.setText(newSetSession.distanceUnit);
                    break;
            }
        }
        // TODO update record in screen instead of notifyDataSetChanged() ???
//        notifyDataSetChanged();
    }

    public void isSetCompleted(int groupPosition) {

    }


    public SetSession createSetSessionForSet(int setId) {
        if (tempSetSessions.size() == 0) {
            tempWorkoutSession.createdDate = Utils.getCurrentTimestamp();
            tempWorkoutSession.updatedDate = Utils.getCurrentTimestamp();
            tempWorkoutSession.sessionStartDate = Utils.getCurrentTimestamp();
            tempWorkoutSession.sessionEndDate = 0;
        }
        for (Set set : sets) {
            if (set.setId.equals(setId)) {
                SetSession newSetSession = new SetSession();
                newSetSession.workoutSessionId = tempWorkoutSession.workoutSessionId;
                newSetSession.setId = set.setId;
                newSetSession.serverSetId = set.serverSetId;
                newSetSession.clientId = tempWorkoutSession.clientId;
                newSetSession.repetitions = set.repetitions;
                newSetSession.weight = set.weight;
                newSetSession.weightUnit = set.weightUnit;
                newSetSession.rest = set.rest;
                newSetSession.restUnit = set.restUnit;
                newSetSession.distance = set.distance;
                newSetSession.distanceUnit = set.distanceUnit;
                newSetSession.time = set.time;
                newSetSession.timeUnit = set.timeUnit;
                newSetSession.createdDate = 0;
                newSetSession.updatedDate = 0;
                newSetSession.syncStatus = SyncStatus.NEW;
                newSetSession.deleted = 0;
                tempSetSessions.add(newSetSession);
                return newSetSession;
            }
        }
        return null;
    }

    public float getCompletionLevel(Integer exerciseId) {
        int totalCount = 0;
        int completedCount = 0;
        for (Set set : sets) {
            if (set.exerciseId.equals(exerciseId)) {
                totalCount++;
                for (SetSession setSession : tempSetSessions) {
                    if (setSession.setId.equals(set.setId)) {
                        if (setSession.createdDate != 0 && setSession.updatedDate != 0) {
                            completedCount++;
                        }
                        break;
                    }
                }
            }
        }
        float completionLevel = (completedCount + 0f) / (totalCount + 0f);
        return completionLevel;
    }

    public void killTimer(){
        if(activeTimer != null){
            Integer setId = activeTimer.getIdentifier();
            for(SetSession setSession:tempSetSessions){
                if(setSession.setId.equals(setId)){
                    setSession.createdDate = 0;
                    setSession.updatedDate = 0;
                    break;
                }
            }
            activeTimer.killTimer();
            timerStarted = false;
            activeTimer = null;
        }
    }
}
