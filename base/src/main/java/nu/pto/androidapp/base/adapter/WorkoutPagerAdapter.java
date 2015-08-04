package nu.pto.androidapp.base.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.YoutubePlayerActivity;
import nu.pto.androidapp.base.db.model.Exercise;
import nu.pto.androidapp.base.util.DbImageLoader;

import java.util.ArrayList;


public class WorkoutPagerAdapter extends PagerAdapter {
    private Activity mActivity;
    private Exercise exercise;
    private ArrayList<String> decodedImageIds;
    private ArrayList<String> imageDescriptions;
    private boolean videoExists;

    public WorkoutPagerAdapter(Activity activity, Exercise exercise) {
        this.decodedImageIds = new ArrayList<String>();
        this.imageDescriptions = new ArrayList<String>();
        this.mActivity = activity;
        this.exercise = exercise;
        this.videoExists = false;

        String videoUrl = exercise.videoUrl;
        String videoDescription = exercise.videoDescription;
        if (videoUrl != null && !videoUrl.equals("")) {
            String videoId = Uri.parse(videoUrl).getQueryParameter("v");
            videoUrl = "http://img.youtube.com/vi/" + videoId + "/mqdefault.jpg";
            decodedImageIds.add(0, videoUrl);
            videoExists = true;
        }
        String[] decodedImages = exercise.imagesIds.split("#\\|#");
        String[] decodedImageDescriptions = exercise.imagesDescription.split("#\\|#");
        if (videoExists) {
            this.imageDescriptions.add(videoDescription);
        }
        for (int i = 0; i < decodedImages.length; i++) {
            this.decodedImageIds.add(decodedImages[i]);
            if (i < decodedImageDescriptions.length) {
                this.imageDescriptions.add(decodedImageDescriptions[i]);
            } else {
                this.imageDescriptions.add(" ");
            }
        }
    }

    public String getImageDescriptions(int i) {
        if (i < imageDescriptions.size()) {
            return imageDescriptions.get(i);
        }
        return "";
    }

    @Override
    public int getCount() {
        return decodedImageIds.size();
    }

    @Override
    public Object instantiateItem(View collection, final int position) {
        final ImageView imageView = new ImageView(mActivity);


        if (position == 0 && videoExists) {
            ImageView playHolder = new ImageView(mActivity);
            playHolder.setImageResource(R.drawable.btn_play);
            RelativeLayout youtubeRelativeLayout = new RelativeLayout(mActivity);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );

            Picasso.with(mActivity.getBaseContext()).load(decodedImageIds.get(position)).into(imageView);


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String videoId = Uri.parse(exercise.videoUrl).getQueryParameter("v");


                    Intent youtubeIntent = new Intent(mActivity, YoutubePlayerActivity.class);
                    youtubeIntent.putExtra("video_url", videoId);
                    mActivity.startActivity(youtubeIntent);



                }
            });
            RelativeLayout.LayoutParams ilp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            ilp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            ilp.addRule(RelativeLayout.CENTER_VERTICAL);
            imageView.setBackgroundResource(R.color.white);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            playHolder.setScaleType(ImageView.ScaleType.CENTER);
//            ilp.width = 200;
//            ilp.height = 200;
            imageView.setLayoutParams(ilp);
            playHolder.setLayoutParams(ilp);
            youtubeRelativeLayout.addView(imageView);
            youtubeRelativeLayout.addView(playHolder);
            youtubeRelativeLayout.setLayoutParams(lp);

            ((ViewPager) collection).addView(youtubeRelativeLayout, 0);
            return youtubeRelativeLayout;

        } else {
            imageView.setTag(decodedImageIds.get(position));
            if(decodedImageIds.get(position).equals("")){
                imageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.workout_default_back));
            }else{
                DbImageLoader.getInstance().load(mActivity, decodedImageIds.get(position), imageView);
            }
            ((ViewPager) collection).addView(imageView, 0);
            return imageView;
        }

    }

    @Override
    public void destroyItem(View collection, int position, Object view) {
        if (position == 0 && videoExists) {
            ((ViewPager) collection).removeView((RelativeLayout) view);
        } else {
            ((ViewPager) collection).removeView((ImageView) view);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void finishUpdate(View arg0) {
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }
}
