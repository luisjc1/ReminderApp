package com.richard.remindme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Richard on 8/14/2015.
 */
public class ReminderPagerActivity extends AppCompatActivity
    implements ReminderFragment.Callbacks {

    private ArrayList<Reminder> mReminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the reminder list
        mReminders = ReminderLab.get(this).getReminders();

        // Set up the view as ViewPager
        ViewPager vp = new ViewPager(this);
        vp.setId(R.id.viewPager);
        setContentView(vp);

        // Set the adapter for the ViewPager
        FragmentManager fm = getSupportFragmentManager();
        vp.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                UUID id = mReminders.get(position).getId();
                ReminderFragment fragment = ReminderFragment.newInstance(id);
                return fragment;
            }

            @Override
            public int getCount() {
                return mReminders.size();
            }
        });

        // Set the current page for the ViewPager
        UUID id = (UUID) getIntent().getSerializableExtra(ReminderFragment.EXTRA_REMINDER_ID);
        for (int i = 0; i < mReminders.size(); i++) {
            if (id.equals(mReminders.get(i).getId())) {
                vp.setCurrentItem(i);
                break;
            }
        }

        // Set the title to the Reminder's title
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Intentionally empty
            }

            @Override
            public void onPageSelected(int position) {
                Reminder r = mReminders.get(position);
                setTitle(r.getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Intentionally empty
            }
        });
    }

    @Override
    public void onReminderUpdated(Reminder r) {

    }
}
