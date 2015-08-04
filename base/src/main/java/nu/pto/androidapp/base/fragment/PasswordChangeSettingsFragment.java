package nu.pto.androidapp.base.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import com.android.volley.Response;
import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.db.dao.ClientDao;
import nu.pto.androidapp.base.db.model.Client;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;


public class PasswordChangeSettingsFragment extends BaseFragment implements Response.Listener<JSONObject> {
    private boolean backButtonEnabled;
    private String tempPassword;

    public PasswordChangeSettingsFragment(boolean backButtonEnabled) {
        this.backButtonEnabled = backButtonEnabled;
    }

    public PasswordChangeSettingsFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_password_settings, container, false);

        ImageView bCloseCustomActionBarActivity = (ImageView) getActivity().findViewById(R.id.btn_cancel_custom_action_bar_activity);
        if (backButtonEnabled) {
            bCloseCustomActionBarActivity.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().finish();
                        }
                    }
            );
        } else {
            EditText etOldPassword = (EditText) rootView.findViewById(R.id.old_change_password_custom_action_bar);
            etOldPassword.setVisibility(View.GONE);
            bCloseCustomActionBarActivity.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().finish();
                        }
                    }
            );
        }
        ImageView bSaveCustomActionBarActivity = (ImageView) getActivity().findViewById(R.id.btn_save_custom_action_bar_activity);
        bSaveCustomActionBarActivity.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // change
                        EditText etNewPassword = (EditText) getActivity().findViewById(R.id.new_change_password_custom_action_bar);
                        EditText etConfirmPassword = (EditText) getActivity().findViewById(R.id.confirm_change_password_custom_action_bar);
                        EditText etOldPassword = (EditText) getActivity().findViewById(R.id.old_change_password_custom_action_bar);
                        String oldPassword;
                        if (backButtonEnabled) {
                            oldPassword = etOldPassword.getText().toString();
                            oldPassword = MD5("pto_password_Vd!dfiP#f" + oldPassword);
                        } else {
                            int serverClientId = SharedPreferencesHelper.getInt(getBaseActivity(), SharedPreferencesHelper.KEY_SERVER_CLIENT_ID);
                            try {
                                Client client = getBaseActivity().getDatabaseHelper().getClientDao().getClientByServerClientId(serverClientId);
                                oldPassword = client.password;
                            } catch (SQLException e) {
                                e.printStackTrace();
                                oldPassword = "";
                            }
                        }
                        final String newPassword = etNewPassword.getText().toString();
                        String confirmPassword = etConfirmPassword.getText().toString();
                        if (newPassword.equals(confirmPassword)) {
                            PasswordChangeSettingsFragment.this.getBaseActivity().doChangePasswordRequest(oldPassword, newPassword, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String responseMessage = response.getString("message");
                                        if (responseMessage.equals("password_changed")) {
                                            // password successfully changed, change in db
                                            try {
                                                ClientDao clientDao = getBaseActivity().getDatabaseHelper().getClientDao();
                                                int serverClientId = SharedPreferencesHelper.getInt(getBaseActivity(), SharedPreferencesHelper.KEY_SERVER_CLIENT_ID);
                                                if (clientDao.updateClientPasswordByServerClientId(serverClientId, MD5("pto_password_Vd!dfiP#f" + newPassword))) {
                                                    // password successfully changed in db, close passwordChange activity
                                                    getActivity().finish();
                                                } else {
                                                    // TODO password not changed, print error message or call sync ?

                                                }
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                                // TODO password not changed, print error message or call sync ?

                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Change password")
                                    .setMessage("New password and confirmation must be the same")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        }
                    }
                }
        );

        return rootView;
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            String message = response.get("message").toString();
            Log.i("___message", message);
            getBaseActivity().doFullSync(new BaseActivity.SyncCompleteListener() {

                @Override
                public void onComplete() {

                    getActivity().finish();
//                    Intent intent = new Intent(getActivity().getApplicationContext(), AppActivity.class);
//                    startActivity(intent);
                }

            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}
