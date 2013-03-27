package com.github.riotopsys.malforandroid2.activity;

import roboguice.inject.InjectView;
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
import com.github.riotopsys.malforandroid2.event.CredentialVerificationEvent;
import com.github.riotopsys.malforandroid2.server.RestHelper;
import com.github.riotopsys.malforandroid2.server.ServerInterface;
import com.google.inject.Inject;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class LoginActivity extends BaseActivity implements OnClickListener, OnEditorActionListener {
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		dipshit.setOnClickListener(this);
		password.setOnEditorActionListener(this);
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
	public void loginResults( CredentialVerificationEvent cve ){
		if ( cve.code == 200 ){
			startActivity(new Intent(this, HelloAndroidActivity.class));
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		processLogin();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if ( actionId == EditorInfo.IME_ACTION_DONE ){
			processLogin();
		}
		return false;
	}
	
	
	public void processLogin(){
		restHelper.setCredentials(username.getText().toString().trim(), password.getText().toString().trim());
		
		ServerInterface.verifyCredentials(this);
	}
	
	
}
