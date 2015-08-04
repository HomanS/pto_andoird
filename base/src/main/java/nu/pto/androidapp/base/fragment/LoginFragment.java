package nu.pto.androidapp.base.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

import nu.pto.androidapp.base.AppActivity;
import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.CustomActionBarActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.db.dao.ClientDao;
import nu.pto.androidapp.base.db.model.Client;
import nu.pto.androidapp.base.util.Constants;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;

public class LoginFragment extends BaseFragment implements Response.Listener<JSONObject> {

    private static String USER_TYPE_CLIENT = "client";
    private static String USER_TYPE_TRAINER = "trainer";

    public LoginFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        TextView tvForgotPassword = (TextView) rootView.findViewById(R.id.textViewForgotPassword);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.animation_slide_in_left, R.anim.animation_slide_out_right);
                transaction.replace(R.id.container, new ForgotPasswordFragment());
                transaction.commit();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button buttonLogin = (Button) getActivity().findViewById(R.id.buttonLogin);

        final EditText editTextEmail = (EditText) getActivity().findViewById(R.id.editTextEmail);
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    getActivity().findViewById(R.id.clear_email).setVisibility(View.VISIBLE);
                } else {
                    getActivity().findViewById(R.id.clear_email).setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getActivity().findViewById(R.id.clear_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextEmail.setText("");
            }
        });

        editTextEmail.setText(SharedPreferencesHelper.getString(getActivity(), SharedPreferencesHelper.KEY_EMAIL));

        if (editTextEmail.getText().length() > 0) {
            getActivity().findViewById(R.id.clear_email).setVisibility(View.VISIBLE);
        } else {
            getActivity().findViewById(R.id.clear_email).setVisibility(View.GONE);
        }

        final EditText editTextPassword = (EditText) getActivity().findViewById(R.id.editTextPassword);
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    getActivity().findViewById(R.id.clear_password).setVisibility(View.VISIBLE);
                } else {
                    getActivity().findViewById(R.id.clear_password).setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getActivity().findViewById(R.id.clear_password).setVisibility(View.GONE);
        getActivity().findViewById(R.id.clear_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPassword.setText("");
            }
        });


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                getBaseActivity().sendGoogleAnalyticsData(Constants.ANALYTICS_EVENT_LOGIN);
                // TODO check for empty email and password

                SharedPreferencesHelper.putString(getActivity(), SharedPreferencesHelper.KEY_EMAIL, email);

                LoginFragment.this.getBaseActivity().doLoginRequest(email, password, LoginFragment.this);
                showLoadingDialog();
            }
        });
        boolean showVersionCodeErrorDialogFlag = getActivity().getIntent().getBooleanExtra("SHOW_VERSION_CODE_ERROR", false);
        if (showVersionCodeErrorDialogFlag) {
            getBaseActivity().showVersionCodeErrorDialog();
        }
    }


    @Override
    public void onResponse(JSONObject response) {
        Log.d(BaseActivity.LOG_TAG, response.toString());
        try {

            final String userType = response.getString("type");

            SharedPreferencesHelper.putString(getActivity(), SharedPreferencesHelper.KEY_USER_TYPE, userType);

            int assigned = 1;
            if (response.has("assigned")) {
                assigned = response.getInt("assigned");
            }

            if(assigned == 0) {
                dismissLoadingDialog();

                final View noTrainerView = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_start_no_trainer, null, false);
                noTrainerView.findViewById(R.id.bt_logout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showLoadingDialog();
                        ((ViewGroup)getView()).removeView(noTrainerView);
                        getBaseActivity().doLogoutSync(new BaseActivity.SyncCompleteListener() {
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
                });
                ((ViewGroup)getView()).addView(noTrainerView);

                return;
            }

            int isUserExpired = 0;
            if (response.has("expired_user")) {
                isUserExpired = response.getInt("expired_user");
            }

            if (isUserExpired == 1) {
                dismissLoadingDialog();
                getBaseActivity().showLoginFailedErrorDialog();
                return;
            }

            JSONObject userObject = response.getJSONObject("user");




            if (userType.equals(USER_TYPE_CLIENT)) {
                SharedPreferencesHelper.putInt(getActivity(), SharedPreferencesHelper.KEY_SERVER_CLIENT_ID, userObject.getInt("server_client_id"));
                if (userObject.getString("server_trainer_id").equals("null")) {
                    SharedPreferencesHelper.putInt(getActivity(), SharedPreferencesHelper.KEY_SERVER_TRAINER_ID, 0);
                } else {
                    SharedPreferencesHelper.putInt(getActivity(), SharedPreferencesHelper.KEY_SERVER_TRAINER_ID, userObject.getInt("server_trainer_id"));
                }
                SharedPreferencesHelper.putInt(getActivity(), SharedPreferencesHelper.KEY_IS_EXPIRED_USER, isUserExpired);
            } else if (userType.equals(USER_TYPE_TRAINER)) {
                // TODO show alert dialog
                dismissLoadingDialog();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.login_failed_dialog_title));
                builder.setMessage(getString(R.string.login_failed_dialog_message));
                builder.setPositiveButton("OK", null);
                Dialog loginFailedDialog = builder.create();
                loginFailedDialog.show();
                return;
            }


            getBaseActivity().doFullSync(new BaseActivity.SyncCompleteListener() {

                @Override
                public void onComplete() {

                    try {
                        if (userType.equals(USER_TYPE_CLIENT)) {
                            int serverClientId = SharedPreferencesHelper.getInt(getActivity(), SharedPreferencesHelper.KEY_SERVER_CLIENT_ID);
                            Client client = getDatabaseHelper().getClientDao().getClientByServerClientId(serverClientId);
                            if (client != null && client.clientId != null) {
                                SharedPreferencesHelper.putInt(getActivity(), SharedPreferencesHelper.KEY_CLIENT_ID, client.clientId);
                                SharedPreferencesHelper.putInt(getActivity(), SharedPreferencesHelper.KEY_TRAINER_ID, client.trainerId);
                            }
                        } else if (userType.equals(USER_TYPE_TRAINER)) {
                            // TODO implement for trainer
                        }


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    dismissLoadingDialog();
                    Intent intent = new Intent(getActivity().getApplicationContext(), AppActivity.class);
                    startActivity(intent);

                    if (checkForChangedPassword()) {
                        Intent passwordChangedIntent = new Intent(getActivity(), CustomActionBarActivity.class);
                        passwordChangedIntent.putExtra("FRAGMENT_NAME", "PasswordChangeFragment");
                        passwordChangedIntent.putExtra("FRAGMENT_TITLE", getActivity().getString(R.string.string_change_password));
                        startActivity(passwordChangedIntent);
                    }

                    getActivity().finish();
                }

            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * check if clients password is changed
     * //TODO also add check for trainer
     *
     * @return if password changed
     */
    private boolean checkForChangedPassword() {

        try {
            ClientDao clientDao = getDatabaseHelper().getClientDao();
            Client client = clientDao.getClientByClientId(SharedPreferencesHelper.getInt(getActivity(), SharedPreferencesHelper.KEY_CLIENT_ID));
            if (client != null && client.passwordChanged == 1) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
