package com.richard.remindme;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ReminderListActivity extends SingleFragmentActivity
    implements ReminderListFragment.Callbacks, ReminderFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new ReminderListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onReminderSelected(Reminder reminder) {
        // Check if tablet fragment container id exists
        if (findViewById(R.id.detailFragmentContainer) == null) {
            // Use phone interface
            Intent i = new Intent(this, ReminderPagerActivity.class);
            i.putExtra(ReminderFragment.EXTRA_REMINDER_ID, reminder.getId());
            startActivity(i);
        } else {
            // Use tablet interface
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newDetail = ReminderFragment.newInstance(reminder.getId());

            if (oldDetail != null) {
                ft.remove(oldDetail);
            }

            ft.add(R.id.detailFragmentContainer, newDetail);
            ft.commit();
        }
    }

    @Override
    public void onReminderUpdated(Reminder r) {
        FragmentManager fm = getSupportFragmentManager();
        ReminderListFragment listFragment = (ReminderListFragment) fm.findFragmentById(R.id.fragmentContainer);
        listFragment.updateUI();
    }
}
