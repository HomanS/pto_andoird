package nu.pto.androidapp.base.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.CustomActionBarActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.Updateable;
import nu.pto.androidapp.base.adapter.ActivityAdapter;
import nu.pto.androidapp.base.db.dao.MessageDao;
import nu.pto.androidapp.base.db.model.Client;
import nu.pto.androidapp.base.db.model.Diet;
import nu.pto.androidapp.base.db.model.Message;
import nu.pto.androidapp.base.db.model.Package;
import nu.pto.androidapp.base.db.model.Syncable;
import nu.pto.androidapp.base.db.model.Trainer;
import nu.pto.androidapp.base.db.model.Workout;
import nu.pto.androidapp.base.ui.PtoDrawerLayout;
import nu.pto.androidapp.base.ui.PtoTextView;
import nu.pto.androidapp.base.util.DbImageLoader;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;
import nu.pto.androidapp.base.util.Utils;

public class StartFragment extends BaseFragment implements Updateable {
    private static final int ACTIVITIES_PER_PAGE = 5;
    private final int WORKOUT_COMPLETE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateView();
    }

    @Override
    public void onResume() {
        updateView();
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getBaseActivity().setTitleHome();
    }

    @Override
    public void updateView() {
        int clientId = SharedPreferencesHelper.getInt(getBaseActivity(), SharedPreferencesHelper.KEY_CLIENT_ID);
        try {

            // TODO check for null entries
            Client client = getBaseActivity().getDatabaseHelper().getClientDao().getClientByClientId(clientId);
            Integer trainerId = client.trainerId;

            if (trainerId == 0) {
                getActivity().findViewById(R.id.iv_menu_icon).setVisibility(View.GONE);
                ((PtoDrawerLayout) getActivity().findViewById(R.id.drawer_layout)).setDrawerLockMode(PtoDrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                return;
            }

            Trainer trainer = getBaseActivity().getDatabaseHelper().getTrainerDao().getTrainerByTrainerId(trainerId);

            ((PtoTextView) getActivity().findViewById(R.id.trainer_name_fragment_start)).setText(trainer.firstName + " " + trainer.lastName);

            Package aPackage = getBaseActivity().getDatabaseHelper().getPackageDao().getPackageByServerPackageId(client.serverPackageId);

            ((PtoTextView) getActivity().findViewById(R.id.package_name_fragment_start)).setText(aPackage.name);

            if (client.packageEndDate != null) {
                ((PtoTextView) getActivity().findViewById(R.id.package_date_fragment_start)).setText(this.getString(R.string.string_active_until) + " " + Utils.secondFormatToDate(client.packageEndDate));
            } else {
                ((PtoTextView) getActivity().findViewById(R.id.package_date_fragment_start)).setText(getString(R.string.string_activ_prenumeration));
            }
            ImageView imageView = (ImageView) getActivity().findViewById(R.id.iv_trainer_fragment_start);

            if (imageView.getTag() == null || !imageView.getTag().equals(trainer.photo)) {
                imageView.setTag(trainer.photo);
                DbImageLoader.getInstance().load(getBaseActivity(), trainer.photo, imageView, 214, 260);
            }

            // get last 5 workouts
            ArrayList<Workout> lastWorkouts = getBaseActivity().getDatabaseHelper().getWorkoutDao().getClientLastNWorkouts(ACTIVITIES_PER_PAGE, clientId);
            // get last 5 diets
            ArrayList<Diet> lastDiets = getBaseActivity().getDatabaseHelper().getDietDao().getClientLastNDiets(ACTIVITIES_PER_PAGE, clientId);

            ArrayList<Syncable> objects = new ArrayList<Syncable>();

            objects.addAll(lastDiets);
            objects.addAll(lastWorkouts);

            Collections.sort(objects, new Comparator<Syncable>() {
                public int compare(Syncable a, Syncable b) {
                    return (a.createdDate > b.createdDate) ? -1 : 1;
                }
            });

            // pass objects to adapter and show
            ArrayList<Syncable> nObjects = new ArrayList<Syncable>();
            int nObjectsSize = objects.size() >= ACTIVITIES_PER_PAGE ? ACTIVITIES_PER_PAGE : objects.size();
            nObjects.addAll(objects.subList(0, nObjectsSize));
            objects.clear();


            ListView startPageActivityList = (ListView) getActivity().findViewById(R.id.lv_layout_activity_fragment_start);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.list_view_activity_home_page) / 5 * nObjectsSize);

            startPageActivityList.setLayoutParams(params);
            startPageActivityList.setAdapter(new ActivityAdapter(getActivity(), nObjects));

            startPageActivityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Syncable item = (Syncable) view.getTag();
                    Fragment fragment = null;
                    if (item instanceof Diet) {

                        fragment = new DietFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("diet_id", ((Diet) item).dietId);
                        fragment.setArguments(bundle);
                        getBaseActivity().setContent(fragment, null, true);

                    } else if (item instanceof Workout) {

                        Intent workoutIntent = new Intent(getActivity(), CustomActionBarActivity.class);
                        workoutIntent.putExtra("FRAGMENT_NAME", "WorkoutFragment");
                        workoutIntent.putExtra("FRAGMENT_WORKOUT_ID", ((Workout) item).workoutId);
                        startActivityForResult(workoutIntent, WORKOUT_COMPLETE);

                    }
                }
            });

            startPageActivityList.setScrollContainer(false);
            MessageDao messageDao = getBaseActivity().getDatabaseHelper().getMessageDao();
            Message message = messageDao.getClientLastMessageByClientId(clientId);
            if (message != null) {
                if (message.fromTrainer == 1) {
                    ((PtoTextView) getActivity().findViewById(R.id.message_from_fragment_start)).setText(trainer.firstName + " " + trainer.lastName);
                } else {
                    ((PtoTextView) getActivity().findViewById(R.id.message_from_fragment_start)).setText(client.firstName + " " + client.lastName);
                }
                ((PtoTextView) getActivity().findViewById(R.id.message_text_fragment_start)).setText(message.message);
                getActivity().findViewById(R.id.message_fragment_start).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getBaseActivity().setContent(new MessagesFragment(), null, true);
                    }
                });
            } else {
                getActivity().findViewById(R.id.message_fragment_start).setVisibility(View.GONE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            try {
                getBaseActivity().logoutFromApp();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

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

        if (requestCode == WORKOUT_COMPLETE && resultCode == 1) {
            // show alert
            new AlertDialog.Builder(getActivity())
                    .setMessage(clientName + ", " + getActivity().getString(R.string.string_workout_successfully_completed) + " " + trainerName)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();

        } else if (requestCode == WORKOUT_COMPLETE && resultCode == 0) {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
