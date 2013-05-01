package com.github.riotopsys.malforandroid2.fragment;

import roboguice.fragment.RoboDialogFragment;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.github.riotopsys.malforandroid2.GlobalState;
import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.event.CredentialVerificationEvent;
import com.github.riotopsys.malforandroid2.server.ServerInterface;
import com.google.inject.Inject;

import de.greenrobot.event.EventBus;

public class LoginFragment extends RoboDialogFragment implements OnClickListener, OnEditorActionListener {
	
	@InjectView(R.id.login_dipshit_button)
	private Button dipshit;

	@InjectView(R.id.login_username)
	private EditText username;

	@InjectView(R.id.login_password)
	private EditText password;

	@Inject 
	private GlobalState state;

	@Inject
	private EventBus bus;

	private ProgressDialog progressDialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.login, null, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		Dialog d = getDialog();
		if ( d != null ){
			d.setTitle(R.string.login_button);
			d.setCancelable(false);
			d.setCanceledOnTouchOutside(false);
		}
		
		
		dipshit.setOnClickListener(this);
		password.setOnEditorActionListener(this);

		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage(getString(R.string.loading));
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

	public void onEventMainThread(CredentialVerificationEvent cve) {
		progressDialog.cancel();
		if (cve.code == 200) {
			ServerInterface.getAnimeList(getActivity());
			dismiss();
		} else if (cve.code == 401) {
			new AlertDialog.Builder(getActivity()).setTitle(R.string.login_error_title)
					.setMessage(R.string.login_error_invalid_combo).create()
					.show();
		} else {
			new AlertDialog.Builder(getActivity()).setTitle(R.string.login_error_title)
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
			new AlertDialog.Builder(getActivity()).setTitle(R.string.login_error_title)
					.setMessage(R.string.login_error_no_creds).create().show();
			return;
		}
		
		state.setUser(user);
		state.setPass(pass);
		
		progressDialog.show();
		ServerInterface.verifyCredentials( getActivity() );
	}

}
