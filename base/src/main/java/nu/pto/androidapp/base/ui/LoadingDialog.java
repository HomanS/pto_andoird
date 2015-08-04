package nu.pto.androidapp.base.ui;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import nu.pto.androidapp.base.R;

public class LoadingDialog extends Dialog {


    public LoadingDialog(Context context, int theme) {
        super(context, theme);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        setCancelable(false);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

}
