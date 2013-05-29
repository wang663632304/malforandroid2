package com.github.riotopsys.malforandroid2.server.tasks;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.util.Log;

import com.github.riotopsys.malforandroid2.GlobalState;
import com.github.riotopsys.malforandroid2.event.CredentialVerificationEvent;
import com.github.riotopsys.malforandroid2.model.NameValuePair;
import com.github.riotopsys.malforandroid2.server.retrofit.AnimeInterconnect;
import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

import de.greenrobot.event.EventBus;

public class VerifyCredentialsTask extends AbstractBackgroundTask {

	private static final String TAG = VerifyCredentialsTask.class.getSimpleName();

	@Inject
	private AnimeInterconnect connection;

	@Override
	protected void runInBackground(final Context ctx, final EventBus bus, final GlobalState state) {
		Response result;
		try{
			result = connection.verifyCredentials(generateCredentials());
		} catch (RetrofitError e){
			Log.e(TAG, e.getUrl(), e);
			result = e.getResponse();
		}
		int status = result.getStatus();
		bus.post(new CredentialVerificationEvent(status));
		if (status == HttpURLConnection.HTTP_OK) {
			final Dao<NameValuePair, String> dao;
			try {
				dao = getHelper().getDao(NameValuePair.class);
				dao.callBatchTasks(new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						dao.createOrUpdate(new NameValuePair("USER", state.getUser()));
						dao.createOrUpdate(new NameValuePair("PASS", state.getPass()));
						return null;
					}
				});
			} catch (SQLException e) {
				Log.e(TAG, "", e);
			} catch (Exception e) {
				Log.e(TAG, "", e);
			}
		}
	}
}
