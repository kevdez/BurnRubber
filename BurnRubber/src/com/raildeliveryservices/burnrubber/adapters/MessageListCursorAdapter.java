package com.raildeliveryservices.burnrubber.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.data.Message;
import com.raildeliveryservices.burnrubber.utils.Utils;

import java.util.ArrayList;

public class MessageListCursorAdapter extends SimpleCursorAdapter {

    private static ArrayList<Long> _selectedMessageIds;
    private Context _context;

    public MessageListCursorAdapter(Context context, int layout, int flags) {
        super(context, layout, null, new String[]{Message.Columns.MESSAGE_TEXT}, new int[]{R.id.messageText}, flags);
        _context = context;
        _selectedMessageIds = new ArrayList<Long>();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final CheckBox messageCheckBox = (CheckBox) view.findViewById(R.id.messageCheckbox);
        final TextView messageDateTimeText = (TextView) view.findViewById(R.id.messageDateTimeText);
        final TextView messageText = (TextView) view.findViewById(R.id.messageText);
        final long messageId = cursor.getLong(cursor.getColumnIndex(Message.Columns._ID));

        String msgDateTime = cursor.getString(cursor.getColumnIndex(Message.Columns.CREATED_DATE_TIME));
        messageDateTimeText.setText(Utils.formatDateTime(msgDateTime, Constants.ClientDateFormat));

        messageText.setText(cursor.getString(cursor.getColumnIndex(Message.Columns.MESSAGE_TEXT)));

        if (cursor.getString(cursor.getColumnIndex(Message.Columns.MESSAGE_TYPE)).equals("Dispatch")) {
            //messageCheckBox.setVisibility(View.INVISIBLE);
            messageDateTimeText.setTextColor(_context.getResources().getColor(R.color.green));
            messageText.setTextColor(_context.getResources().getColor(R.color.green));
        } else {
            //messageCheckBox.setVisibility(View.VISIBLE);
            messageDateTimeText.setTextColor(_context.getResources().getColor(R.color.white));
            messageText.setTextColor(_context.getResources().getColor(R.color.white));
        }

        if (_selectedMessageIds.contains(messageId)) {
            messageCheckBox.setChecked(true);
        } else {
            messageCheckBox.setChecked(false);
        }

        messageCheckBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (cb.isChecked()) {
                    _selectedMessageIds.add(messageId);
                } else {
                    _selectedMessageIds.remove(messageId);
                }
            }
        });
    }

    public ArrayList<Long> getSelectedMessageIds() {
        return _selectedMessageIds;
    }

    public void setSelectedMessageIdsNew() {
        _selectedMessageIds = new ArrayList<Long>();
    }
}
