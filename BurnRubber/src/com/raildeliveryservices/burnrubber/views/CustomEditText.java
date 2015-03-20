package com.raildeliveryservices.burnrubber.views;

import android.content.Context;
import android.widget.EditText;

public class CustomEditText extends EditText {

	private boolean _isRequiredFlag = false;
	
	public CustomEditText(Context context) {
		super(context);
	}
	
	public void setIsRequired(boolean value) {
		_isRequiredFlag = value;
	}
	
	public boolean getIsRequired() {
		return _isRequiredFlag;
	}
}
