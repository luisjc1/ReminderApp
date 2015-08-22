package com.richard.remindme;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by Richard on 8/17/2015.
 */
public class LoadingDialog extends DialogFragment {
    public LoadingDialog() { }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ProgressDialog dialog = new ProgressDialog(getActivity());
        this.setStyle(STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Dialog);
        dialog.setMessage("Loading...");

        dialog.setCancelable(false);

        return dialog;
    }
}
