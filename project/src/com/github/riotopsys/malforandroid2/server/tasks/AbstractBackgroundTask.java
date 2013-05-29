package com.github.riotopsys.malforandroid2.server.tasks;

import java.util.concurrent.Executor;

import android.content.Context;
import android.util.Base64;

import com.github.riotopsys.malforandroid2.GlobalState;
import com.github.riotopsys.malforandroid2.database.DatabaseHelper;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import de.greenrobot.event.EventBus;

public abstract class AbstractBackgroundTask implements Runnable {
	
	@Inject 
	private EventBus bus;
	
	@Inject
	private Context ctx;
	
	@Inject
	private GlobalState state;
	
	@Inject 
	private Executor executor;
	
	private DatabaseHelper databaseHelper = null;
	
	protected abstract void runInBackground(Context ctx, EventBus bus, GlobalState state); 

	@Override
	public final void run() {
		runInBackground(ctx, bus, state);
		onDestroy();
	}
	
	public void start(){
		executor.execute(this);
	}
	
	protected String generateCredentials() {
		String username = state.getUser();
		String password = state.getPass();
		if (username == null || password == null) {
			return null;
		}
		return "Basic "
				+ Base64.encodeToString((username + ":" + password).getBytes(),
						Base64.DEFAULT | Base64.NO_WRAP);
	}
	
	private void onDestroy() {
	    if (databaseHelper != null) {
	        OpenHelperManager.releaseHelper();
	        databaseHelper = null;
	    }
	}

	protected DatabaseHelper getHelper() {
	    if (databaseHelper == null) {
	        databaseHelper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
	    }
	    return databaseHelper;
	}

}
