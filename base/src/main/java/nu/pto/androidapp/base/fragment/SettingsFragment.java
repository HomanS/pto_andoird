package nu.pto.androidapp.base.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.android.volley.Response;
import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.CustomActionBarActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.Updateable;
import nu.pto.androidapp.base.db.model.Client;
import nu.pto.androidapp.base.ui.PtoTextView;
import nu.pto.androidapp.base.util.DbImageLoader;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

public class SettingsFragment extends BaseFragment implements Updateable, Response.Listener<JSONObject> {


    public SettingsFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        getBaseActivity().setTitle(getBaseActivity().getString(R.string.string_settings));

        Button passwordChangeButton = (Button) rootView.findViewById(R.id.button_change_password);
        passwordChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent passwordChangeIntent = new Intent(getActivity(), CustomActionBarActivity.class);
                passwordChangeIntent.putExtra("FRAGMENT_NAME", "PasswordChangeSettingsFragment");
                passwordChangeIntent.putExtra("FRAGMENT_TITLE", getActivity().getString(R.string.string_change_password));
                startActivity(passwordChangeIntent);
            }
        });

        Button showPersonalInfoButton = (Button) rootView.findViewById(R.id.b_personal_info_settings);
        showPersonalInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent personalInfoFragment = new Intent(getBaseActivity(), CustomActionBarActivity.class);
                personalInfoFragment.putExtra("FRAGMENT_NAME", "PersonalInfoFragment");
                personalInfoFragment.putExtra("FRAGMENT_TITLE", getActivity().getString(R.string.string_change_personal_info));
                startActivity(personalInfoFragment);
            }
        });

        Button unSubscribeButton = (Button) rootView.findViewById(R.id.buttonUnSubscribe);
        unSubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getActivity().getString(R.string.subscribe_dialog_message));
                builder.setNegativeButton(R.string.string_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        getBaseActivity().doUnSubscribeRequest(SettingsFragment.this);
                    }
                });

                builder.show();
            }
        });

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }


    @Override
    public void updateView() {
        int clientId = SharedPreferencesHelper.getInt(getBaseActivity(), SharedPreferencesHelper.KEY_CLIENT_ID);
        Client client = null;
        try {
            // TODO check for null entries
            client = getBaseActivity().getDatabaseHelper().getClientDao().getClientByClientId(clientId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (client != null) {
            ((PtoTextView) getActivity().findViewById(R.id.client_full_name_fragment_settings)).setText(client.firstName + " " + client.lastName);
            ((PtoTextView) getActivity().findViewById(R.id.client_phone_number_fragment_settings)).setText(client.phoneNumber);
            ((PtoTextView) getActivity().findViewById(R.id.client_email_fragment_settings)).setText(client.email);
            ((PtoTextView) getActivity().findViewById(R.id.client_date_of_birth_fragment_settings)).setText(Integer.toString(client.yearOfBirth));

            ImageView imageView = (ImageView) getActivity().findViewById(R.id.client_photo_fragment_settings);

            imageView.setTag(client.photo);

            if (client.photo != null && !client.photo.isEmpty()) {
                DbImageLoader.getInstance().load(getActivity(), client.photo, imageView);
            } else {
                if (client.photo == null) {

                } else {
                    byte[] decodedString;
                    decodedString = Base64.decode(String.valueOf(client.photoBase64), Base64.DEFAULT);

                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }

            if (client.isSubscribed == 0) {
                getActivity().findViewById(R.id.subscribeInactive).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.subscribeActive).setVisibility(View.GONE);
            } else {
                getActivity().findViewById(R.id.subscribeInactive).setVisibility(View.GONE);
                getActivity().findViewById(R.id.subscribeActive).setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onResponse(JSONObject response) {

        try {
            if (response.getInt("success") == 1) {
                if (response.getInt("force_logout") == 1){
                    getBaseActivity().doForceLogoutSync(new BaseActivity.SyncCompleteListener() {
                        @Override
                        public void onComplete() {
                            try {
                                getBaseActivity().logoutFromApp();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    getBaseActivity().doSync();
                    getActivity().findViewById(R.id.subscribeInactive).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.subscribeActive).setVisibility(View.GONE);
                }            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
