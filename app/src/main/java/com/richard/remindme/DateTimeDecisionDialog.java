package com.richard.remindme;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Richard on 8/14/2015.
 */
public class DateTimeDecisionDialog extends DialogFragment {

    // Extras
    public static final String EXTRA_DATE_TIME_DECISION = "com.richard.remindme.date_time_decision";

    // Choice
    int mChoice = CHOICE_NULL;
    public static final int CHOICE_NULL = 0;
    public static final int CHOICE_DATE = 1;
    public static final int CHOICE_TIME = 2;

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_DATE_TIME_DECISION, mChoice);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_decision_date_time, null);

        Button dateButton = (Button) v.findViewById(R.id.dialog_decision_date_button);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChoice = CHOICE_DATE;
                sendResult(Activity.RESULT_OK);
            }
        });

        Button timeButton = (Button) v.findViewById(R.id.dialog_decision_time_button);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChoice = CHOICE_TIME;
                sendResult(Activity.RESULT_OK);
            }
        });



        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_decision_message)
                .setView(v)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
