package com.raildeliveryservices.burnrubber.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.adapters.MessageListCursorAdapter;
import com.raildeliveryservices.burnrubber.data.Form;
import com.raildeliveryservices.burnrubber.data.Message;
import com.raildeliveryservices.burnrubber.tasks.DeleteMessageAsyncTask;
import com.raildeliveryservices.burnrubber.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MessageListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_MESSAGES = -1;

    private Activity _activity;
    private long _orderId;
    private int _fileNo;
    private Button _sendMessageButton;
    private EditText _messageEditText;
    private MessageListCursorAdapter _listAdapter;
    private Callbacks _callbacks;
    private OnClickListener _buttonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sendMessageButton:
                    saveMessage("MSG", null);
                    break;
                case R.id.cannedMessageButton:
                    // TODO: Show alternate dialog if user is not logged in
                    showCannedMessageDialog();
                    break;
                case R.id.purgeMessageButton:
                    showPurgeMessageDialog();
                    break;
                case R.id.cancelMessageButton:
                    _callbacks.onCancelButtonClick();
                    break;
            }
        }
    };
    private TextWatcher _messageTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count > 0) {
                _sendMessageButton.setEnabled(true);
            } else {
                _sendMessageButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public MessageListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _activity = getActivity();

        Bundle bundle = getArguments();
        if (bundle != null) {
            _orderId = bundle.getLong(Constants.BUNDLE_PARAM_ORDER_ID);
        }

        _sendMessageButton = (Button) _activity.findViewById(R.id.sendMessageButton);
        Button cannedMessageButton = (Button) _activity.findViewById(R.id.cannedMessageButton);
        Button purgeMessageButton = (Button) _activity.findViewById(R.id.purgeMessageButton);
        Button cancelMessageButton = (Button) _activity.findViewById(R.id.cancelMessageButton);

        _sendMessageButton.setOnClickListener(_buttonListener);
        _sendMessageButton.setEnabled(false);

        cannedMessageButton.setOnClickListener(_buttonListener);
        purgeMessageButton.setOnClickListener(_buttonListener);
        cancelMessageButton.setOnClickListener(_buttonListener);

        _messageEditText = (EditText) _activity.findViewById(R.id.messageEditText);
        _messageEditText.addTextChangedListener(_messageTextWatcher);

        _listAdapter = new MessageListCursorAdapter(_activity, R.layout.message_list_row, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setListAdapter(_listAdapter);
        registerForContextMenu(getListView());

        Loader<Cursor> loader = getLoaderManager().getLoader(LOADER_MESSAGES);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(LOADER_MESSAGES, null, this);
        } else {
            getLoaderManager().initLoader(LOADER_MESSAGES, null, this);
        }

        final ListView listView = this.getListView();

        listView.requestFocus();
        _activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.setMessageAlertFlag(_activity, Utils.getDriverNo(_activity), false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.setMessageAlertFlag(_activity, Utils.getDriverNo(_activity), false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_list_fragment, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            _callbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement Callbacks");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle(_activity.getString(R.string.message_options_menu_title));
        MenuInflater inflater = new MenuInflater(_activity);
        inflater.inflate(R.menu.message_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Cursor cursor = _listAdapter.getCursor();
        final long messageId = cursor.getLong(cursor.getColumnIndex(Message.Columns._ID));

        switch (item.getItemId()) {
            case R.id.delete_message:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(_activity);
                alertBuilder.setTitle(_activity.getString(R.string.delete_message_dialog_title));
                alertBuilder.setMessage(_activity.getString(R.string.delete_message_dialog_message));
                alertBuilder.setPositiveButton(_activity.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(messageId);
                        dialog.dismiss();
                    }
                });
                alertBuilder.setNegativeButton(_activity.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertBuilder.create().show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteMessage(long messageId) {
        Uri uri = Uri.withAppendedPath(Message.CONTENT_URI, String.valueOf(messageId));
        _activity.getContentResolver().delete(uri, null, null);
    }

    /**
     * Saves a message to the message list fragment.
     *
     * @param label    - MSG, GPS, START FILE, ON-LINE, OFF-LINE, ARRIVE, DEPART...
     * @param formName - CANNED, OUTBOUND LOAD, DVIR TOWED, DVIR POWER
     */
    private void saveMessage(String label, String formName) {

        Date currentDateTime = new Date();

        ContentValues values = new ContentValues();
        values.put(Message.Columns.DRIVER_NO, Utils.getDriverNo(_activity));
        values.put(Message.Columns.MESSAGE_TYPE, "User");
        values.put(Message.Columns.MESSAGE_TEXT, label.equals("MSG") ? _messageEditText.getText().toString() : label + " " + _messageEditText.getText().toString());
        values.put(Message.Columns.CREATED_DATE_TIME, Constants.ClientDateFormat.format(currentDateTime));

        if (_orderId > 0) {
            values.put(Message.Columns.ORDER_ID, _orderId);
            values.put(Message.Columns.FILE_NO, _fileNo);
        } else {
            values.putNull(Message.Columns.ORDER_ID);
            values.putNull(Message.Columns.FILE_NO);
        }

        _activity.getContentResolver().insert(Message.CONTENT_URI, values);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(_activity));
            jsonObject.accumulate(WebServiceConstants.FIELD_IN_OUT_FLAG, "I");
            jsonObject.accumulate(WebServiceConstants.FIELD_LABEL, label);

            if (!TextUtils.isEmpty(formName)) {
                jsonObject.accumulate(WebServiceConstants.FIELD_FORM_NAME, formName);
            }

            jsonObject.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, _messageEditText.getText().toString());
            jsonObject.accumulate(WebServiceConstants.FIELD_CLIENT_DATETIME, Utils.getCurrentDateTime(Constants.ServerDateFormat));

            Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_MESSAGE, jsonObject);
        } catch (JSONException e) {

        }

        _messageEditText.setText("");

        try {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
    }

    // TODO: If user is not signed in, do not allow canned messages
    private void showCannedMessageDialog() {

        String[] projection = {Form.Columns._ID,
                Form.Columns.LABEL,
                Form.Columns.FORM_NAME,
                Form.Columns.FILL_IN,
                Form.Columns.FORM_TYPE};
        String selection = Form.Columns.FORM_NAME + " = 'CANNED'";
        String sortOrder = Form.Columns._ID;

        final Cursor cursor = _activity.getContentResolver().query(Form.CONTENT_URI, projection, selection, null, sortOrder);
        cursor.moveToFirst();

        new AlertDialog.Builder(_activity)
                .setTitle(_activity.getString(R.string.canned_message_dialog_title))
                .setNegativeButton(_activity.getString(R.string.canned_message_dialog_cancel_button), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })

                .setCursor(cursor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cursor.moveToPosition(which);

                        if (cursor.getInt(cursor.getColumnIndex(Form.Columns.FILL_IN)) > 0) {
                            showCannedMessageEntryDialog(cursor.getString(cursor.getColumnIndex(Form.Columns.LABEL)), cursor.getInt(cursor.getColumnIndex(Form.Columns.FILL_IN)), cursor.getString(cursor.getColumnIndex(Form.Columns.FORM_TYPE)));
                        } else {
                            saveMessage(cursor.getString(cursor.getColumnIndex(Form.Columns.LABEL)), cursor.getString(cursor.getColumnIndex(Form.Columns.FORM_NAME)));
                        }

                    }
                }, Form.Columns.LABEL)
                .create().show();
    }

    private void showCannedMessageEntryDialog(String messageLabel, int fillIn, String formType) {

        final String label = messageLabel;
        final EditText messageEditText = new EditText(_activity);
        final InputFilter[] inputFilter = new InputFilter[1];

        inputFilter[0] = new InputFilter.LengthFilter(fillIn);

        messageEditText.setHint("Enter value");
        messageEditText.setFilters(inputFilter);

        if (formType.equals("N")) {
            messageEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (formType.equals("A")) {
            messageEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        } else if (formType.equals("AN")) {
            messageEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        } else {
            messageEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }

        new AlertDialog.Builder(_activity)
                .setTitle(messageLabel)
                .setView(messageEditText)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _messageEditText.setText(messageEditText.getText().toString());
                        saveMessage(label, "CANNED");
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create().show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Uri uri = Message.CONTENT_URI;
        String[] projection = {Message.Columns._ID,
                Message.Columns.DRIVER_NO,
                Message.Columns.FILE_NO,
                Message.Columns.MESSAGE_TYPE,
                Message.Columns.MESSAGE_TEXT,
                Message.Columns.CREATED_DATE_TIME};
        String sortOrder = Message.Columns.CREATED_DATE_TIME + " desc";
        String selection = null;

        if (_orderId > 0) {
            selection = Message.Columns.ORDER_ID + " = " + _orderId;
        } else {

            selection = Message.Columns.ORDER_ID + " isnull";
        }

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -7);

        // AND [CREATED DATETIME] > '[
        selection += " AND " + Message.Columns.CREATED_DATE_TIME + " > '" + Constants.ClientDateFormat.format(c.getTime()).toString() + "'";

        selection += " AND " + Message.Columns.DRIVER_NO + " = " + Utils.getDriverNo(_activity);

        return new CursorLoader(_activity, uri, projection, selection, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        _listAdapter.swapCursor(cursor);

        if (!cursor.isAfterLast()) {
            if (cursor.isNull(cursor.getColumnIndex(Message.Columns.FILE_NO))) {
                _fileNo = cursor.getInt(cursor.getColumnIndex(Message.Columns.FILE_NO));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        _listAdapter.swapCursor(null);
    }

    private void showPurgeMessageDialog() {

        final CharSequence[] choiceItems = new CharSequence[]{"Selected Messages", "All Sent Messages"};

        new AlertDialog.Builder(_activity)
                .setTitle("Which messages do you want to delete?")
                .setSingleChoiceItems(choiceItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                deleteSelectedMessages();
                                break;
                            case 1:
                                deleteAllSentMessages();
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @SuppressWarnings("unchecked")
    private void deleteSelectedMessages() {

        ArrayList<Long> selectedMessageIds = _listAdapter.getSelectedMessageIds();

        DeleteMessageAsyncTask task = new DeleteMessageAsyncTask(_activity);
        task.execute(selectedMessageIds);

        _listAdapter.setSelectedMessageIdsNew();
    }

    private void deleteAllSentMessages() {

        Cursor cursor = _listAdapter.getCursor();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            final String messageType = cursor.getString(cursor.getColumnIndex(Message.Columns.MESSAGE_TYPE));

            if (messageType.equals("User")) {
                deleteMessage(cursor.getLong(cursor.getColumnIndex(Message.Columns._ID)));
            }

            cursor.moveToNext();
        }
    }

    public interface Callbacks {
        public void onCancelButtonClick();
    }
}
