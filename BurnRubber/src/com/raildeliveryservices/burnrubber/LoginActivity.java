package com.raildeliveryservices.burnrubber;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.raildeliveryservices.burnrubber.tasks.LoginAsyncTask;
import com.raildeliveryservices.burnrubber.utils.Services;
import com.raildeliveryservices.burnrubber.utils.Utils;

public class LoginActivity extends BaseFragmentActivity {

    private EditText _userNameEditText;
    private EditText _passwordEditText;
    private Button _loginButton;
    private OnClickListener _buttonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loginButton:
                    LoginAsyncTask loginAsyncTask = new LoginAsyncTask(LoginActivity.this);
                    loginAsyncTask.execute(new String[]{_userNameEditText.getText().toString().trim(), _passwordEditText.getText().toString().trim()});
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        if (Utils.isUserLoggedIn(this)) {
            startActivity(new Intent(this, OrderActivity.class));
        }

        _userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        _passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        _loginButton = (Button) findViewById(R.id.loginButton);
        _loginButton.setOnClickListener(_buttonListener);

        _userNameEditText.setText(Utils.getDriverNo(this));
    }
}