package com.richard.remindme;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Richard on 8/14/2015.
 */
public class Reminder {

    public static final int NUM_PROPERTIES = 5;

    private UUID mId;
    private Date mDueDate;
    private String mTitle;
    private String mDescription;
    private boolean mFinished;

    public Reminder() {
        mId = UUID.randomUUID();
        mDueDate = new Date();
        mTitle = "";
        mDescription = "";
        mFinished = false;
    }


    public UUID getId() {
        return mId;
    }

    public void setId(String id) {
        mId = UUID.fromString(id);
    }

    public Date getDueDate() {
        return mDueDate;
    }

    public void setDueDate(Date dueDate) {
        mDueDate = dueDate;
    }

    public void setDueDate(String dueDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM HH:mm:ss z yyyy");
        try {
            mDueDate = sdf.parse(dueDate);
        } catch (ParseException e) {
            Log.d("Reminder", e.getMessage());
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public boolean isFinished() {
        return mFinished;
    }

    public void setFinished(boolean finished) {
        mFinished = finished;
    }

    public void setFinished(String finished) {
        mFinished = finished.equals("true");
    }
}
