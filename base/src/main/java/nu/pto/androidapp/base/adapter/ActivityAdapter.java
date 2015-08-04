package nu.pto.androidapp.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.db.model.Diet;
import nu.pto.androidapp.base.db.model.Syncable;
import nu.pto.androidapp.base.db.model.Workout;
import nu.pto.androidapp.base.ui.PtoTextView;


public class ActivityAdapter extends BaseAdapter {


    private Context mContext;
    private List<Syncable> mObjects;

    public ActivityAdapter(Context context, List<Syncable> objects) {
        this.mContext = context;
        this.mObjects = objects;
    }

    public void setDiets(List<Syncable> objects) {
        this.mObjects = objects;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Syncable getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (getItem(position) instanceof Diet) {
                convertView = (View) inflater.inflate(R.layout.item_fragment_start_activity_diet, null);
            } else {
                convertView = (View) inflater.inflate(R.layout.item_fragment_start_activity_workout, null);
            }
        }


        PtoTextView ptvType = (PtoTextView) convertView.findViewById(R.id.type_layout_activity);
        PtoTextView ptvName = (PtoTextView) convertView.findViewById(R.id.name_layout_activity);

        if (getItem(position) instanceof Diet) {
            Diet diet = (Diet) getItem(position);
            ptvType.setText(mContext.getString(R.string.string_new_diet));
            ptvName.setText(diet.title);

        } else if (getItem(position) instanceof Workout) {

            final Workout workout = (Workout) getItem(position);
            ptvType.setText(mContext.getString(R.string.string_new_workout));
            ptvName.setText(workout.workoutName);
        }

        convertView.setTag(getItem(position));
        return convertView;
    }
}