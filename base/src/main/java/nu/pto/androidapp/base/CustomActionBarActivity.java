package nu.pto.androidapp.base;

import android.content.Intent;
import android.os.Bundle;

import nu.pto.androidapp.base.fragment.PasswordChangeSettingsFragment;
import nu.pto.androidapp.base.fragment.PersonalInfoFragment;
import nu.pto.androidapp.base.fragment.WorkoutFragment;
import nu.pto.androidapp.base.ui.PtoTextView;

public class CustomActionBarActivity extends BaseActivity {
    private String currentFragment;
    private WorkoutFragment workoutFragment = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String fragmentName = getIntent().getStringExtra("FRAGMENT_NAME");
        currentFragment = fragmentName;
        String fragmentTitle = getIntent().getStringExtra("FRAGMENT_TITLE");
        setContentView(R.layout.activity_custom_action_bar);
        setTitle(fragmentTitle);

        if (fragmentName.equals("PasswordChangeFragment")) {
            getFragmentManager().beginTransaction()
                    .add(R.id.custom_action_bar_container, new PasswordChangeSettingsFragment(false))
                    .commit();
        } else if (fragmentName.equals("PasswordChangeSettingsFragment")) {
            getFragmentManager().beginTransaction()
                    .add(R.id.custom_action_bar_container, new PasswordChangeSettingsFragment(true))
                    .commit();

        } else if (fragmentName.equals("PersonalInfoFragment")) {
            getFragmentManager().beginTransaction()
                    .add(R.id.custom_action_bar_container, new PersonalInfoFragment())
                    .commit();
        } else if (fragmentName.equals("WorkoutFragment")) {
            workoutFragment = new WorkoutFragment();
            Bundle bundle = new Bundle();

            bundle.putInt("workout_id", getIntent().getIntExtra("FRAGMENT_WORKOUT_ID", 0));
            workoutFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .add(R.id.custom_action_bar_container, workoutFragment)
                    .commit();
        }
    }

    public void sendIntentResult(int resultCode) {
        Intent returnIntent = new Intent();
        setResult(resultCode, returnIntent);
        finish();
    }

    public void setTitle(String title) {
        if (title != null) {
            if (title.length() > 20) {
                title = title.substring(0, 20) + "...";
            }
        }

        PtoTextView ptoTextView = (PtoTextView) findViewById(R.id.custom_action_bar_activity_title_tv);
        if (ptoTextView != null && title != null) {
            ptoTextView.setText(title);
        }
    }

    @Override
    public void onBackPressed() {
        if (currentFragment.equals("WorkoutFragment")) {
            if (workoutFragment != null) {
                workoutFragment.finishWorkout();
            }
        } else {
            super.onBackPressed();
        }
    }
}
