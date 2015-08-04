package nu.pto.androidapp.base.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import nu.pto.androidapp.base.R;

public class WorkoutScrollViewItem extends RelativeLayout {
    private int exerciseId;
    private float completionLevel;

    public WorkoutScrollViewItem(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.workout_scroll_view_options, this, true);
        HUDView hudView = (HUDView) findViewById(R.id.workout_scroll_view_hudview);
        hudView.setVisibility(GONE);
        findViewById(R.id.iv_workout_scroll_view_item).setPadding(2, 10, 2, 10);
    }

//    public WorkoutScrollViewItem(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//    }
//
//    public WorkoutScrollViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }


    public void showHideHUDView(boolean show) {
        HUDView hudView = (HUDView) findViewById(R.id.workout_scroll_view_hudview);
        if (show) {
            hudView.setVisibility(VISIBLE);
        } else {
            hudView.setVisibility(GONE);
        }
    }

    public void updateHUDView(float completionLevel) {
        this.completionLevel = completionLevel;
        HUDView hudView = (HUDView) findViewById(R.id.workout_scroll_view_hudview);
        hudView.setCompletionLevel(completionLevel);
        hudView.invalidate();
    }

    /**
     * set selected state : change size
     *
     * @param selectedState
     */
    public void setSelectedState(boolean selectedState) {
        if (selectedState) {
            findViewById(R.id.iv_workout_scroll_view_item).setPadding(-2, -10, -2, -10);
        } else {
            findViewById(R.id.iv_workout_scroll_view_item).setPadding(2, 10, 2, 10);
        }
        findViewById(R.id.iv_workout_scroll_view_item).invalidate();
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }
}
