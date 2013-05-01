package com.github.riotopsys.malforandroid2;

import android.util.Log;

public class GlobalState {
	
	private static final String TAG = GlobalState.class.getSimpleName();
	
	private String user = null;
	private String pass = null;
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		Log.v(TAG, String.format("User is: '%s'!", user));
		this.user = user;
	}
	
	public String getPass() {
		return pass;
	}
	
	public void setPass(String pass) {
		Log.v(TAG, "password is set!");
		this.pass = pass;
	}

	public boolean loginSet() {
		return ( user != null && pass != null);
	}
	
	

}
