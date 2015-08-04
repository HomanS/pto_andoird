package nu.pto.androidapp.base.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import nu.pto.androidapp.base.CustomActionBarActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.adapter.WorkoutAdapter;
import nu.pto.androidapp.base.adapter.WorkoutLogAdapter;
import nu.pto.androidapp.base.db.model.Client;
import nu.pto.androidapp.base.db.model.Trainer;
import nu.pto.androidapp.base.db.model.Workout;
import nu.pto.androidapp.base.db.model.WorkoutSession;
import nu.pto.androidapp.base.ui.PtoButton;
import nu.pto.androidapp.base.util.Constants;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;

import java.sql.SQLException;
import java.util.ArrayList;


public class WorkoutsFragment extends BaseFragment {
    private final int WORKOUT_COMPLETE = 1;

    public WorkoutsFragment() {
        super();
        this.trainingIsSelected = true;
    }

    PtoButton trainingButton, logButton;
    WorkoutAdapter workoutAdapter;
    WorkoutLogAdapter workoutLogAdapter;
    ListView workoutList;
    private boolean trainingIsSelected;

    private View lastListChild, lastListChildContent;
    int initialLeftPosition = 0 , initialRightPosition = 0 , currentStartLeftPosition = 0 , currentStartRightPosition = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workouts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getBaseActivity().setTitle(getString(R.string.fragment_title_workouts));


        try {
            int clientId = SharedPreferencesHelper.getInt(getBaseActivity(), SharedPreferencesHelper.KEY_CLIENT_ID);
            // TODO check for null entries


            Client client = getBaseActivity().getDatabaseHelper().getClientDao().getClientByClientId(clientId);
            final ArrayList<Workout> workouts = getBaseActivity().getDatabaseHelper().getWorkoutDao().getWorkoutsByClientIdAndExistingSets(client.clientId);

            final ArrayList<Workout> workoutsWithDeleted = getBaseActivity().getDatabaseHelper().getWorkoutDao().getWorkoutsWithDeletedByClientId(client.clientId);
            final ArrayList<WorkoutSession> workoutSessions = getBaseActivity().getDatabaseHelper().getWorkoutSessionDao().getWorkoutSessionsByClientId(client.clientId);
            workoutList = (ListView) getActivity().findViewById(R.id.workout_list_fragment_workout);

            getActivity().findViewById(R.id.ib_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().findViewById(R.id.fl_workout_desc).setVisibility(View.GONE);
                }
            });

            final GestureDetector detector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {

                @Override
                public boolean onDown(MotionEvent motionEvent) {
                    int position = workoutList.pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
                    position -= workoutList.getFirstVisiblePosition();
                    if (position < 0) {
                        return false;
                    }
                    ViewGroup child = (ViewGroup) workoutList.getChildAt(position);
                    if (child != null) {
                        RelativeLayout childContent = (RelativeLayout) child.findViewById(R.id.rl_content);
                        if (lastListChildContent != null && childContent != lastListChildContent) {
                            lastListChildContent.animate().x(initialLeftPosition);
                        }

                        lastListChildContent = childContent;
                        lastListChild = child;
                        currentStartLeftPosition = childContent.getLeft();
                        currentStartRightPosition = childContent.getRight();
                    }
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent motionEvent) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {

                    int position = workoutList.pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
                    position -= workoutList.getFirstVisiblePosition();

                    if (position < 0) {
                        return false;
                    }

                    position += workoutList.getFirstVisiblePosition();


                    if (trainingButton.isSelected()) {
                        getBaseActivity().sendGoogleAnalyticsData(Constants.ANALYTICS_EVENT_WORKOUT);
                        int workoutId = workouts.get(position).workoutId;
                        Intent workoutIntent = new Intent(getActivity(), CustomActionBarActivity.class);
                        workoutIntent.putExtra("FRAGMENT_NAME", "WorkoutFragment");
                        workoutIntent.putExtra("FRAGMENT_WORKOUT_ID", workoutId);
                        startActivityForResult(workoutIntent, WORKOUT_COMPLETE);


                        if (initialLeftPosition == 0 && lastListChildContent != null) {
                            initialLeftPosition = lastListChildContent.getLeft();
                        }
                        if(lastListChildContent != null) {
                            lastListChildContent.animate().x(initialLeftPosition);
                        }

                    } else {
                        int workoutId = workoutSessions.get(position).workoutId;
                        int workoutSessionId = workoutSessions.get(position).workoutSessionId;
                        getBaseActivity().setContent(new WorkoutExerciseFragment(workoutId, workoutSessionId), null, true);
                    }

                    return false;
                }

                @Override
                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
                    if (motionEvent == null || motionEvent2 == null) return false;
                    if(lastListChildContent == null) {
                        return false;
                    }

                    if (initialLeftPosition == 0) {
                        initialLeftPosition = lastListChildContent.getLeft();
                    }
                    if (initialRightPosition == 0) {
                        initialRightPosition = lastListChildContent.getRight();
                    }

                    int difference = (int) (motionEvent2.getX() - motionEvent.getX());

                    int newLeftPosition = currentStartLeftPosition + difference;
                    int newRightPosition = currentStartRightPosition + difference;
                    if (newLeftPosition > initialLeftPosition) {
                        newLeftPosition = initialLeftPosition;
                        newRightPosition = initialRightPosition;
                    }

                    ImageButton infoButton = (ImageButton) lastListChild.findViewById(R.id.ib_info);
                    if (newLeftPosition < -infoButton.getWidth()) {
                        newLeftPosition = -infoButton.getWidth();
                        newRightPosition = initialRightPosition - infoButton.getWidth();
                    }

                    lastListChildContent.setLeft(newLeftPosition);
                    lastListChildContent.setRight(newRightPosition);
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent motionEvent) {

                }

                @Override
                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {

                    return false;
                }
            });

            workoutList.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if ((motionEvent.getAction() == MotionEvent.ACTION_CANCEL || motionEvent.getAction() == MotionEvent.ACTION_UP )
                            && lastListChild != null) {

                        ImageButton infoButton = (ImageButton) lastListChild.findViewById(R.id.ib_info);
                        int newLeftPosition;
                        if (lastListChildContent.getLeft() < -infoButton.getWidth() / 2 + initialLeftPosition/2) {
                            newLeftPosition = -infoButton.getWidth();
                            infoButton.setClickable(true);
                        } else {
                            newLeftPosition = initialLeftPosition;
                            infoButton.setClickable(false);
                        }
                        lastListChildContent.animate().x(newLeftPosition);

                    }

                    detector.onTouchEvent(motionEvent);
                    return false;
                }
            });

            workoutAdapter = new WorkoutAdapter(getActivity(), workouts);
            workoutLogAdapter = new WorkoutLogAdapter(getActivity(), workoutsWithDeleted, workoutSessions);


            workoutList.setAdapter(workoutAdapter);

            trainingButton = (PtoButton) getActivity().findViewById(R.id.training_btn);
            logButton = (PtoButton) getActivity().findViewById(R.id.log_btn);

            trainingButton.setSelected(trainingIsSelected);
            if (trainingIsSelected) {
                trainingButton.setTextColor(Color.BLACK);
                logButton.setTextColor(Color.WHITE);
                workoutList.setAdapter(workoutAdapter);
            } else {
                trainingButton.setTextColor(Color.WHITE);
                logButton.setTextColor(Color.BLACK);
                workoutList.setAdapter(workoutLogAdapter);
            }


            logButton.setSelected(!trainingIsSelected);

            trainingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!trainingButton.isSelected()) {
                        trainingIsSelected = true;
                        trainingButton.setSelected(true);
                        trainingButton.setTextColor(Color.BLACK);
                        logButton.setSelected(false);
                        logButton.setTextColor(Color.WHITE);
                        workoutList.setAdapter(workoutAdapter);
                    }
                }
            });

            logButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!logButton.isSelected()) {
                        getBaseActivity().sendGoogleAnalyticsData(Constants.ANALYTICS_EVENT_WORKOUT_LOG);
                        trainingIsSelected = false;
                        logButton.setSelected(true);
                        logButton.setTextColor(Color.BLACK);
                        trainingButton.setSelected(false);
                        trainingButton.setTextColor(Color.WHITE);
                        workoutList.setAdapter(workoutLogAdapter);
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    AlertDialog workoutSuccessCompleteAlertDialog = null;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int clientId = SharedPreferencesHelper.getInt(getBaseActivity(), SharedPreferencesHelper.KEY_CLIENT_ID);
        Client client = null;
        Trainer trainer = null;
        String clientName = "";
        String trainerName = "";
        try {
            client = getBaseActivity().getDatabaseHelper().getClientDao().getClientByClientId(clientId);
            trainer = getBaseActivity().getDatabaseHelper().getTrainerDao().getTrainerByTrainerId(client.trainerId);
            clientName = client.firstName;
            trainerName = trainer.firstName;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WORKOUT_COMPLETE && resultCode == 1) {
            // show alert
            if (workoutSuccessCompleteAlertDialog == null) {
                workoutSuccessCompleteAlertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage(clientName + ", " + getActivity().getString(R.string.string_workout_successfully_completed) + " " + trainerName)
                        .setPositiveButton(getActivity().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
            }
            workoutSuccessCompleteAlertDialog.show();


        } else if (requestCode == WORKOUT_COMPLETE && resultCode == 0) {
            // do nothing

        }
    }

    @Override
    public void onPause() {
        if (workoutSuccessCompleteAlertDialog != null) {
            workoutSuccessCompleteAlertDialog.dismiss();
            workoutSuccessCompleteAlertDialog = null;
        }
        super.onPause();

    }
}
