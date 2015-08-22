package com.richard.remindme;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.util.ArrayList;

/**
 * Created by Richard on 8/16/2015.
 */
public class MySQLSaver {
    // Debugging Tag
    private static final String TAG = "MySQLSaver";

    // Members
    private Context mContext;
    private OnDatabaseInteractionCompleted mDatabaseListener;
    private static final String FILE_LINK = "http://192.168.1.104/AndroidRemindMe/index.php";
    private static final String FILE_LOAD = "http://192.168.1.104/AndroidRemindMe/indexload.php";
    private static final String FILE_DELETE = "http://192.168.1.104/AndroidRemindMe/indexdelete.php";


    /** Constructor */
    public MySQLSaver(Context context) {
        mContext = context;
    }

    public void setOnDatabaseInteractionCompletedListener(OnDatabaseInteractionCompleted listener) {
        mDatabaseListener = listener;
    }

    /** Used to query */
    public void saveReminder(Reminder reminder) {
        String[] reminderProperties = new String[Reminder.NUM_PROPERTIES];
        reminderProperties[0] = reminder.getId().toString();
        reminderProperties[1] = reminder.getDueDate().toString();
        reminderProperties[2] = reminder.getTitle();
        reminderProperties[3] = reminder.getDescription();
        reminderProperties[4] = ""+reminder.isFinished();
        new saveReminderAsyncTask().execute(reminderProperties);
    }
    public void loadReminders() {
        new loadRemindersAsyncTask().execute();
    }
    public void deleteReminder(Reminder r) {
        String reminderId = r.getId().toString();
        new deleteReminderAsyncTask().execute(reminderId);
    }

    /** AsyncTask web service */
    private class saveReminderAsyncTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            // Send data to php file as POST
            try {
                Log.d(TAG, "In the beginning of doInBackground");
                String id = params[0];
                String dueDate = params[1];
                String title = params[2];
                String description = params[3];
                String finished = params[4];

                String data = URLEncoder.encode("id","UTF-8") + "=" + URLEncoder.encode(id,"UTF-8");
                data += "&" + URLEncoder.encode("due_date","UTF-8") + "=" + URLEncoder.encode(dueDate,"UTF-8");
                data += "&" + URLEncoder.encode("title","UTF-8") + "=" + URLEncoder.encode(title,"UTF-8");
                data += "&" + URLEncoder.encode("description","UTF-8") + "=" + URLEncoder.encode(description,"UTF-8");
                data += "&" + URLEncoder.encode("finished","UTF-8") + "=" + URLEncoder.encode(finished,"UTF-8");

                URL url = new URL(FILE_LINK);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                writer.write(data);
                writer.flush();


                //---------- Why do I need this? Why do I need to read something in order for the files to get sent to the database
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                Log.d(TAG, "RECEIVED MESSAGE FROM PHP: " + sb.toString());

                //----------
                return null;
            } catch (Exception e) {
                Log.d(TAG, "ERROR SAVING REMINDERS: " + e.getMessage());
                return "Exception: " + e.getMessage();
            }
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "Success");
        }
    }
    private class loadRemindersAsyncTask extends AsyncTask<String,Void,String> {
        private ArrayList<Reminder> mReminders;
        private ProgressDialog mProgressDialog;

        public loadRemindersAsyncTask () {
            mReminders = new ArrayList<Reminder>();
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mContext,"","Loading Reminders",false,true);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.d(TAG, "In dobackground");
                URL url = new URL(FILE_LOAD);
                Log.d(TAG, "MadeURL");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                Log.d(TAG, "RESPONSE: " + conn.getResponseMessage());

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line+"\n");
                }
                String[] properties = sb.toString().split("\n");
                for (int i = 0; i < properties.length; i+=Reminder.NUM_PROPERTIES) {
                    Reminder r = new Reminder();
                    r.setId(properties[i]);
                    r.setDueDate(properties[i + 1]);
                    r.setTitle(properties[i + 2]);
                    r.setDescription(properties[i + 3]);
                    r.setFinished(properties[i + 4]);
                    mReminders.add(r);
                }
                Log.d(TAG, "COUNT: " + mReminders.size());
                return null;
            } catch (Exception e) {
                Log.d(TAG, "ERROR: " + e.getMessage());
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            ((AppCompatActivity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mDatabaseListener != null) {
                        mDatabaseListener.onLoadCompleted(mReminders);
                    }
                    mProgressDialog.dismiss();
                }
            });

        }
    }
    private class deleteReminderAsyncTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            try {

                String id = params[0];
                String data = URLEncoder.encode("id","UTF-8") + "=" + URLEncoder.encode(id,"UTF-8");
                URL url = new URL(FILE_DELETE);
                URLConnection conn = url.openConnection();


                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                Log.d(TAG, "RESPONSE FROM PHP: " + sb.toString());
                return null;
            } catch (Exception e) {
                return "ERROR: " + e.getMessage();
            }
        }
        @Override
        protected void onPostExecute(String s) {
            ((AppCompatActivity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mDatabaseListener != null) {
                        mDatabaseListener.onDeleteCompleted();
                    }
                }
            });

        }
    }

    /** Used to notify ReminderLab when AsyncTask is finished*/
    public interface OnDatabaseInteractionCompleted {
        void onLoadCompleted(ArrayList<Reminder> reminders);
        void onDeleteCompleted();
        // onSaveCompleted is unnecessary for now
    }


}
