package com.raildeliveryservices.burnrubber.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.raildeliveryservices.burnrubber.Constants;
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
    private Callbacks _callbacks;

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

        try {
            _callbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement Callbacks");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor cursor = _listAdapter.getCursor();
        cursor.moveToPosition(position);

        boolean confirmedFlag = cursor.getInt(cursor.getColumnIndex(Order.Columns.CONFIRMED_FLAG)) == 1;
        boolean completedFlag = cursor.getInt(cursor.getColumnIndex(Order.Columns.COMPLETED_FLAG)) == 1;
        boolean readOnly = !confirmedFlag || completedFlag;

        _callbacks.onOrderListItemClick(id, readOnly);
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

        Cursor cursor = _listAdapter.getCursor();
        final long orderId = cursor.getLong(cursor.getColumnIndex(Order.Columns._ID));

        switch (item.getItemId()) {
            case R.id.delete_order:
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
            projection = new String[]{Order.Columns._ID,
                    Order.Columns.FILE_NO,
                    Order.Columns.VOYAGE_NO,
                    Order.Columns.HAZMAT_FLAG,
                    Order.Columns.APPT_DATE_TIME,
                    Order.Columns.APPT_TIME,
                    Order.Columns.MOVE_TYPE,
                    Order.Columns.CONFIRMED_FLAG,
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

    public interface Callbacks {
        public void onOrderListItemClick(long orderId, boolean readOnly);
    }
}
