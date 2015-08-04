package nu.pto.androidapp.base.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;

import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.Updateable;
import nu.pto.androidapp.base.db.model.Diet;
import nu.pto.androidapp.base.util.DbImageLoader;


public class DietFragment extends BaseFragment implements Updateable {
    private Diet diet;
    private int dietId = 0;
    private TextView dietDescriptionTextView;
    private TextView dietTitleTextView;
    private ImageView dietImageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.diet_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dietDescriptionTextView = (TextView) view.findViewById(R.id.dietDescription);
        dietTitleTextView = (TextView) view.findViewById(R.id.dietTitle);
        dietImageView = (ImageView) view.findViewById(R.id.dietImage);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return; // todo exit fragment
        }
        if (bundle.containsKey("diet_id")) {
            dietId = bundle.getInt("diet_id");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        updateView();
    }

    @Override
    public void updateView() {
        try {
            diet = getDatabaseHelper().getDietDao().getDietByDietId(dietId);
            if (diet == null) {
                // TODO go to diet list / back
                getBaseActivity().setContent(DietListFragment.class, null, false);
            } else {
                dietDescriptionTextView.setText(Html.fromHtml(diet.description));
                dietTitleTextView.setText(diet.title);
                if (diet.photo != null && !diet.photo.isEmpty()) {
                    dietImageView = (ImageView) getActivity().findViewById(R.id.dietImage);
                    DbImageLoader.getInstance().load(getActivity(), diet.photo, dietImageView, 640, 440);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
