package com.raildeliveryservices.burnrubber.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorTreeAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.data.Leg;
import com.raildeliveryservices.burnrubber.data.LegExtra;
import com.raildeliveryservices.burnrubber.utils.Utils;

import java.text.ParseException;
import java.util.HashMap;

@SuppressLint("UseSparseArrays")
public class LegListCursorAdapter extends CursorTreeAdapter {

    // Allow editing after Arrive/Depart/Done button gets pressed
    private boolean allowTimeEditing = false;

    private Context _context;
    private boolean _readOnly;
    private boolean _startedFlag;
    private LayoutInflater _layoutInflater;
    private HashMap<Integer, Boolean> _completed = new HashMap<Integer, Boolean>();
    private AdapterCallbacks _adapterCallbacks;

    public LegListCursorAdapter(Context context, boolean readOnly) {
        super(null, context);
        _context = context;
        _readOnly = readOnly;
        _layoutInflater = LayoutInflater.from(context);
    }

    public void setButtonListener(AdapterCallbacks listener) {
        _adapterCallbacks = listener;
    }

    public void setStartedFlag(boolean value) {
        _startedFlag = value;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {

        final long legId = cursor.getLong(cursor.getColumnIndex(Leg.Columns._ID));
        final int orderId = cursor.getInt(cursor.getColumnIndex(Leg.Columns.ORDER_ID));
        final int legNo = cursor.getInt(cursor.getColumnIndex(Leg.Columns.LEG_NO));
        final int parentLegNo = cursor.getInt(cursor.getColumnIndex(Leg.Columns.PARENT_LEG_NO));
        final TextView companyNameFromText = (TextView) view.findViewById(R.id.companyNameFromText);
        final TextView addressFromText = (TextView) view.findViewById(R.id.addressFromText);
        final TextView cityFromText = (TextView) view.findViewById(R.id.cityFromText);
        final TextView stateFromText = (TextView) view.findViewById(R.id.stateFromText);
        final TextView zipcodeFromText = (TextView) view.findViewById(R.id.zipcodeFromText);
        final TextView companyNameToText = (TextView) view.findViewById(R.id.companyNameToText);
        final TextView addressToText = (TextView) view.findViewById(R.id.addressToText);
        final TextView cityToText = (TextView) view.findViewById(R.id.cityToText);
        final TextView stateToText = (TextView) view.findViewById(R.id.stateToText);
        final TextView zipcodeToText = (TextView) view.findViewById(R.id.zipcodeToText);
        final TextView countFlagText = (TextView) view.findViewById(R.id.countFlagText);
        final TextView weightFlagText = (TextView) view.findViewById(R.id.weightFlagText);
        final boolean countFlag = cursor.getInt(cursor.getColumnIndex(Leg.Columns.COUNT_FLAG)) == 1;
        final boolean weightFlag = cursor.getInt(cursor.getColumnIndex(Leg.Columns.WEIGHT_FLAG)) == 1;
        final boolean outboundFlag = cursor.getInt(cursor.getColumnIndex(Leg.Columns.OUTBOUND_FLAG)) == 1;
        final Button arriveFromButton = (Button) view.findViewById(R.id.arriveFromButton);
        final Button departFromButton = (Button) view.findViewById(R.id.departFromButton);
        final Button arriveToButton = (Button) view.findViewById(R.id.arriveToButton);
        final Button departToButton = (Button) view.findViewById(R.id.departToButton);
        final Button endFileButton = (Button) view.findViewById(R.id.endFileButton);
        final Button outboundFormButton = (Button) view.findViewById(R.id.outboundFormButton);
        String arriveFromDate = "";
        String departFromDate = "";
        String arriveToDate = "";
        String departToDate = "";

        arriveFromButton.setTag(legId);
        departFromButton.setTag(legId);
        arriveToButton.setTag(legId);
        departToButton.setTag(legId);
        arriveFromButton.setText(_context.getString(R.string.arrive_button_text));
        arriveToButton.setText(_context.getString(R.string.arrive_button_text));
        departFromButton.setText(_context.getString(R.string.depart_button_text));
        departToButton.setText(_context.getString(R.string.depart_button_text));
        endFileButton.setText(_context.getString(R.string.endFile_button_text));

        if (parentLegNo > 0) {
            Cursor parentLegCursor = getParentLeg(orderId, parentLegNo);

            companyNameFromText.setText(parentLegCursor.getString(parentLegCursor.getColumnIndex(Leg.Columns.COMPANY_NAME_TO)));
            addressFromText.setText(parentLegCursor.getString(parentLegCursor.getColumnIndex(Leg.Columns.ADDRESS_TO)));
            cityFromText.setText(parentLegCursor.getString(parentLegCursor.getColumnIndex(Leg.Columns.CITY_TO)) + ", ");
            stateFromText.setText(parentLegCursor.getString(parentLegCursor.getColumnIndex(Leg.Columns.STATE_TO)) + " ");
            zipcodeFromText.setText(parentLegCursor.getString(parentLegCursor.getColumnIndex(Leg.Columns.ZIP_CODE_TO)));

            try {
                if (!TextUtils.isEmpty(parentLegCursor.getString(parentLegCursor.getColumnIndex(Leg.Columns.ARRIVE_TO_DATE_TIME)))) {
                    arriveFromDate = Constants.ClientDateFormat.format(Constants.ClientDateFormat.parse(parentLegCursor.getString(parentLegCursor.getColumnIndex(Leg.Columns.ARRIVE_TO_DATE_TIME))));
                }
            } catch (ParseException e) {
                arriveFromDate = "";
            }

            try {
                if (!TextUtils.isEmpty(parentLegCursor.getString(parentLegCursor.getColumnIndex(Leg.Columns.DEPART_TO_DATE_TIME)))) {
                    departFromDate = Constants.ClientDateFormat.format(Constants.ClientDateFormat.parse(parentLegCursor.getString(parentLegCursor.getColumnIndex(Leg.Columns.DEPART_TO_DATE_TIME))));
                }
            } catch (ParseException e) {
                departFromDate = "";
            }

            parentLegCursor.close();
        } else {
            companyNameFromText.setText(cursor.getString(cursor.getColumnIndex(Leg.Columns.COMPANY_NAME_FROM)));
            addressFromText.setText(cursor.getString(cursor.getColumnIndex(Leg.Columns.ADDRESS_FROM)));
            cityFromText.setText(cursor.getString(cursor.getColumnIndex(Leg.Columns.CITY_FROM)) + ", ");
            stateFromText.setText(cursor.getString(cursor.getColumnIndex(Leg.Columns.STATE_FROM)) + " ");
            zipcodeFromText.setText(cursor.getString(cursor.getColumnIndex(Leg.Columns.ZIP_CODE_FROM)));

            try {
                if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(Leg.Columns.ARRIVE_FROM_DATE_TIME)))) {
                    arriveFromDate = Constants.ClientDateFormat.format(Constants.ClientDateFormat.parse(cursor.getString(cursor.getColumnIndex(Leg.Columns.ARRIVE_FROM_DATE_TIME))));
                }
            } catch (ParseException e) {
            }

            try {
                if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(Leg.Columns.DEPART_FROM_DATE_TIME)))) {
                    departFromDate = Constants.ClientDateFormat.format(Constants.ClientDateFormat.parse(cursor.getString(cursor.getColumnIndex(Leg.Columns.DEPART_FROM_DATE_TIME))));
                }
            } catch (ParseException e) {
            }
        }

        companyNameToText.setText(cursor.getString(cursor.getColumnIndex(Leg.Columns.COMPANY_NAME_TO)));
        addressToText.setText(cursor.getString(cursor.getColumnIndex(Leg.Columns.ADDRESS_TO)));
        cityToText.setText(cursor.getString(cursor.getColumnIndex(Leg.Columns.CITY_TO)) + ", ");
        stateToText.setText(cursor.getString(cursor.getColumnIndex(Leg.Columns.STATE_TO)) + " ");
        zipcodeToText.setText(cursor.getString(cursor.getColumnIndex(Leg.Columns.ZIP_CODE_TO)));

        try {
            if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(Leg.Columns.ARRIVE_TO_DATE_TIME)))) {
                arriveToDate = Constants.ClientDateFormat.format(Constants.ClientDateFormat.parse(cursor.getString(cursor.getColumnIndex(Leg.Columns.ARRIVE_TO_DATE_TIME))));
            }
        } catch (ParseException e) {
        }

        try {
            if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(Leg.Columns.DEPART_TO_DATE_TIME)))) {
                departToDate = Constants.ClientDateFormat.format(Constants.ClientDateFormat.parse(cursor.getString(cursor.getColumnIndex(Leg.Columns.DEPART_TO_DATE_TIME))));
            }
        } catch (ParseException e) {
        }

        if (countFlag) {
            countFlagText.setVisibility(View.VISIBLE);
        } else {
            countFlagText.setVisibility(View.GONE);
        }

        if (weightFlag) {
            weightFlagText.setVisibility(View.VISIBLE);
        } else {
            weightFlagText.setVisibility(View.GONE);
        }

        if (isLastChild) {
            final Cursor fromExtrasCursor = getExtrasCursor(legId, "from");
            final LinearLayout legFromExtrasLayout = (LinearLayout) view.findViewById(R.id.legFromExtrasLayout);
            legFromExtrasLayout.removeAllViews();

            if (fromExtrasCursor.getCount() > 0) {
                do {
                    TextView tv = new TextView(_context);
                    tv.setText(fromExtrasCursor.getString(fromExtrasCursor.getColumnIndex(LegExtra.Columns.EXTRA)));
                    legFromExtrasLayout.addView(tv);
                } while (fromExtrasCursor.moveToNext());
            }

            final Cursor toExtrasCursor = getExtrasCursor(legId, "to");
            final LinearLayout legToExtrasLayout = (LinearLayout) view.findViewById(R.id.legToExtrasLayout);
            legToExtrasLayout.removeAllViews();

            if (toExtrasCursor.getCount() > 0) {
                do {
                    TextView tv = new TextView(_context);
                    tv.setText(toExtrasCursor.getString(toExtrasCursor.getColumnIndex(LegExtra.Columns.EXTRA)));
                    legToExtrasLayout.addView(tv);
                } while (toExtrasCursor.moveToNext());
            }
        }

        boolean previousCompleted = true;
        if (_completed.containsKey(legNo - 1)) {
            if (_completed.get(legNo - 1)) {
                previousCompleted = true;
            } else {
                previousCompleted = false;
            }
        }

        if (!_readOnly) {
            if (TextUtils.isEmpty(arriveFromDate)) {
                arriveFromButton.setVisibility(View.VISIBLE);

                if (previousCompleted && Utils.isUserOnline(_context) && _startedFlag) {
                    arriveFromButton.setEnabled(true);

                    arriveFromButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _adapterCallbacks.onArriveDepartButtonClick(v, legId);
                        }
                    });
                } else {
                    arriveFromButton.setEnabled(false);
                }
            } else {
                arriveFromButton.setText(arriveFromDate);

                if (parentLegNo <= 0 && allowTimeEditing) {
                    arriveFromButton.setEnabled(true);
                    arriveFromButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _adapterCallbacks.onEditArriveDepartButtonClick(v, legId);
                        }
                    });
                } else {
                    arriveFromButton.setEnabled(false);
                }
            }

            if (TextUtils.isEmpty(departFromDate)) {
                departFromButton.setVisibility(View.VISIBLE);

                if (previousCompleted && Utils.isUserOnline(_context) && _startedFlag && !TextUtils.isEmpty(arriveFromDate)) {
                    departFromButton.setEnabled(true);

                    departFromButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _adapterCallbacks.onArriveDepartButtonClick(v, legId);
                        }
                    });
                } else {
                    departFromButton.setEnabled(false);
                }
            } else {
                departFromButton.setText(departFromDate);

                if (parentLegNo <= 0 && allowTimeEditing) {
                    departFromButton.setEnabled(true);
                    departFromButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _adapterCallbacks.onEditArriveDepartButtonClick(v, legId);
                        }
                    });
                } else {
                    departFromButton.setEnabled(false);
                }
            }

            if (TextUtils.isEmpty(arriveToDate)) {
                arriveToButton.setVisibility(View.VISIBLE);

                if (previousCompleted && Utils.isUserOnline(_context) && _startedFlag && !TextUtils.isEmpty(departFromDate)) {
                    arriveToButton.setEnabled(true);

                    arriveToButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _adapterCallbacks.onArriveDepartButtonClick(v, legId);
                        }
                    });
                } else {
                    arriveToButton.setEnabled(false);
                }
            } else {

                arriveToButton.setText(arriveToDate);

                if (allowTimeEditing) {
                    arriveToButton.setEnabled(true);
                    arriveToButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _adapterCallbacks.onEditArriveDepartButtonClick(v, legId);
                        }
                    });
                } else {
                    arriveToButton.setEnabled(false);
                }
            }

            if (TextUtils.isEmpty(departToDate)) {

                if (isLastLeg(orderId, legNo)) {
                    departToButton.setVisibility(View.GONE);
                    endFileButton.setVisibility(View.VISIBLE);
                } else {
                    endFileButton.setVisibility(View.GONE);
                    departToButton.setVisibility(View.VISIBLE);
                }
                if (previousCompleted && Utils.isUserOnline(_context) && _startedFlag && !TextUtils.isEmpty(arriveToDate)) {
                    if (isLastLeg(orderId, legNo)) {
                        endFileButton.setEnabled(true);
                        departToButton.setEnabled(false);
                    } else {
                        departToButton.setEnabled(true);
                        endFileButton.setEnabled(false);
                    }

                    endFileButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _adapterCallbacks.onArriveDepartButtonClick(v, legId);
                        }
                    });
                    departToButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _adapterCallbacks.onArriveDepartButtonClick(v, legId);
                        }
                    });

                } else {
                    departToButton.setEnabled(false);
                    endFileButton.setEnabled(false);
                }
            } else {
                endFileButton.setText(departToDate);
                departToButton.setText(departToDate);

                if (allowTimeEditing) {
                    if (isLastLeg(orderId, legNo)) {
                        endFileButton.setEnabled(true);
                        departToButton.setEnabled(false);
                        endFileButton.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                _adapterCallbacks.onEditArriveDepartButtonClick(v, legId);
                            }
                        });
                    } else {
                        departToButton.setEnabled(true);
                        endFileButton.setEnabled(false);
                        departToButton.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                _adapterCallbacks.onEditArriveDepartButtonClick(v, legId);
                            }
                        });
                    }
                } else {
                    departToButton.setEnabled(false);
                    endFileButton.setEnabled(false);
                }
            }

            if (outboundFlag) {
                outboundFormButton.setVisibility(View.VISIBLE);

                if (Utils.isUserOnline(_context) && _startedFlag) {
                    outboundFormButton.setEnabled(true);

                    outboundFormButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _adapterCallbacks.onOutboundFormClick(legId, orderId);
                        }
                    });
                } else {
                    outboundFormButton.setEnabled(false);
                }
            } else {
                outboundFormButton.setVisibility(View.GONE);
            }
        } else {
            arriveFromButton.setVisibility(View.GONE);
            departFromButton.setVisibility(View.GONE);
            arriveToButton.setVisibility(View.GONE);
            departToButton.setVisibility(View.GONE);
            endFileButton.setVisibility(View.GONE);
            outboundFormButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {

        final int legNo = cursor.getInt(cursor.getColumnIndex(Leg.Columns.LEG_NO));
        boolean completeFlag = cursor.getInt(cursor.getColumnIndex(Leg.Columns.COMPLETED_FLAG)) == 1 ? true : false;
        final TextView legNoText = (TextView) view.findViewById(R.id.legNoText);
        final TextView legCompleteText = (TextView) view.findViewById(R.id.legCompleteText);
        legNoText.setText(String.valueOf(legNo));

        if (completeFlag) {
            legCompleteText.setVisibility(View.VISIBLE);
        } else {
            legCompleteText.setVisibility(View.GONE);
        }

        if (_completed.containsKey(legNo)) {
            _completed.remove(legNo);
        }
        _completed.put(legNo, completeFlag);
    }

    private boolean isLastLeg(int orderId, int legNo) {
        Uri uri = Leg.CONTENT_URI;
        String[] projection = {Leg.Columns.LEG_NO};
        String selection = Leg.Columns.ORDER_ID + " = " + orderId;
        String sortOrder = Leg.Columns.LEG_NO;

        Cursor cursor = _context.getContentResolver().query(uri, projection, selection, null, sortOrder);
        cursor.moveToLast();

        if (cursor.getInt(cursor.getColumnIndex(Leg.Columns.LEG_NO)) == legNo) {
            return true;
        } else {
            return false;
        }
    }

    private Cursor getParentLeg(int orderId, int parentLegNo) {
        Uri uri = Leg.CONTENT_URI;
        String[] projection = {Leg.Columns.COMPANY_NAME_TO,
                Leg.Columns.ADDRESS_TO,
                Leg.Columns.CITY_TO,
                Leg.Columns.STATE_TO,
                Leg.Columns.ZIP_CODE_TO,
                Leg.Columns.ARRIVE_TO_DATE_TIME,
                Leg.Columns.DEPART_TO_DATE_TIME};
        String selection = Leg.Columns.ORDER_ID + " = " + orderId + " and " + Leg.Columns.LEG_NO + " = " + parentLegNo;

        Cursor cursor = _context.getContentResolver().query(uri, projection, selection, null, null);
        cursor.moveToFirst();

        return cursor;
    }

    private Cursor getExtrasCursor(long legId, String legPart) {

        Uri uri = LegExtra.CONTENT_URI;
        String[] projection = {LegExtra.Columns.EXTRA};
        String selection = LegExtra.Columns.LEG_ID + " = " + legId + " and " + LegExtra.Columns.LEG_PART + " = '" + legPart + "'";

        Cursor cursor = _context.getContentResolver().query(uri, projection, selection, null, null);
        cursor.moveToFirst();

        return cursor;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        Cursor itemCursor = getGroup(groupCursor.getPosition());
        long legId = itemCursor.getLong(itemCursor.getColumnIndex(Leg.Columns._ID));
        Uri uri = Uri.withAppendedPath(Leg.CONTENT_URI, String.valueOf(legId));
        String[] projection = {Leg.Columns._ID,
                Leg.Columns.ORDER_ID,
                Leg.Columns.FILE_NO,
                Leg.Columns.LEG_NO,
                Leg.Columns.PARENT_LEG_NO,
                Leg.Columns.COMPANY_NAME_FROM,
                Leg.Columns.ADDRESS_FROM,
                Leg.Columns.CITY_FROM,
                Leg.Columns.STATE_FROM,
                Leg.Columns.ZIP_CODE_FROM,
                Leg.Columns.ARRIVE_FROM_DATE_TIME,
                Leg.Columns.DEPART_FROM_DATE_TIME,
                Leg.Columns.COMPANY_NAME_TO,
                Leg.Columns.ADDRESS_TO,
                Leg.Columns.CITY_TO,
                Leg.Columns.STATE_TO,
                Leg.Columns.ZIP_CODE_TO,
                Leg.Columns.ARRIVE_TO_DATE_TIME,
                Leg.Columns.DEPART_TO_DATE_TIME,
                Leg.Columns.COUNT_FLAG,
                Leg.Columns.WEIGHT_FLAG,
                Leg.Columns.OUTBOUND_FLAG,
                Leg.Columns.COMPLETED_FLAG};

        Cursor childCursor = _context.getContentResolver().query(uri, projection, null, null, null);
        childCursor.moveToFirst();

        return childCursor;
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        return _layoutInflater.inflate(R.layout.leg_list_row, parent, false);
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        return _layoutInflater.inflate(R.layout.leg_list_group, parent, false);
    }

    public interface AdapterCallbacks {
        public void onArriveDepartButtonClick(View v, long legId);

        public void onOutboundFormClick(long legId, int fileNo);

        public void onEditArriveDepartButtonClick(View v, long legId);
    }
}
