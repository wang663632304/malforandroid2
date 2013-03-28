package com.github.riotopsys.malforandroid2.activity;

import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.event.CredentialVerificationEvent;
import com.github.riotopsys.malforandroid2.runnable.ShowAlert;
import com.github.riotopsys.malforandroid2.server.RestHelper;
import com.github.riotopsys.malforandroid2.server.ServerInterface;
import com.google.inject.Inject;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class LoginActivity extends BaseActivity implements OnClickListener,
		OnEditorActionListener {

	@InjectView(R.id.login_dipshit_button)
	private Button dipshit;

	@InjectView(R.id.login_username)
	private EditText username;

	@InjectView(R.id.login_password)
	private EditText password;

	@Inject
	private RestHelper restHelper;

	@Inject
	private Bus bus;

	private ProgressDialog progressDialog;
	
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		dipshit.setOnClickListener(this);
		password.setOnEditorActionListener(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Loading...");
		progressDialog.setCanceledOnTouchOutside(false);
	}

	@Override
	public void onPause() {
		bus.unregister(this);
		super.onPause();
	}

	@Override
	public void onResume() {
		bus.register(this);
		super.onResume();
	}

	@Subscribe
	public void loginResults(CredentialVerificationEvent cve) {
		progressDialog.cancel();
		if (cve.code == 200) {
			startActivity(new Intent(this, HelloAndroidActivity.class));
			finish();
//		} else if (cve.code == 401) {
//			handler.post(new ShowAlert(new AlertDialog.Builder(this).setTitle("Login failed")
//					.setMessage("Invalid user and password combination")
//					.create()));
//			;
		} else {
			handler.post(new ShowAlert(this, "Login failed", "Unknown server error. Please try again later") );
//			handler.post(new ShowAlert(new AlertDialog.Builder(this).setTitle("Login failed")
//					.setMessage("Unknown server error. Please try again later")
//					.create()));
		}
	}

	@Override
	public void onClick(View v) {
		processLogin();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			processLogin();
		}
		return false;
	}

	public void processLogin() {
		progressDialog.show();
		restHelper.setCredentials(username.getText().toString().trim(),
				password.getText().toString().trim());
		ServerInterface.verifyCredentials(this);
	}

}
