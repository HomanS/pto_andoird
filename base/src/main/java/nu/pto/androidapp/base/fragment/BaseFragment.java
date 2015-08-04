package nu.pto.androidapp.base.fragment;

import android.app.Fragment;
import android.os.Bundle;

import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.db.DatabaseHelper;

public class BaseFragment extends Fragment {


    private BaseActivity activity = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    public DatabaseHelper getDatabaseHelper() {
        return getBaseActivity().getDatabaseHelper();
    }

    public void showLoadingDialog() {
        getBaseActivity().showLoadingDialog();
    }

    public void dismissLoadingDialog() {
        getBaseActivity().dismissLoadingDialog();
    }

    public BaseActivity getBaseActivity() {
        if (activity == null) {
            activity = (BaseActivity) getActivity();
        }
        if(activity == null) {
            activity = BaseActivity.staticInstance;
        }
        return activity;
    }

}
