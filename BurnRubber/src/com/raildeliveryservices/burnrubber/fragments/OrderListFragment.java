package com.raildeliveryservices.burnrubber.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.adapters.OrderListCursorAdapter;
import com.raildeliveryservices.burnrubber.data.Form;
import com.raildeliveryservices.burnrubber.data.MessageAlert;
import com.raildeliveryservices.burnrubber.data.Order;
import com.raildeliveryservices.burnrubber.tasks.DeleteOrderAsyncTask;
import com.raildeliveryservices.burnrubber.utils.Utils;

@SuppressLint("NewApi")
public class OrderListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final int LOADER_ORDERS = -11;
	private static final int LOADER_HISTORY_ORDERS = -2;
	private static final int LOADER_MESSAGE_ALERTS = -3;
	
	private Activity _activity;
	private Button _messageButton;
	private Button _tripHistoryButton;
	private Button _onlineButton;
	//private Button _formsButton;
	//private Button _logoffButton;
	//private Button _returnButton;
	private OrderListCursorAdapter _listAdapter;
	private boolean _tripHistory;
	private Callbacks _callbacks;
	
	public interface Callbacks {
		public void onMessageButtonClick();
		public void onTripHistoryButtonClick();
		public void onReturnButtonClick();
		public void onLogoffButtonClick();
		public void onOrderListItemClick(long orderId, boolean readOnly);
		public void onFormListItemClick(String formName);
	}
	
	public OrderListFragment() {
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		_activity = getActivity();
		
		//_settings = _activity.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);

		_messageButton = (Button) _activity.findViewById(R.id.messageButton);
		_tripHistoryButton = (Button) _activity.findViewById(R.id.tripHistoryButton);
		_onlineButton = (Button) _activity.findViewById(R.id.onlineButton);
		Button formsButton = (Button) _activity.findViewById(R.id.formsButton);
		Button logoffButton = (Button) _activity.findViewById(R.id.logoffButton);
		Button returnButton = (Button) _activity.findViewById(R.id.returnButton);
		
		_messageButton.setOnClickListener(_buttonListener);
		_tripHistoryButton.setOnClickListener(_buttonListener);
		_onlineButton.setOnClickListener(_buttonListener);
		formsButton.setOnClickListener(_buttonListener);
		logoffButton.setOnClickListener(_buttonListener);
		returnButton.setOnClickListener(_buttonListener);
		
		Bundle bundle = getArguments();
		_tripHistory = bundle.getBoolean(Constants.BUNDLE_PARAM_TRIP_HISTORY);
		
		if (_tripHistory) {
			_messageButton.setVisibility(View.GONE);
			_onlineButton.setVisibility(View.GONE);
			formsButton.setVisibility(View.GONE);
			logoffButton.setVisibility(View.GONE);
			_tripHistoryButton.setVisibility(View.GONE);
			returnButton.setVisibility(View.VISIBLE);
		} else {
			_messageButton.setVisibility(View.VISIBLE);
			_onlineButton.setVisibility(View.VISIBLE);
			formsButton.setVisibility(View.VISIBLE);
			logoffButton.setVisibility(View.VISIBLE);
			_tripHistoryButton.setVisibility(View.VISIBLE);
			returnButton.setVisibility(View.GONE);
		}
		
		if (Utils.isUserOnline(_activity)) {
			setUserButtonOnline(true, Utils.getDriverOnlineSystem(_activity));
		} else {
			setUserButtonOnline(false, null);
		}
		
		TextView driverNumberText = (TextView) _activity.findViewById(R.id.driverNumberText);
		driverNumberText.setText(Utils.getDriverNo(_activity));
		
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
		
		Loader<Cursor> messageAlertLoader = getLoaderManager().getLoader(LOADER_MESSAGE_ALERTS);
		if (messageAlertLoader != null && !messageAlertLoader.isReset()) {
			getLoaderManager().restartLoader(LOADER_MESSAGE_ALERTS, null, this);
		} else {
			getLoaderManager().initLoader(LOADER_MESSAGE_ALERTS, null, this);
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
		
		boolean confirmedFlag = cursor.getInt(cursor.getColumnIndex(Order.Columns.CONFIRMED_FLAG)) == 1 ? true : false;
		boolean readOnly = confirmedFlag ? false : true;
		
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
		deleteOrder.execute(new Long[] { orderId });
	}
	
	private void setUserButtonOnline(boolean value, String system) {
		
		if (value) {
			if (system.equals("Crossdock")) {
				_onlineButton.setText(getString(R.string.online_xdock));
			} else if (system.equals("Drayage")) {
				_onlineButton.setText(getString(R.string.online_drayage));
			} else {
				_onlineButton.setText(getString(R.string.online));
			}
			
			_onlineButton.setTextColor(getActivity().getResources().getColor(R.color.green));
		} else {
			_onlineButton.setText(getString(R.string.offline));
			_onlineButton.setTextColor(getActivity().getResources().getColor(R.color.red));
		}
	}
	
	private void setUserOnline(String system) {
		Utils.setUserOnline(_activity, true);
		Utils.setDriverOnlineSystem(_activity, system);
		setUserButtonOnline(true, system);
		Utils.sendUserOnlineToServer(_activity, true, system);
	}
	
	private void setUserOffline() {
		Utils.setUserOnline(_activity, false);
		Utils.setDriverOnlineSystem(_activity, "");
		setUserButtonOnline(false, null);
		Utils.sendUserOnlineToServer(_activity, false, null);
	}
	
	private OnClickListener _buttonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.messageButton:
					_callbacks.onMessageButtonClick();
					break;
				case R.id.tripHistoryButton:
					_callbacks.onTripHistoryButtonClick();
					break;
				case R.id.onlineButton:
					if (Utils.isUserOnline(_activity)) {
						setUserOffline();
					} else {				
						final CharSequence[] choiceItems = new CharSequence[] { "Drayage", "Crossdock" };
						
						AlertDialog.Builder alertBuilder = new AlertDialog.Builder(_activity);
						alertBuilder.setTitle("Select System");
						alertBuilder.setSingleChoiceItems(choiceItems, -1, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								setUserOnline(choiceItems[which].toString());
								dialog.dismiss();
							}
						});
						alertBuilder.create().show();
					}
					break;
				case R.id.formsButton:
					showFormDialog();
					break;
				case R.id.logoffButton:
					if (Utils.isUserOnline(_activity)) {
						setUserOffline();
					}
					_callbacks.onLogoffButtonClick();
					break;
				case R.id.returnButton:
					_callbacks.onReturnButtonClick();
					break;
			}
		}
	};
	
	private void showFormDialog() {
		
		String[] projection = { Form.Columns._ID,
								Form.Columns.LABEL,
								Form.Columns.FORM_NAME,
								Form.Columns.FILL_IN,
								Form.Columns.FORM_TYPE };
		String selection = Form.Columns.FORM_NAME + " != 'CANNED'";
		String sortOrder = Form.Columns.FORM_NAME + "," + Form.Columns._ID;
	
		final Cursor cursor = _activity.getContentResolver().query(Form.CONTENT_URI, projection, selection, null, sortOrder);
		cursor.moveToFirst();
		
		final MatrixCursor newCursor = new MatrixCursor(new String[] { Form.Columns._ID, Form.Columns.FORM_NAME });
		newCursor.moveToFirst();
		String formName = "";
		
		try {
			do {
				if (!cursor.getString(cursor.getColumnIndex(Form.Columns.FORM_NAME)).equals(formName)) {
					newCursor.addRow(new Object[] { cursor.getLong(cursor.getColumnIndex(Form.Columns._ID)), cursor.getString(cursor.getColumnIndex(Form.Columns.FORM_NAME)) });
				}
				
				formName = cursor.getString(cursor.getColumnIndex(Form.Columns.FORM_NAME));
			} while (cursor.moveToNext());
		} catch (Exception e) {
			
		}
		
		cursor.close();
		
		new AlertDialog.Builder(_activity)
			.setTitle("Select Form")
			.setCursor(newCursor, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					newCursor.moveToPosition(which);
					_callbacks.onFormListItemClick(newCursor.getString(newCursor.getColumnIndex(Form.Columns.FORM_NAME)));
				}
			}, Form.Columns.FORM_NAME)
			.create().show();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		
		Uri uri = null;
		String selection = null;
		String sortOrder = null;
		String[] projection = null;
		
		if (loaderId == LOADER_ORDERS || loaderId == LOADER_HISTORY_ORDERS) {
			uri = Order.CONTENT_URI;
			projection = new String[] { Order.Columns._ID,
						   				Order.Columns.FILE_NO,
						   				Order.Columns.VOYAGE_NO,
						   				Order.Columns.HAZMAT_FLAG,
						   				Order.Columns.APPT_DATE_TIME,
						   				Order.Columns.MOVE_TYPE,
						   				Order.Columns.CONFIRMED_FLAG };
		} else if (loaderId == LOADER_MESSAGE_ALERTS) {
			uri = MessageAlert.CONTENT_URI;
			projection = new String[] { MessageAlert.Columns._ID,
										MessageAlert.Columns.DRIVER_NO,
										MessageAlert.Columns.MESSAGE_FLAG };
		}
		
		if (loaderId == LOADER_ORDERS) {
			selection = Order.Columns.COMPLETED_FLAG + " != 1";
			sortOrder = Order.Columns.APPT_DATE_TIME;
		} else if (loaderId == LOADER_HISTORY_ORDERS) {
			selection = Order.Columns.COMPLETED_FLAG + " = 1";
			sortOrder = Order.Columns.APPT_DATE_TIME + " DESC";
		} else if (loaderId == LOADER_MESSAGE_ALERTS) {
			selection = MessageAlert.Columns.DRIVER_NO + " = " + Utils.getDriverNo(_activity);
		}
		
		return new CursorLoader(_activity, uri, projection, selection, null, sortOrder);
		
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		if (loader.getId() == LOADER_MESSAGE_ALERTS) {
			if (cursor.moveToFirst())
			{
				if (cursor.getInt(cursor.getColumnIndex(MessageAlert.Columns.MESSAGE_FLAG)) == 1) {
					_messageButton.setBackgroundColor(_activity.getResources().getColor(R.color.red));
				} else {
					int currentApiVersion = Build.VERSION.SDK_INT;
					
					if (currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN) {
						Button tempButton = new Button(_activity);
						_messageButton.setBackground(tempButton.getBackground());
					} else {
						_messageButton.setBackgroundColor(0);
					}
				}
			}
		} else {
			cursor.moveToFirst();
			_listAdapter.swapCursor(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() != LOADER_MESSAGE_ALERTS) {
			_listAdapter.swapCursor(null);
		}
	}
}
