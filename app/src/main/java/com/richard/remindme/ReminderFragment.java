package com.richard.remindme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Richard on 8/14/2015.
 */
public class ReminderFragment extends Fragment {

    // Extras
    public static final String EXTRA_REMINDER_ID = "com.richard.remindme.reminder_id";

    // Dialogs
    private static final String DIALOG_DATE_TIME_DECISION = "date_time_decision";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";

    // Request Codes
    private static final int REQUEST_DATE_TIME_DECISION = 0;
    private static final int REQUEST_DATE = 1;
    private static final int REQUEST_TIME = 2;

    // Members
    private Reminder mReminder;
    private Callbacks mCallbacks;

    // Widgets
    private EditText mTitleField;
    private Button mDueDateButton;
    private CheckBox mIsFinishedBox;
    private EditText mDescriptionField;

    public interface Callbacks {
        void onReminderUpdated(Reminder r);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void configureDate() {
        DatePickerFragment fragment = DatePickerFragment.newInstance(mReminder.getDueDate());
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fragment.setTargetFragment(ReminderFragment.this, REQUEST_DATE);
        fragment.show(fm, DIALOG_DATE);
    }
    private void configureTime() {
        TimePickerFragment fragment = TimePickerFragment.newInstance(mReminder.getDueDate());
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fragment.setTargetFragment(ReminderFragment.this, REQUEST_TIME);
        fragment.show(fm, DIALOG_TIME);
    }

    private void updateDate() {
        String date = DateFormat.getDateFormat(getActivity()).format(mReminder.getDueDate());
        String time = DateFormat.getTimeFormat(getActivity()).format(mReminder.getDueDate());
        mDueDateButton.setText(time + " " + date);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == REQUEST_DATE_TIME_DECISION) {
            int choice = data.getIntExtra(DateTimeDecisionDialog.EXTRA_DATE_TIME_DECISION, DateTimeDecisionDialog.CHOICE_NULL);
            if (choice == DateTimeDecisionDialog.CHOICE_DATE) {
                configureDate();
            } else if (choice == DateTimeDecisionDialog.CHOICE_TIME) {
                configureTime();
            }
        } else if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mReminder.setDueDate(date);
            mCallbacks.onReminderUpdated(mReminder);
            updateDate();
        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mReminder.setDueDate(date);
            mCallbacks.onReminderUpdated(mReminder);
            updateDate();
        }

    }

    /* Creation */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reminder, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mTitleField = (EditText) v.findViewById(R.id.reminder_title);
        mTitleField.setText(mReminder.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Intentionally empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mReminder.setTitle(s.toString());
                mCallbacks.onReminderUpdated(mReminder);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Intentionally empty
            }
        });

        mDueDateButton = (Button) v.findViewById(R.id.reminder_due_date);
        updateDate();
        mDueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeDecisionDialog fragment = new DateTimeDecisionDialog();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fragment.setTargetFragment(ReminderFragment.this, REQUEST_DATE_TIME_DECISION);
                fragment.show(fm, DIALOG_DATE_TIME_DECISION);
            }
        });

        mIsFinishedBox = (CheckBox) v.findViewById(R.id.reminder_finished);
        mIsFinishedBox.setChecked(mReminder.isFinished());
        mIsFinishedBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mReminder.setFinished(isChecked);
                mCallbacks.onReminderUpdated(mReminder);
            }
        });

        mDescriptionField = (EditText) v.findViewById(R.id.reminder_description);
        mDescriptionField.setText(mReminder.getDescription());
        mDescriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mReminder.setDescription(s.toString());
                mCallbacks.onReminderUpdated(mReminder);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID reminderId = (UUID) getArguments().getSerializable(EXTRA_REMINDER_ID);
        mReminder = ReminderLab.get(getActivity()).getReminder(reminderId);
    }
    public static ReminderFragment newInstance(UUID reminderId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_REMINDER_ID, reminderId);
        ReminderFragment fragment = new ReminderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /* Options Menu */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_reminder, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MySQLSaver", "IN PAUSE");
        ReminderLab.get(getActivity()).saveReminder(mReminder);
    }
}
