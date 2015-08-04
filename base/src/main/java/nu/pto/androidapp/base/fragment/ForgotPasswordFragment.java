package nu.pto.androidapp.base.fragment;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import nu.pto.androidapp.base.R;


public class ForgotPasswordFragment extends BaseFragment implements Response.Listener<JSONObject>, Response.ErrorListener {
    public ForgotPasswordFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        TextView tvBack = (TextView) rootView.findViewById(R.id.textViewBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.animation_slide_out_left, R.anim.animation_slide_in_right);
                transaction.replace(R.id.container, new LoginFragment());
                transaction.commit();

            }
        });

        Button forgotPassword = (Button) rootView.findViewById(R.id.button_forgot_password);
        final EditText editTextEmail = (EditText) rootView.findViewById(R.id.edit_text_email_forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                ForgotPasswordFragment.this.getBaseActivity().doForgotPasswordRequest(email, ForgotPasswordFragment.this, ForgotPasswordFragment.this);
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button forgotPassword = (Button) getActivity().findViewById(R.id.button_forgot_password);

        final EditText editTextEmail = (EditText) getActivity().findViewById(R.id.edit_text_email_forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                ForgotPasswordFragment.this.getBaseActivity().doForgotPasswordRequest(email, ForgotPasswordFragment.this, ForgotPasswordFragment.this);
            }
        });
    }

    @Override
    public void onResponse(JSONObject response) {
        onForgotPasswordResponse();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        onForgotPasswordResponse();
    }

    public void onForgotPasswordResponse() {
        dismissLoadingDialog();
        new AlertDialog.Builder(ForgotPasswordFragment.this.getBaseActivity())
                .setTitle(getActivity().getString(R.string.forgot_password_changed_dialog_title))
                .setMessage(getActivity().getString(R.string.forgot_password_changed_dialog_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.animation_slide_out_left, R.anim.animation_slide_in_right);
                        transaction.replace(R.id.container, new LoginFragment());
                        transaction.commit();
                    }
                })
                .show();
    }
}
