/**
 * Copyright 2013 C. A. Fitzgerald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.github.riotopsys.malforandroid2.activity;

import java.util.List;

import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.database.ReadNameValuePairs;
import com.github.riotopsys.malforandroid2.database.ReadNameValuePairs.Callback;
import com.github.riotopsys.malforandroid2.event.CredentialVerificationEvent;
import com.github.riotopsys.malforandroid2.model.NameValuePair;
import com.github.riotopsys.malforandroid2.server.RestHelper;
import com.github.riotopsys.malforandroid2.server.ServerInterface;
import com.google.inject.Inject;

import de.greenrobot.event.EventBus;

public class LoginActivity extends BaseActivity implements OnClickListener,
		OnEditorActionListener, Callback<String> {

	@InjectView(R.id.login_dipshit_button)
	private Button dipshit;

	@InjectView(R.id.login_username)
	private EditText username;

	@InjectView(R.id.login_password)
	private EditText password;

	@Inject
	private RestHelper restHelper;

	@Inject
	private EventBus bus;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		dipshit.setOnClickListener(this);
		password.setOnEditorActionListener(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getString(R.string.loading));
		progressDialog.setCanceledOnTouchOutside(false);

		new ReadNameValuePairs<String>(getHelper(), this).execute("TOKEN");
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

	public void onEventMainThread(CredentialVerificationEvent cve) {
		progressDialog.cancel();
		if (cve.code == 200) {
			startActivity(new Intent(this, HelloAndroidActivity.class));
			finish();
		} else if (cve.code == 401) {
			new AlertDialog.Builder(this).setTitle(R.string.login_error_title)
					.setMessage(R.string.login_error_invalid_combo).create()
					.show();
		} else {
			new AlertDialog.Builder(this).setTitle(R.string.login_error_title)
					.setMessage(R.string.login_error_unknown_issue).create()
					.show();
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
		String user = username.getText().toString().trim();
		String pass = password.getText().toString().trim();
		if (user.equals("") || pass.equals("")) {
			new AlertDialog.Builder(this).setTitle(R.string.login_error_title)
					.setMessage(R.string.login_error_no_creds).create().show();
			return;
		}
		progressDialog.show();
		restHelper.setCredentials(user, pass);
		ServerInterface.verifyCredentials(this);
	}

	@Override
	public void onNameValuePairsReady(List<NameValuePair<String>> data) {
		for (NameValuePair<String> pair : data) {
			if ("TOKEN".equals(pair.name)) {
				restHelper.setToken((String) pair.value);
				startActivity(new Intent(this, HelloAndroidActivity.class));
				finish();
			}
			break;
		}
	}
}
