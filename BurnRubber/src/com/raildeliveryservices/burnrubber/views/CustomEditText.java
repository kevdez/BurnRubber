package com.raildeliveryservices.burnrubber.views;

import android.content.Context;
import android.widget.EditText;

public class CustomEditText extends EditText {

    private boolean _isRequiredFlag = false;

    public CustomEditText(Context context) {
        super(context);
    }

    public boolean getIsRequired() {
        return _isRequiredFlag;
    }

    public void setIsRequired(boolean value) {
        _isRequiredFlag = value;
    }
}
