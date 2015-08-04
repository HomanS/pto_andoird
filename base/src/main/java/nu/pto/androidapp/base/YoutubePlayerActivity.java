package nu.pto.androidapp.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubePlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    static private final String DEVELOPER_KEY = "AIzaSyCZC3BJm2H5C5Vv1FkTVVlfojnOtsoVDZA";
    static private String VIDEO_URL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String videoUrl = (String) getIntent().getExtras().get("video_url");
        this.VIDEO_URL = videoUrl;

        YouTubePlayerView youTubePlayerView = new YouTubePlayerView(this);
        setContentView(youTubePlayerView);
        youTubePlayerView.initialize(DEVELOPER_KEY, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
        Dialog dialog = error.getErrorDialog(this, error.ordinal(), new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer player, boolean wasRestored) {
        player.setShowFullscreenButton(true);
        player.loadVideo(this.VIDEO_URL);
        player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {

            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {

            }

            @Override
            public void onVideoEnded() {

            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {
                Log.w("","");
            }
        });

    }

}