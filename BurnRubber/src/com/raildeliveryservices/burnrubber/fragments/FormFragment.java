package com.raildeliveryservices.burnrubber.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.FormDetailActivity;
import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.data.Form;

/**
 * Created by nghia on 04/28/2015.
 */
public class FormFragment extends ListFragment {
    private Activity mActivity;
    private MatrixCursor mFormCursor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.form_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        loadForm();
    }

    private void loadForm() {
        String[] projection = {Form.Columns._ID,
                Form.Columns.LABEL,
                Form.Columns.FORM_NAME,
                Form.Columns.FILL_IN,
                Form.Columns.FORM_TYPE};
        String selection = Form.Columns.FORM_NAME + " != 'CANNED'";
        String sortOrder = Form.Columns.FORM_NAME + "," + Form.Columns._ID;

        final Cursor cursor = mActivity.getContentResolver().query(Form.CONTENT_URI, projection, selection, null, sortOrder);
        cursor.moveToFirst();
        mFormCursor = new MatrixCursor(new String[]{Form.Columns._ID, Form.Columns.FORM_NAME});
        mFormCursor.moveToFirst();
        String formName = "";

        try {
            do {
                if (!cursor.getString(cursor.getColumnIndex(Form.Columns.FORM_NAME)).equals(formName)) {
                    mFormCursor.addRow(new Object[]{cursor.getLong(cursor.getColumnIndex(Form.Columns._ID)), cursor.getString(cursor.getColumnIndex(Form.Columns.FORM_NAME))});
                }

                formName = cursor.getString(cursor.getColumnIndex(Form.Columns.FORM_NAME));
            } while (cursor.moveToNext());
        } catch (Exception e) {

        }

        cursor.close();


        String[] mappingFrom = new String[]{Form.Columns.FORM_NAME};
        int[] mappingTo = new int[]{android.R.id.text1};

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(mActivity, android.R.layout.simple_list_item_1, mFormCursor, mappingFrom, mappingTo, 0);
        setListAdapter(simpleCursorAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mFormCursor != null && mFormCursor.getCount() > 0) {
            mFormCursor.moveToPosition(position);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE_PARAM_FORM_NAME, mFormCursor.getString(mFormCursor.getColumnIndex(Form.Columns.FORM_NAME)));
            Intent intent = new Intent(mActivity, FormDetailActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
