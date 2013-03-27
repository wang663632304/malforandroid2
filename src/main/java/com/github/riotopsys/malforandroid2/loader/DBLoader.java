package com.github.riotopsys.malforandroid2.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.github.riotopsys.malforandroid2.database.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public abstract class DBLoader<D> extends AsyncTaskLoader<D> {

	private DatabaseHelper databaseHelper;

	public DBLoader(Context context) {
		super(context);
	}
	
	@Override
	public void reset() {
	    super.reset();
	    if (databaseHelper != null) {
	        OpenHelperManager.releaseHelper();
	        databaseHelper = null;
	    }
	}

	protected DatabaseHelper getHelper() {
	    if (databaseHelper == null) {
	        databaseHelper =
	            OpenHelperManager.getHelper(this.getContext(), DatabaseHelper.class);
	    }
	    return databaseHelper;
	}
}
