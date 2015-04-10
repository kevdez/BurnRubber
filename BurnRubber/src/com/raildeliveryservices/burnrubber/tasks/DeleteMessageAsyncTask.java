package com.raildeliveryservices.burnrubber.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.raildeliveryservices.burnrubber.data.Message;

import java.util.ArrayList;

public class DeleteMessageAsyncTask extends AsyncTask<ArrayList<Long>, Void, Void> {

    private Context _context;
    private ProgressDialog _progressDialog;

    public DeleteMessageAsyncTask(Context context) {
        _context = context;
    }

    @Override
    protected Void doInBackground(ArrayList<Long>... params) {

        ArrayList<Long> messageIds = params[0];

        for (long id : messageIds) {
            _context.getContentResolver().delete(Uri.withAppendedPath(Message.CONTENT_URI, String.valueOf(id)), null, null);
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        _progressDialog = ProgressDialog.show(_context, "Deleting Messages", "Message delete in progress...");
    }

    @Override
    protected void onPostExecute(Void result) {
        _progressDialog.dismiss();
    }
}
