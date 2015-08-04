package nu.pto.androidapp.base;

import android.os.Bundle;

import nu.pto.androidapp.base.fragment.LoginFragment;


public class AuthActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        getFragmentManager().beginTransaction()
                .add(R.id.container, new LoginFragment())
                .commit();


    }

}