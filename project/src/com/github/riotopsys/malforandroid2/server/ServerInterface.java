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

package com.github.riotopsys.malforandroid2.server;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import roboguice.service.RoboIntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.riotopsys.malforandroid2.GlobalState;
import com.github.riotopsys.malforandroid2.database.DatabaseHelper;
import com.github.riotopsys.malforandroid2.event.AnimeSearchUpdated;
import com.github.riotopsys.malforandroid2.event.AnimeUpdateEvent;
import com.github.riotopsys.malforandroid2.event.CredentialVerificationEvent;
import com.github.riotopsys.malforandroid2.model.AnimeListResponse;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.JournalEntry;
import com.github.riotopsys.malforandroid2.model.NameValuePair;
import com.github.riotopsys.malforandroid2.model.UpdateType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import de.greenrobot.event.EventBus;

public class ServerInterface extends RoboIntentService {

	private static final String TAG = ServerInterface.class.getSimpleName();
	private static final String ACTION_KEY = ServerInterface.class
			.getCanonicalName() + ".action";

	public static final String ANIME_UPDATED = ServerInterface.class
			.getCanonicalName() + ".anime_updated";
	public static final String ID_KEY = ServerInterface.class
			.getCanonicalName() + ".id";
	private static final String ID_CRITERIA = ServerInterface.class
			.getCanonicalName() + ".criteria";
	
	private enum Action {
		GET_ANIME_LIST, GET_ANIME_RECORD, VERIFY_CREDENTIALS, UPDATE_ANIME_RECORD, ADD_ANIME, SEARCH_ANIME
	};

	@Inject
	private UrlBuilder urlBuilder;

	@Inject
	private RestHelper restHelper;
	
	@Inject 
	private Gson gson;
	
	@Inject
	private EventBus bus;
	
	@Inject
	private GlobalState state;

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
		String criteria = null;
		if (extras.containsKey(ID_CRITERIA)) {
			criteria = extras.getString(ID_CRITERIA);
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
				break;
			case ADD_ANIME:
				addAnimeRecord(id);
				break;
			case SEARCH_ANIME:
				searchAnime(criteria);
				break;
			default:
				Log.v(TAG, String.format("Invalid Request: %s", action.name()));
			}
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}
	}
	
	private void addAnimeRecord(int id) throws SQLException, MalformedURLException {
		Dao<AnimeRecord, Integer> dao = getHelper().getDao(AnimeRecord.class);
		
		Dao<JournalEntry, Integer> journalDao = getHelper().getDao(JournalEntry.class);
		journalUpdate(journalDao, new JournalEntry(id, UpdateType.ADD_TO_LIST) );
		
		AnimeRecord anime = dao.queryForId(id);
		String value = String.format("status=%s&anime_id=%d",
				anime.watched_status.getServerKey(), anime.id);
		URL url = urlBuilder.getAnimeAddUrl();
		Log.v(TAG, String.format("url %s data %s", url, value));
		RestResult<String> result = restHelper.post(url, value);
		if ( result.code == 200 ){
			journalDao.deleteById(id);
		}
	}

	private void updateAnimeRecord(int id) throws SQLException, MalformedURLException {
		Dao<AnimeRecord, Integer> dao = getHelper().getDao(AnimeRecord.class);
		
		Dao<JournalEntry, Integer> journalDao = getHelper().getDao(JournalEntry.class);
		journalUpdate(journalDao, new JournalEntry(id, UpdateType.UPDATED) );
		
		AnimeRecord anime = dao.queryForId(id);
		String value = String.format("status=%s&episodes=%d&score=%d",
				anime.watched_status.getServerKey(), anime.watched_episodes, anime.score);
		
		Log.v(TAG, String.format("url %s data %s",  urlBuilder.getAnimeUpdateUrl(id), value));
		RestResult<String> result = restHelper.put(urlBuilder.getAnimeUpdateUrl(id), value);
		if ( result.code == 200 ){
			journalDao.deleteById(id);
		}
	}

	private void journalUpdate(Dao<JournalEntry, Integer> journalDao, JournalEntry journalEntry) throws SQLException {
		JournalEntry original = journalDao.queryForId(journalEntry.recordId);
		if ( original == null ){
			// id has no out standing operations so add it
			journalDao.create(journalEntry);
		} else {
			if ( original.updateType == UpdateType.ADD_TO_LIST && journalEntry.updateType == UpdateType.DELETE_FROM_LIST){
				//this indicates a noop condition, the user added a list then removed it we no longer need this in the journal
				journalDao.deleteById(original.recordId);
			} else if ( original.updateType == UpdateType.DELETE_FROM_LIST && journalEntry.updateType == UpdateType.ADD_TO_LIST ){
				//this indicates a noop condition, the user removed a list then added it back we no longer need this in the journal
				journalDao.deleteById(original.recordId);
			} else if ( original.updateType == UpdateType.UPDATED && journalEntry.updateType == UpdateType.DELETE_FROM_LIST ){
				//delete takes precedence
				journalDao.update(journalEntry);
			}
			// add && update = keep add
			// update && add = WFT?
			// delete && update = WTF?
			
			//This system leaves a hole if the sequence is update, delete, add causing the original update to become orphaned.  
		}
	}

	private void getAnimeRecord(int id) throws MalformedURLException, SQLException {
		RestResult<String> result = restHelper.get(urlBuilder.getAnimeRecordUrl(id));
		if (result.code == 200) {
			
			AnimeRecord ar = gson.fromJson(result.result, AnimeRecord.class);
			getHelper().getDao(AnimeRecord.class).createOrUpdate(ar);
			
			Dao<JournalEntry, Integer> journalDao = getHelper().getDao(JournalEntry.class);
			journalDao.deleteById(ar.id);
			bus.post(new AnimeUpdateEvent(id));
		}
	}

	private void getAnimeList() throws MalformedURLException, SQLException {
		String user = state.getUser();
		if ( user == null ){
			return;
		}
		Dao<AnimeRecord, Integer> dao = getHelper().getDao(AnimeRecord.class);
		Dao<JournalEntry, Integer> journalDao = getHelper().getDao(JournalEntry.class);
		
		//push outstanding journal entries to server
		List<JournalEntry> journal = journalDao.queryForAll();
		for( JournalEntry je : journal ){
			switch (je.updateType) {
			case ADD_TO_LIST:
				addAnimeRecord(je.recordId);
				break;
			case DELETE_FROM_LIST:
				//unimplemented
				break;
			case UPDATED:
				updateAnimeRecord(je.recordId);
				break;
			}
		}
		
		//get list from server
		RestResult<String> result = restHelper.get(urlBuilder.getAnimeListUrl(user ));
		if (result.code == 200) {
			Log.v(TAG, result.result);

			List<AnimeRecord> alr = gson.fromJson(result.result, AnimeListResponse.class).anime;
			if (alr == null || alr.isEmpty()) {
				return;
			}
			
			
			//get ids that may have been removed, a.k.a. all ids  
			GenericRawResults<String[]> data = dao.queryBuilder().selectColumns("id").where().isNotNull("watched_status").queryRaw();
			Set<Integer> deletedIds = new HashSet<Integer>();
			
			for ( String[] s: data.getResults()){
				deletedIds.add(Integer.parseInt(s[0]));
			}
			
			//update local items
			for ( AnimeRecord ar : alr ){
				//remove form suspect list 
				deletedIds.remove(ar.id);
				AnimeRecord arOriginal = dao.queryForId(ar.id);
				if ( arOriginal != null ){
					arOriginal.watched_status = ar.watched_status;
					arOriginal.score = ar.score;
					arOriginal.watched_episodes = ar.watched_episodes;
					
					dao.createOrUpdate(arOriginal);
				} else {
					dao.createOrUpdate(ar);
				}
				journalDao.deleteById(ar.id);
				bus.post(new AnimeUpdateEvent(ar.id));
			}
			
			//any items still in deletedIds need to be cleared
			for ( int id : deletedIds){
				AnimeRecord arOriginal = dao.queryForId(id);
				arOriginal.watched_status = null;
				arOriginal.score = 0;
				arOriginal.watched_episodes = 0;
				dao.createOrUpdate(arOriginal);
				journalDao.deleteById(id);
				bus.post(new AnimeUpdateEvent(id));
			}
		}
	}
	
	private void searchAnime( String criteria ) throws MalformedURLException, SQLException, UnsupportedEncodingException {
		RestResult<String> result = restHelper.get(urlBuilder.getSearchUrl( criteria ));
		if (result.code == 200) {
			Log.v(TAG, result.result);
			
			List<AnimeRecord> alr = gson.fromJson(result.result, new TypeToken<List<AnimeRecord>>(){}.getType());
			if (alr == null || alr.isEmpty()) {
				return;
			}
			
			Dao<AnimeRecord, Integer> dao = getHelper().getDao(AnimeRecord.class);
			
			List<Integer> ids = new LinkedList<Integer>();
			
			for ( AnimeRecord ar : alr ){
				ar.synopsis = null;//silence the partial synopsis so we can fetch the full later
				AnimeRecord arOriginal = dao.queryForId(ar.id);
				
				ids.add(ar.id);
				
				if ( arOriginal == null ){
					//limit additions to items not in the database.
					dao.create(ar);
				}
			}
			state.setSearchResults(ids);
			
			bus.post(new AnimeSearchUpdated());
		}
	}
	
	
	private void verifyCredentials() throws MalformedURLException, SQLException {
		
		RestResult<String> result = restHelper.get(urlBuilder.getVerifyCredentialsUrl());
		bus.post(new CredentialVerificationEvent(result.code));
		if ( result.code == 200 ){
			Dao<NameValuePair<String>, String> dao = getHelper().getDao(NameValuePair.class);
			dao.createOrUpdate(new NameValuePair<String>("USER", state.getUser()));
			dao.createOrUpdate(new NameValuePair<String>("PASS", state.getPass()));
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
	
	public static void addAnimeRecord(Context context, int id) {
		Intent serviceIntent = new Intent(context, ServerInterface.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.ADD_ANIME);
		bundle.putInt(ID_KEY, id);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}
	
	public static void searchAnime(Context context, String criteria) {
		Intent serviceIntent = new Intent(context, ServerInterface.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.SEARCH_ANIME);
		bundle.putString(ID_CRITERIA, criteria);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}

	public static void verifyCredentials(Context context) {
		Intent serviceIntent = new Intent(context, ServerInterface.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.SEARCH_ANIME);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}
	
	

}