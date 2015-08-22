package com.richard.remindme;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Richard on 8/14/2015.
 */
public class ReminderListFragment extends ListFragment {

    private Callbacks mCallbacks;

    public interface Callbacks {
        void onReminderSelected(Reminder reminder);
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

    private void startAddReminderActivity() {
        Reminder r = new Reminder();
        UUID id = r.getId();
        ReminderLab.get(getActivity()).addReminder(r);
        mCallbacks.onReminderSelected(r);
        updateUI();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Reminder r = ((ReminderAdapter) getListAdapter()).getItem(position);
        mCallbacks.onReminderSelected(r);
    }

    // Custom Adapter for the List View
    private class ReminderAdapter extends ArrayAdapter<Reminder> {

        public ReminderAdapter(ArrayList<Reminder> reminders) {
            super(getActivity(), 0, reminders);
        }

        public void refresh(ArrayList<Reminder> reminders) {
            Log.d("ReminderListFragment", "SIZE OF THE ADAPTER: " + this.getCount());

            ArrayList<Reminder> array = (ArrayList<Reminder>) reminders.clone();
            this.clear();
            if (reminders != null) {
                for (Reminder r : array) {
                    this.add(r);
//                    this.insert(r, this.getCount());
                }
            }
            this.notifyDataSetChanged();
            Log.d("ReminderListFragment", "SIZE OF THE ADAPTER: " + this.getCount());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Reuse old view if necessary
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_reminder, null);
            }

            Reminder r = getItem(position);

            // Set the title
            TextView reminderTitle = (TextView) convertView.findViewById(R.id.list_item_reminder_title);
            reminderTitle.setText(r.getTitle());

            // Set the due date
            TextView reminderDueDate = (TextView) convertView.findViewById(R.id.list_item_reminder_due_date);
            reminderDueDate.setText(r.getDueDate().toString());

            // Set if finished
            CheckBox reminderFinished = (CheckBox) convertView.findViewById(R.id.list_item_finished_checkBox);
            reminderFinished.setChecked(r.isFinished());

            return convertView;
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;
        ReminderAdapter adapter = ((ReminderAdapter)getListAdapter());
        Reminder r = adapter.getItem(position);
        switch (item.getItemId()) {
            case R.id.menu_item_delete_reminder:
                ReminderLab.get(getActivity()).deleteReminder(r);
                updateUI();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.context_list_reminder, menu);
    }

    // Used to allow set a different view when list is empty
    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_reminder, container, false);

        Button addReminder = (Button) v.findViewById(R.id.empty_list_add_reminder_button);
        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddReminderActivity();
            }
        });

        ListView listView = (ListView) v.findViewById(android.R.id.list);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(listView);
        } else {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    // Required but not implemented
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.context_list_reminder, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    // Required but not implemented
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_reminder:
                            ReminderAdapter adapter = (ReminderAdapter) getListAdapter();
                            ReminderLab reminderLab = ReminderLab.get(getActivity());
                            for (int i = adapter.getCount()-1; i >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    reminderLab.deleteReminder(adapter.getItem(i));
                                }
                            }
                            mode.finish();
                            updateUI();
                            return true;
                        default:
                            return false;
                    }

                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    // Required but not implemented
                }
            });
        }

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Set adapter
        ReminderLab.get(getActivity()).setOnReminderListUpdateListener(new ReminderLab.OnReminderListUpdate() {
            @Override
            public void updateListView() {
                updateUI();
            }
        });
        ReminderAdapter adapter = new ReminderAdapter(ReminderLab.get(getActivity()).getReminders());
        setListAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_reminder:
                startAddReminderActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_list_reminder, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        ((ReminderAdapter)getListAdapter()).refresh(ReminderLab.get(getActivity()).getReminders());

    }
}

