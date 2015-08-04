package nu.pto.androidapp.base.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.helper.Timer;
import nu.pto.androidapp.base.util.Utils;

public class TimerView extends RelativeLayout {
    public interface TimerChangeInterface {
        public void onTimerComplete();
    }


    private Timer mTimer = null;
    private long requireSeconds;
    private TimerChangeInterface timerChangeInterface;

    public TimerView(Context context) {
        super(context);
        init();
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setTimerChangeInterface(TimerChangeInterface timerChangeInterface) {
        this.timerChangeInterface = timerChangeInterface;
    }

    private void init() {
        if(mTimer == null) {
            mTimer = new Timer(1000, new Runnable() {
                @Override
                public void run() {
                    if (requireSeconds == -1) {
                        ((TextView) findViewById(R.id.text_tv)).setText(Utils.getTimeFromSeconds(mTimer.getElapsedSeconds()));
                    } else {
                        if (mTimer.getElapsedSeconds() > requireSeconds) {
                            if (mTimer.isWorking()) {
                                timerChangeInterface.onTimerComplete();
                            }
                            pause();
                            return;
                        }
                        RelativeLayout hover = (RelativeLayout) findViewById(R.id.timer_linear_layout_hover);
                        LayoutParams layoutParams = (LayoutParams) hover.getLayoutParams();
                        layoutParams.width = (int) ((getWidth() - 50) / requireSeconds * mTimer.getElapsedSeconds());
                        hover.setLayoutParams(layoutParams);
                        ((TextView) findViewById(R.id.text_tv)).setText(Utils.getTimeFromSeconds(requireSeconds - mTimer.getElapsedSeconds()));
                    }
                }
            });
        }
    }

    public void start() {
        mTimer.start();
        ((TextView) findViewById(R.id.label_text_tv)).setText(getContext().getString(R.string.string_stop_timer));
    }

    public void pause() {
        mTimer.pause();
    }

    public void setRequireSeconds(long requireSeconds) {
        this.requireSeconds = requireSeconds;
        if (requireSeconds == -1) {
            ((TextView) findViewById(R.id.text_tv)).setText(getContext().getString(R.string.string_best_time));
        } else {
            ((TextView) findViewById(R.id.text_tv)).setText(Utils.getTimeFromSeconds(requireSeconds - mTimer.getElapsedSeconds()));
        }
    }

    public Timer getTimer(){
        return this.mTimer;
    }
}
