package nu.pto.androidapp.base.helper;

import android.os.Handler;

public class Timer {

    Handler handler = new Handler();
    Runnable runnable;
    long interval, elapsedSeconds;
    boolean isWorking;
    private Integer identifier;

    public Timer(final long interval, final Runnable task) {
        super();

        this.interval = interval;

        runnable = new Runnable() {
            @Override
            public void run() {
                elapsedSeconds += interval / 1000;
                task.run();
                handler.postDelayed(this, interval);
            }
        };
    }

    public Timer start() {
        handler.postDelayed(runnable, interval);
        isWorking = true;
        return this;
    }

    public void pause() {
        handler.removeCallbacks(runnable);
        isWorking = false;
    }

    public long getElapsedSeconds() {
        return elapsedSeconds;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void killTimer(){
        this.handler.removeCallbacks(this.runnable);
        isWorking = false;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }
}
