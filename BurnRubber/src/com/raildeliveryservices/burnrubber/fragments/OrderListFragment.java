package com.raildeliveryservices.burnrubber.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.LegActivity;
import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.adapters.OrderListCursorAdapter;
import com.raildeliveryservices.burnrubber.data.Order;
import com.raildeliveryservices.burnrubber.tasks.DeleteOrderAsyncTask;

@SuppressLint("NewApi")
public class OrderListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ORDERS = -11;
    private static final int LOADER_HISTORY_ORDERS = -2;

    private Activity _activity;
    private OrderListCursorAdapter _listAdapter;
    private boolean _tripHistory;

    public OrderListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _activity = getActivity();

        Bundle bundle = getArguments();
        _tripHistory = bundle.getBoolean(Constants.BUNDLE_PARAM_TRIP_HISTORY);

        _listAdapter = new OrderListCursorAdapter(_activity, R.layout.order_list_row, _tripHistory);
        setListAdapter(_listAdapter);

        Loader<Cursor> loader;
        if (!_tripHistory) {
            loader = getLoaderManager().getLoader(LOADER_ORDERS);

            if (loader != null && !loader.isReset()) {
                getLoaderManager().restartLoader(LOADER_ORDERS, null, this);
            } else {
                getLoaderManager().initLoader(LOADER_ORDERS, null, this);
            }
        } else {
            registerForContextMenu(getListView());
            loader = getLoaderManager().getLoader(LOADER_HISTORY_ORDERS);

            if (loader != null && !loader.isReset()) {
                getLoaderManager().restartLoader(LOADER_HISTORY_ORDERS, null, this);
            } else {
                getLoaderManager().initLoader(LOADER_HISTORY_ORDERS, null, this);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_list_fragment, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor cursor = _listAdapter.getCursor();
        cursor.moveToPosition(position);

        //Get information of selected order
        Order selectedOrder = getOrderFromCursor(cursor);

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_PARAM_SELECTED_ORDER, selectedOrder);
        bundle.putBoolean(Constants.BUNDLE_PARAM_TRIP_HISTORY, _tripHistory);

        Intent intent = new Intent(_activity, LegActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    private Order getOrderFromCursor(Cursor cursor) {
        Order order = new Order();
        order.setId(cursor.getInt(cursor.getColumnIndex(Order.Columns._ID)));
        order.setFileNo(cursor.getInt(cursor.getColumnIndex(Order.Columns.FILE_NO)));
        order.setDriverNo(cursor.getInt(cursor.getColumnIndex(Order.Columns.DRIVER_NO)));
        order.setParentFileNo(cursor.getInt(cursor.getColumnIndex(Order.Columns.PARENT_FILE_NO)));
        order.setVoyageNo(cursor.getString(cursor.getColumnIndex(Order.Columns.VOYAGE_NO)));
        order.setTripNo(cursor.getString(cursor.getColumnIndex(Order.Columns.TRIP_NO)));
        order.setPoNo(cursor.getString(cursor.getColumnIndex(Order.Columns.PO_NO)));
        order.setPickUpNo(cursor.getString(cursor.getColumnIndex(Order.Columns.PICKUP_NO)));
        order.setRailNo(cursor.getString(cursor.getColumnIndex(Order.Columns.RAIL_NO)));
        order.setManifestNo(cursor.getString(cursor.getColumnIndex(Order.Columns.MANIFEST_NO)));
        order.setBookingNo(cursor.getString(cursor.getColumnIndex(Order.Columns.BOOKING_NO)));
        order.setAppointmentDate(cursor.getString(cursor.getColumnIndex(Order.Columns.APPT_DATE_TIME)));
        order.setAppointmentTime(cursor.getString(cursor.getColumnIndex(Order.Columns.APPT_TIME)));
        order.setMoveType(cursor.getString(cursor.getColumnIndex(Order.Columns.MOVE_TYPE)));
        order.setContainerNo(cursor.getString(cursor.getColumnIndex(Order.Columns.CONTAINER_NO)));
        order.setChassisNo(cursor.getString(cursor.getColumnIndex(Order.Columns.CHASSIS_NO)));
        order.setHazmatFlag(cursor.getInt(cursor.getColumnIndex(Order.Columns.HAZMAT_FLAG)));
        order.setComment(cursor.getString(cursor.getColumnIndex(Order.Columns.COMMENTS)));
        order.setLumperFlag(cursor.getInt(cursor.getColumnIndex(Order.Columns.LUMPER_FLAG)));
        order.setScaleFlag(cursor.getInt(cursor.getColumnIndex(Order.Columns.SCALE_FLAG)));
        order.setWeightFlag(cursor.getInt(cursor.getColumnIndex(Order.Columns.WEIGHT_FLAG)));
        order.setConfirmFlag(cursor.getInt(cursor.getColumnIndex(Order.Columns.CONFIRMED_FLAG)));
        order.setStartFlag(cursor.getInt(cursor.getColumnIndex(Order.Columns.STARTED_FLAG)));
        order.setCompletedFlag(cursor.getInt(cursor.getColumnIndex(Order.Columns.COMPLETED_FLAG)));

        return order;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle(_activity.getString(R.string.order_options_menu_title));
        MenuInflater inflater = new MenuInflater(_activity);
        inflater.inflate(R.menu.order_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(this.getClass().getSimpleName(), "Order  - onContextItemSelected - Hisotry Order: " + _tripHistory);
        if (!getUserVisibleHint()) {
            return false;
        }

        switch (item.getItemId()) {
            case R.id.delete_order:
                AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                Cursor cursor = _listAdapter.getCursor();

                int orderPosition = adapterContextMenuInfo.position;
                if (cursor != null) {
                    cursor.moveToPosition(orderPosition);
                }
                final long orderId = cursor.getLong(cursor.getColumnIndex(Order.Columns._ID));
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(_activity);
                alertBuilder.setTitle(_activity.getString(R.string.delete_order_dialog_title));
                alertBuilder.setMessage(_activity.getString(R.string.delete_order_dialog_message));
                alertBuilder.setPositiveButton(_activity.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteOrder(orderId);
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

    private void deleteOrder(long orderId) {
        DeleteOrderAsyncTask deleteOrder = new DeleteOrderAsyncTask(_activity);
        deleteOrder.execute(new Long[]{orderId});
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        Uri uri = null;
        String selection = null;
        String sortOrder = null;
        String[] projection = null;

        if (loaderId == LOADER_ORDERS || loaderId == LOADER_HISTORY_ORDERS) {
            uri = Order.CONTENT_URI;
            projection = new String[]{
                    Order.Columns._ID,
                    Order.Columns.FILE_NO,
                    Order.Columns.DRIVER_NO,
                    Order.Columns.PARENT_FILE_NO,
                    Order.Columns.VOYAGE_NO,
                    Order.Columns.TRIP_NO,
                    Order.Columns.PO_NO,
                    Order.Columns.PICKUP_NO,
                    Order.Columns.RAIL_NO,
                    Order.Columns.MANIFEST_NO,
                    Order.Columns.BOOKING_NO,
                    Order.Columns.HAZMAT_FLAG,
                    Order.Columns.APPT_DATE_TIME,
                    Order.Columns.APPT_TIME,
                    Order.Columns.MOVE_TYPE,
                    Order.Columns.CONTAINER_NO,
                    Order.Columns.CHASSIS_NO,
                    Order.Columns.COMMENTS,
                    Order.Columns.LUMPER_FLAG,
                    Order.Columns.SCALE_FLAG,
                    Order.Columns.WEIGHT_FLAG,
                    Order.Columns.CONFIRMED_FLAG,
                    Order.Columns.STARTED_FLAG,
                    Order.Columns.COMPLETED_FLAG};
        }

        if (loaderId == LOADER_ORDERS) {
            selection = Order.Columns.COMPLETED_FLAG + " != 1";
            sortOrder = Order.Columns.APPT_DATE_TIME;
        } else if (loaderId == LOADER_HISTORY_ORDERS) {
            selection = Order.Columns.COMPLETED_FLAG + " = 1";
            sortOrder = Order.Columns.APPT_DATE_TIME + " DESC";
        }

        return new CursorLoader(_activity, uri, projection, selection, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        _listAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        _listAdapter.swapCursor(null);
    }
}
