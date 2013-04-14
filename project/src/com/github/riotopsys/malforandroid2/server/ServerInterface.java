package com.github.riotopsys.malforandroid2.server;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;

import roboguice.service.RoboIntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.riotopsys.malforandroid2.database.DatabaseHelper;
import com.github.riotopsys.malforandroid2.event.AnimeUpdateEvent;
import com.github.riotopsys.malforandroid2.event.CredentialVerificationEvent;
import com.github.riotopsys.malforandroid2.model.AnimeListResponse;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.NameValuePair;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import de.greenrobot.event.EventBus;

public class ServerInterface extends RoboIntentService {

	private static final String TAG = ServerInterface.class.getSimpleName();
	private static final String ACTION_KEY = ServerInterface.class
			.getCanonicalName() + ".action";

	public static final String ANIME_UPDATED = ServerInterface.class
			.getCanonicalName() + ".anime_updated";
	public static final String ID_KEY = ServerInterface.class
			.getCanonicalName() + ".id";
	
	private enum Action {
		GET_ANIME_LIST, GET_ANIME_RECORD, VERIFY_CREDENTIALS, UPDATE_ANIME_RECORD
	};

	@Inject
	private UrlBuilder urlBuilder;

	@Inject
	private RestHelper restHelper;
	
	@Inject 
	private Gson gson;
	
	@Inject
	private EventBus bus;

	public ServerInterface() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	private DatabaseHelper databaseHelper = null;

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	protected DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this,
					DatabaseHelper.class);
		}
		return databaseHelper;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		Action action = (Action) extras.getSerializable(ACTION_KEY);
		int id = 0;
		if (extras.containsKey(ID_KEY)) {
			id = extras.getInt(ID_KEY);
		}

		try {
			switch (action) {
			case GET_ANIME_LIST:
				getAnimeList();
				break;
			case GET_ANIME_RECORD:
				getAnimeRecord(id);
				break;
			case VERIFY_CREDENTIALS:
				verifyCredentials();
				break;
			case UPDATE_ANIME_RECORD:
				updateAnimeRecord(id);
			default:
				Log.v(TAG, String.format("Invalid Request: %s", action.name()));
			}
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}
	}

	private void updateAnimeRecord(int id) throws SQLException, MalformedURLException {
		Dao<AnimeRecord, Integer> dao = getHelper().getDao(AnimeRecord.class);
		AnimeRecord anime = dao.queryForId(id);
		String value = String.format("status=%s&episodes=%d&score=%d",
				anime.watched_status, anime.watched_episodes, anime.score);
		restHelper.post(urlBuilder.getAnimeUpdateUrl(id), value);
	}

	private void getAnimeRecord(int id) throws MalformedURLException, SQLException {
		RestResult<String> result = restHelper.get(urlBuilder.getAnimeRecordUrl(id));
		if (result.code == 200) {
			Log.v(TAG, result.result);
			
			AnimeRecord ar = gson.fromJson(result.result, AnimeRecord.class);
			getHelper().getDao(AnimeRecord.class).createOrUpdate(ar);
			bus.post(new AnimeUpdateEvent(id));
		}
	}

	private void getAnimeList() throws MalformedURLException, SQLException {
		RestResult<String> result = restHelper.get(urlBuilder.getAnimeListUrl("riotopsys"));
		if (result.code == 200) {
			Log.v(TAG, result.result);

			List<AnimeRecord> alr = gson.fromJson(result.result, AnimeListResponse.class).anime;
			if (alr == null || alr.isEmpty()) {
				return;
			}
			
			Dao<AnimeRecord, Integer> dao = getHelper().getDao(AnimeRecord.class);
			
			for ( AnimeRecord ar : alr ){
				AnimeRecord arOriginal = dao.queryForId(ar.id);
				if ( arOriginal != null ){
					arOriginal.status = ar.status;
					arOriginal.score = ar.score;
					arOriginal.episodes = ar.episodes;
					
					dao.createOrUpdate(arOriginal);
				} else {
					dao.createOrUpdate(ar);
				}
				bus.post(new AnimeUpdateEvent(ar.id));
			}
		}
	}
	
	private void verifyCredentials() throws MalformedURLException, SQLException {
		
		RestResult<String> result = restHelper.get(urlBuilder.getVerifyCredentialsUrl());
		bus.post(new CredentialVerificationEvent(result.code));
		if ( result.code == 200 ){
			Dao<NameValuePair<String>, String> dao = getHelper().getDao(NameValuePair.class);
			dao.createOrUpdate(new NameValuePair<String>("TOKEN", restHelper.getToken()));
		}
	}
	
	public static void getAnimeList(Context context) {
		Intent serviceIntent = new Intent(context, ServerInterface.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.GET_ANIME_LIST);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}

	public static void getAnimeRecord(Context context, int id) {
		Intent serviceIntent = new Intent(context, ServerInterface.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.GET_ANIME_RECORD);
		bundle.putInt(ID_KEY, id);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}

	public static void updateAnimeRecord(Context context, int id) {
		Intent serviceIntent = new Intent(context, ServerInterface.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.UPDATE_ANIME_RECORD);
		bundle.putInt(ID_KEY, id);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}

	public static void verifyCredentials(Context context) {
		Intent serviceIntent = new Intent(context, ServerInterface.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.VERIFY_CREDENTIALS);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}

}