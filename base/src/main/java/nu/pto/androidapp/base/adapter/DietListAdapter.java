package nu.pto.androidapp.base.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.db.model.Diet;
import nu.pto.androidapp.base.util.DbImageLoader;

public class DietListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Diet> mDiets = new ArrayList<Diet>();
    private ArrayList<Diet> allDiets = new ArrayList<Diet>();

    public DietListAdapter(Context context) {
        this.mContext = context;
    }


    public DietListAdapter(Context context, List<Diet> diets) {
        this.mContext = context;
        this.mDiets = diets;
        this.allDiets = new ArrayList<Diet>(diets);

    }

    public void setDiets(List<Diet> diets) {
        this.mDiets = diets;
        this.allDiets = new ArrayList<Diet>(diets);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDiets.size();
    }

    @Override
    public Diet getItem(int position) {
        return mDiets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.diet_list_item_layout, null);
        }

        ImageView dietImage = (ImageView) convertView.findViewById(R.id.dietImage);

        String photoId = getItem(position).photo;

        Log.i("PTO_TAG", position + "--" + photoId + "--" + dietImage.getTag());



        if (photoId != null && !photoId.isEmpty()) {

            if (dietImage.getTag() == null) {
                dietImage.setImageResource(R.drawable.diet_default);
                dietImage.setTag(photoId);
                DbImageLoader.getInstance().load(mContext, photoId, dietImage, 160, 110);
            } else if (dietImage.getTag().equals(photoId)) {
                Log.i("PTO_TAG", "EQUALS--" + dietImage.getTag());
                // do nothing
            } else {
                dietImage.setImageResource(R.drawable.diet_default);
                dietImage.setTag(photoId);
                DbImageLoader.getInstance().load(mContext, photoId, dietImage, 160, 110);
            }
        }else{
            dietImage.setTag("");
        }

        TextView dietTitle = (TextView) convertView.findViewById(R.id.dietName);
        dietTitle.setText(getItem(position).title);

        return convertView;
    }


    public void filter(String q) {
        mDiets.clear();
        if (q.length() == 0 || q.isEmpty() || q.trim().isEmpty()) {
            mDiets.addAll(allDiets);
        } else {
            for (Diet diet : allDiets) {
                if (diet.title.toLowerCase(Locale.getDefault()).startsWith(q)) {
                    mDiets.add(diet);
                }
            }
        }

        notifyDataSetChanged();
    }
}