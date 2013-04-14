package com.github.riotopsys.malforandroid2.activity;

import roboguice.activity.RoboFragmentActivity;

import com.github.riotopsys.malforandroid2.database.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class BaseActivity extends RoboFragmentActivity {
	
	private DatabaseHelper databaseHelper = null;

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (databaseHelper != null) {
	        OpenHelperManager.releaseHelper();
	        databaseHelper = null;
	    }
	}

	protected DatabaseHelper getHelper() {
	    if (databaseHelper == null) {
	        databaseHelper =
	            OpenHelperManager.getHelper(this, DatabaseHelper.class);
	    }
	    return databaseHelper;
	}
}
