package nu.pto.androidapp.base.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.db.dao.ClientDao;
import nu.pto.androidapp.base.db.model.Client;
import nu.pto.androidapp.base.ui.PtoTextView;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;

import java.sql.SQLException;

public class TechnicalSupportFragment extends BaseFragment {
    private Client client;

    public TechnicalSupportFragment(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_technical_support, container, false);
        getBaseActivity().setTitle(getString(R.string.string_title_technical_support));
        try {
            ClientDao clientDao = getBaseActivity().getDatabaseHelper().getClientDao();
            int serverClientId = SharedPreferencesHelper.getInt(getBaseActivity(), SharedPreferencesHelper.KEY_SERVER_CLIENT_ID);
            this.client = clientDao.getClientByServerClientId(serverClientId);
            ((PtoTextView) rootView.findViewById(R.id.tv_name_fragment_technical_support)).setText("Hej " + client.firstName);
        } catch (SQLException e) {
            // TODO catch
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String phoneModel = Build.MANUFACTURER + " " + Build.MODEL;
        phoneModel = phoneModel.substring(0, 1).toUpperCase() + phoneModel.substring(1);
        Button bEmail = (Button) getBaseActivity().findViewById(R.id.b_email_tech_support_fragment);
        final String finalPhoneModel = phoneModel;
        bEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.support_email)});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.support_subject));
                emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.support_body_head, client.firstName + " " + client.lastName, finalPhoneModel));
                emailIntent.setType("message/rfc822");
                getBaseActivity().startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.support_mail_chooser_title)));
            }
        });
        Button bCall = (Button) getBaseActivity().findViewById(R.id.b_phone_tech_support_fragment);
        bCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dialogMessage = getResources().getString(R.string.support_phone_message) + "\n" + getResources().getString(R.string.support_phone);

                AlertDialog alertDialog = new AlertDialog.Builder(getBaseActivity()).setTitle("")
                        .setMessage(dialogMessage)
                        .setPositiveButton(getResources().getString(R.string.support_confirm), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + getResources().getString(R.string.support_phone)));
                                getBaseActivity().startActivity(callIntent);
                            }
                        })
                        .setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();

                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                textView.setGravity(Gravity.CENTER);
                alertDialog.show();
            }
        });
        Button bSendSMS = (Button) getBaseActivity().findViewById((R.id.b_sms_tech_support_fragment));
        bSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dialogMessage = getResources().getString(R.string.support_sms_message) + "\n" + getResources().getString(R.string.support_sms);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getBaseActivity());
                alertBuilder.setMessage(dialogMessage);
                alertBuilder.setPositiveButton(R.string.support_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.putExtra("sms_body", getResources().getString(R.string.support_body_head, client.firstName + " " + client.lastName, finalPhoneModel));
                        smsIntent.putExtra("address", getResources().getString(R.string.support_sms));
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        getBaseActivity().startActivity(smsIntent);
                    }
                });

                alertBuilder.setNegativeButton(getResources().getString(R.string.string_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog alertDialog = alertBuilder.show();
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                textView.setGravity(Gravity.CENTER);
                alertDialog.show();
            }
        });


    }
}
