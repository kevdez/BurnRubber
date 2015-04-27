package com.raildeliveryservices.burnrubber.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.data.Form;
import com.raildeliveryservices.burnrubber.utils.Utils;
import com.raildeliveryservices.burnrubber.views.CustomEditText;

import org.json.JSONObject;

import java.util.Date;

public class FormFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_FORMS = -1;

    private Activity _activity;
    private String _formName;
    private Callbacks _callbacks;
    private OnClickListener _buttonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sendFormButton:
                    if (validateForm()) {
                        sendFormToServer();
                        _callbacks.onFormSendButtonClick();
                    }
                    break;
                case R.id.cancelFormButton:
                    _callbacks.onFormCancelButtonClick();
                    break;
                case R.id.dvirHelpButton:
                    showDVIRHelp();
                    break;
            }
        }
    };

    public FormFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _activity = getActivity();

        Bundle bundle = getArguments();
        _formName = bundle.getString(Constants.BUNDLE_PARAM_FORM_NAME);

        _activity.setTitle(_formName);

        Button sendFormButton = (Button) _activity.findViewById(R.id.sendFormButton);
        Button cancelFormButton = (Button) _activity.findViewById(R.id.cancelFormButton);
        Button dvirHelpButton = (Button) _activity.findViewById(R.id.dvirHelpButton);

        sendFormButton.setOnClickListener(_buttonListener);
        cancelFormButton.setOnClickListener(_buttonListener);

        if (_formName.equals("DVIR POWER") || _formName.equals("DVIR TOWED")) {
            dvirHelpButton.setVisibility(View.VISIBLE);
            dvirHelpButton.setOnClickListener(_buttonListener);
        }

        Loader<Cursor> formsLoader = getLoaderManager().getLoader(LOADER_FORMS);

        if (formsLoader != null && !formsLoader.isReset()) {
            getLoaderManager().restartLoader(LOADER_FORMS, null, this);
        } else {
            getLoaderManager().initLoader(LOADER_FORMS, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.form_fragment, container, false);
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

    private void loadFormLayout(Cursor cursor) {

        final LinearLayout layout = (LinearLayout) _activity.findViewById(R.id.formLayout);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);

        do {
            LinearLayout innerLayout = new LinearLayout(_activity);
            innerLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(_activity);
            CustomEditText editText = new CustomEditText(_activity);
            CheckBox checkBox = new CheckBox(_activity);
            final InputFilter[] inputFilter = new InputFilter[1];
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

            String label = cursor.getString(cursor.getColumnIndex(Form.Columns.LABEL));
            boolean isRequired = cursor.getInt(cursor.getColumnIndex(Form.Columns.MUST_FILL_FLAG)) == 1;
            String formType = cursor.getString(cursor.getColumnIndex(Form.Columns.FORM_TYPE));
            int fillIn = cursor.getInt(cursor.getColumnIndex(Form.Columns.FILL_IN));

            textView.setLayoutParams(layoutParams);
            textView.setText(label);
            innerLayout.addView(textView);

            inputFilter[0] = new InputFilter.LengthFilter(fillIn);

            if (formType.equals("B")) {
                checkBox.setLayoutParams(layoutParams);
                checkBox.setTag(label);
            } else {
                editText.setLayoutParams(layoutParams);
                editText.setTag(label);
                editText.setIsRequired(isRequired);
                editText.setFilters(inputFilter);

                boolean driverNoFlag = cursor.getInt(cursor.getColumnIndex(Form.Columns.DRIVER_FLAG)) == 1;

                if (driverNoFlag) {
                    editText.setText(Utils.getDriverNo(_activity));
                }

                editText.setHint("Enter value");
            }

            if (formType.equals("N")) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (formType.equals("A")) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            } else if (formType.equals("AN")) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            } else if (formType.equals("B")) {
                checkBox.setLayoutParams(layoutParams);
            } else {
                editText.setVisibility(View.GONE);
            }

            if (formType.equals("B")) {
                innerLayout.addView(checkBox);
            } else {
                if (fillIn > 0) {
                    innerLayout.addView(editText);
                }
            }

            layout.addView(innerLayout);
        } while (cursor.moveToNext());
    }

    private boolean validateForm() {
        LinearLayout layout = (LinearLayout) _activity.findViewById(R.id.formLayout);

        for (int i = 0; i < layout.getChildCount(); i++) {

            try {
                View childLayout = layout.getChildAt(i);

                if (childLayout instanceof LinearLayout) {
                    View childView = ((LinearLayout) childLayout).getChildAt(1);

                    if (childView instanceof CustomEditText) {
                        CustomEditText editText = (CustomEditText) childView;

                        if (editText.getIsRequired()) {
                            if (TextUtils.isEmpty(editText.getText())) {
                                showValidationDialog(editText.getTag().toString());
                                return false;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    private void showValidationDialog(String editTextName) {
        new AlertDialog.Builder(_activity)
                .setTitle("Field Required")
                .setMessage(editTextName + " is required.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void sendFormToServer() {

        LinearLayout layout = (LinearLayout) _activity.findViewById(R.id.formLayout);
        int sequence = 0;

        if (_formName.equals("DVIR POWER")) {
            sequence = Utils.getSequenceNumber(_activity, Constants.SETTINGS_DVIR_POWERED_SEQUENCE);
        } else if (_formName.equals("DVIR TOWED")) {
            sequence = Utils.getSequenceNumber(_activity, Constants.SETTINGS_DVIR_TOWED_SEQUENCE);
        }

        for (int i = 0; i < layout.getChildCount(); i++) {

            boolean sendFlag = false;

            try {
                View childLayout = layout.getChildAt(i);

                if (childLayout instanceof LinearLayout) {
                    View childView = ((LinearLayout) childLayout).getChildAt(1);
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.accumulate("DriverNo", Utils.getDriverNo(_activity));
                    jsonObject.accumulate("FormName", _formName);
                    jsonObject.accumulate("ClientDateTime", Constants.ServerDateFormat.format(new Date()));

                    if (_formName.equals("DVIR POWER") || _formName.equals("DVIR TOWED")) {
                        jsonObject.accumulate("Sequence", sequence);
                    } else {
                        jsonObject.accumulate("InOutFlag", "I");
                    }

                    if (childView instanceof CustomEditText) {
                        CustomEditText editText = (CustomEditText) childView;

                        if (!TextUtils.isEmpty(editText.getText())) {
                            jsonObject.accumulate("Label", editText.getTag().toString());
                            jsonObject.accumulate("MessageText", editText.getText().toString());
                            sendFlag = true;
                        }
                    } else if (childView instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) childView;

                        if (checkBox.isChecked()) {
                            jsonObject.accumulate("Label", checkBox.getTag().toString());
                            jsonObject.accumulate("MessageText", checkBox.isChecked() ? "1" : "0");
                            sendFlag = true;
                        }
                    }

                    if (sendFlag) {
                        if (_formName.equals("DVIR POWER") || _formName.equals("DVIR TOWED")) {
                            Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_DVIR_MESSAGE, jsonObject);
                        } else {
                            Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_MESSAGE, jsonObject);
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    private void showDVIRHelp() {
        new AlertDialog.Builder(_activity)
                .setTitle(_activity.getString(R.string.dvir_help_dialog_title))
                .setMessage(_activity.getString(R.string.dvir_help_dialog_message))
                .setPositiveButton(_activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        Uri uri = null;
        String[] projection = null;
        String selection = null;
        String sortOrder = null;

        if (loaderId == LOADER_FORMS) {
            uri = Form.CONTENT_URI;
            projection = new String[]{Form.Columns._ID,
                    Form.Columns.LABEL,
                    Form.Columns.FORM_NAME,
                    Form.Columns.FILL_IN,
                    Form.Columns.FORM_TYPE,
                    Form.Columns.MUST_FILL_FLAG,
                    Form.Columns.DRIVER_FLAG};
            selection = Form.Columns.FORM_NAME + " = '" + _formName + "'";
            sortOrder = Form.Columns._ID;
        }

        return new CursorLoader(_activity, uri, projection, selection, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();

        if (loader.getId() == LOADER_FORMS) {
            loadFormLayout(cursor);
        }

        getLoaderManager().destroyLoader(LOADER_FORMS);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public interface Callbacks {
        public void onFormSendButtonClick();

        public void onFormCancelButtonClick();
    }
}
