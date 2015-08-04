package nu.pto.androidapp.base.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.R;

import java.sql.SQLException;

public class LogoutFragment extends BaseFragment {
    public LogoutFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logout, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        showLoadingDialog();
        LogoutFragment.this.getBaseActivity().doLogoutSync(new BaseActivity.SyncCompleteListener() {
            @Override
            public void onComplete() {
                try {
                    getBaseActivity().logoutFromApp();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
