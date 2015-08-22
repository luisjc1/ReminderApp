package com.richard.remindme;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Richard on 8/14/2015.
 */
public class ReminderLab {

    private static ReminderLab sReminderLab;
    private ArrayList<Reminder> mReminders;
    private MySQLSaver mSaver;
    private Context mAppContext;
    private OnReminderListUpdate mOnReminderListUpdateListener;

    /** Private constructor */
    private ReminderLab(Context appContext) {
        mAppContext = appContext;
        mSaver = new MySQLSaver(mAppContext);
        mReminders = new ArrayList<Reminder>();
        mSaver.setOnDatabaseInteractionCompletedListener(new MySQLSaver.OnDatabaseInteractionCompleted() {
            @Override
            public void onLoadCompleted(ArrayList<Reminder> reminders) {
                Log.d("ReminderLab", "Loading task completed");
                mReminders = reminders;
                if (mOnReminderListUpdateListener != null) {
                    mOnReminderListUpdateListener.updateListView();
                }
            }
            @Override
            public void onDeleteCompleted() {
                if (mOnReminderListUpdateListener != null) {
                    mOnReminderListUpdateListener.updateListView();
                }
            }
        });
        mSaver.loadReminders();
    }

    /** Static get method */
    public static ReminderLab get(Context c) {
        if (sReminderLab == null) {
            sReminderLab =  new ReminderLab(c); //NO more c.getApplicationContext(). It messes up the ProgressDialog in MySQLSaver
        }

        return sReminderLab;
    }

    /** Modify Reminder List */
    public ArrayList<Reminder> getReminders() {
        return mReminders;
    }
    public Reminder getReminder(UUID id) {
        for (Reminder r : mReminders) {
            if (id.equals(r.getId())) {
                return r;
            }
        }
        return null;
    }
    public void addReminder(Reminder r) {
        mReminders.add(r);
    }
    public void deleteReminder(Reminder r) {
        Log.d("MySQL", "Preparing to delete");
        mSaver.deleteReminder(r);
        mReminders.remove(r);
    }
    public void saveReminder(Reminder r) {
        mSaver.saveReminder(r);
    }

    /** Set up interface for ReminderList update callback */
    public interface OnReminderListUpdate {
        void updateListView();
    }
    public void setOnReminderListUpdateListener(OnReminderListUpdate listener) {
        mOnReminderListUpdateListener = listener;
    }

}


