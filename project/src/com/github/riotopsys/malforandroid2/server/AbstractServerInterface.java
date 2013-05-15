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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.github.riotopsys.malforandroid2.GlobalState;
import com.github.riotopsys.malforandroid2.database.DatabaseHelper;
import com.github.riotopsys.malforandroid2.event.AnimeSearchUpdated;
import com.github.riotopsys.malforandroid2.event.AnimeUpdateEvent;
import com.github.riotopsys.malforandroid2.event.CredentialVerificationEvent;
import com.github.riotopsys.malforandroid2.event.MangaSearchUpdated;
import com.github.riotopsys.malforandroid2.event.MangaUpdateEvent;
import com.github.riotopsys.malforandroid2.model.AnimeJournalEntry;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.BaseJournalEntry;
import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.github.riotopsys.malforandroid2.model.MangaJournalEntry;
import com.github.riotopsys.malforandroid2.model.MangaRecord;
import com.github.riotopsys.malforandroid2.model.NameValuePair;
import com.github.riotopsys.malforandroid2.model.UpdateType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import de.greenrobot.event.EventBus;

public abstract class AbstractServerInterface extends RoboIntentService {

	private static final String TAG = AbstractServerInterface.class.getSimpleName();
	private static final String ACTION_KEY = AbstractServerInterface.class
			.getCanonicalName() + ".action";

	public static final String ID_KEY = AbstractServerInterface.class
			.getCanonicalName() + ".id";
	private static final String ID_CRITERIA = AbstractServerInterface.class
			.getCanonicalName() + ".criteria";
	
	private enum Action {
		GET_LIST, GET_RECORD, VERIFY_CREDENTIALS, UPDATE_RECORD, ADD_RECORD, SEARCH, SYNC, RM_RECORD
	};

	@Inject
	private UrlBuilder urlBuilder;

	@Inject
	private RestHelper restHelper;
	
	@Inject
	protected Gson gson;
	
	@Inject
	private EventBus bus;
	
	@Inject
	private GlobalState state;
	
	protected abstract Class<?> getRecordClass();
	protected abstract Dao<BaseRecord, Integer> getRecordDao() throws SQLException;
	protected abstract Dao<BaseJournalEntry, Integer> getJournalDao() throws SQLException;
	protected abstract List<?> processListResponse( String raw);
	protected abstract void partialUpdate( BaseRecord original, BaseRecord incomming);
	protected abstract void removeFromList(BaseRecord arOriginal);

	public AbstractServerInterface() {
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
			case GET_LIST:
				getList();
				break;
			case GET_RECORD:
				getRecord(id);
				break;
			case VERIFY_CREDENTIALS:
				verifyCredentials();
				break;
			case UPDATE_RECORD:
				updateRecord(id);
				break;
			case ADD_RECORD:
				addRecord(id);
				break;
			case RM_RECORD:
				removeRecord(id);
				break;
			case SEARCH:
				search(criteria);
				break;
			case SYNC:
				sync();
				break;
			default:
				Log.v(TAG, String.format("Invalid Request: %s", action.name()));
			}
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}
	}
	
	private void sync() throws MalformedURLException, SQLException {
		//limit autosync operations to wifi only  
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
		    getList();
		}
	}

	private void addRecord(int id) throws SQLException, MalformedURLException {
		Dao<BaseRecord, Integer> dao = getRecordDao();
		Dao<BaseJournalEntry, Integer> journalDao = getJournalDao();
		
		BaseRecord record = dao.queryForId(id);
		URL url;
		String value;
		if ( record instanceof AnimeRecord ){
			journalUpdate(journalDao, new AnimeJournalEntry(id, UpdateType.ADD_TO_LIST) );
			AnimeRecord anime = (AnimeRecord)record;
			value = String.format("anime_id=%d&status=%s&episodes=%d&score=%d", anime.id, anime.watched_status.getServerKey(), anime.watched_episodes, anime.score);
			url = urlBuilder.getAnimeAddUrl();
		} else {
			journalUpdate(journalDao, new MangaJournalEntry(id, UpdateType.ADD_TO_LIST) );
			MangaRecord manga = (MangaRecord)record;
			value = String.format("manga_id=%d&status=%s&chapters=%d&volumes=%d&score%d", manga.id, manga.read_status.getServerKey(), manga.chapters_read, manga.volumes_read, manga.score);
			url = urlBuilder.getAnimeAddUrl();
		}
		
		Log.v(TAG, String.format("url %s data %s", url, value));
		RestResult<String> result = restHelper.post(url, value);
		if ( result.code == 200 ){
			journalDao.deleteById(id);
		}
		
		Log.v(TAG, String.format("addRecord: journal size, %d ",  journalDao.countOf()));
	}

	private void updateRecord(int id) throws SQLException, MalformedURLException {
		Dao<BaseRecord, Integer> dao = getRecordDao();
		Dao<BaseJournalEntry, Integer> journalDao = getJournalDao();
		
		BaseRecord record = dao.queryForId(id);
		URL url;
		String value;
		if ( record instanceof AnimeRecord ){
			journalUpdate(journalDao, new AnimeJournalEntry(id, UpdateType.UPDATED) );
			AnimeRecord anime = (AnimeRecord)record;
			value = String.format("status=%s&episodes=%d&score=%d", anime.watched_status.getServerKey(), anime.watched_episodes, anime.score);
			url = urlBuilder.getAnimeUpdateUrl(id);
		} else {
			journalUpdate(journalDao, new MangaJournalEntry(id, UpdateType.UPDATED) );
			MangaRecord manga = (MangaRecord)record;
			value = String.format("status=%s&chapters=%d&volumes=%d&score%d", manga.read_status.getServerKey(), manga.chapters_read, manga.volumes_read, manga.score);
			url = urlBuilder.getMangaUpdateUrl(id);
		}
		
		Log.v(TAG, String.format("url %s data %s", url, value));
		RestResult<String> result = restHelper.put(url, value);
		if ( result.code == 200 ){
			journalDao.deleteById(id);
		}
		
		Log.v(TAG, String.format("updateRecord: journal size, %d ",  journalDao.countOf()));
	}
	
	private void removeRecord(int id) throws SQLException, MalformedURLException {
		Dao<BaseJournalEntry, Integer> journalDao = getJournalDao();
		
		URL url;
		if ( getRecordClass().equals( AnimeRecord.class ) ){
			journalUpdate(journalDao, new AnimeJournalEntry(id, UpdateType.DELETE_FROM_LIST) );
			url = urlBuilder.getAnimeUpdateUrl(id);
		} else {
			journalUpdate(journalDao, new MangaJournalEntry(id, UpdateType.DELETE_FROM_LIST) );
			url = urlBuilder.getMangaUpdateUrl(id);
		}
		
		Log.v(TAG, String.format("url %s",  url));
		RestResult<String> result = restHelper.delete(url);
		if ( result.code == 200 ){
			journalDao.deleteById(id);
		}
		Log.v(TAG, String.format("removeRecord: journal size, %d ",  journalDao.countOf()));
	}

	private void journalUpdate(Dao<BaseJournalEntry, Integer> journalDao, BaseJournalEntry journalEntry) throws SQLException {
		BaseJournalEntry original = journalDao.queryForId(journalEntry.recordId);
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
			
			/*
			 * This system leaves a hole if the sequence is update, delete, add
			 * causing the original update to become orphaned. As this is a one
			 * entry per record journal I'm going to consider this acceptable.
			 * Otherwise we will have to use a more complicated journal.
			 */
		}
	}

	private void getRecord(int id) throws MalformedURLException, SQLException {
		Dao<BaseRecord, Integer> dao = getRecordDao();
		Dao<BaseJournalEntry, Integer> journalDao = getJournalDao();
		
		URL url;
		if ( getRecordClass().equals( AnimeRecord.class ) ){
			url = urlBuilder.getAnimeRecordUrl(id);
		} else {
			url = urlBuilder.getMangaRecordUrl(id);
		}
		
		RestResult<String> result = restHelper.get(url);
		if (result.code == 200) {
			
			BaseRecord ar = (BaseRecord) gson.fromJson(result.result, getRecordClass());
			dao.createOrUpdate(ar);
			
			journalDao.deleteById(ar.id);
			sendUpdateEvent(id);
		}
	}

	private void sendUpdateEvent(int id) {
		if ( getRecordClass().equals( AnimeRecord.class ) ){
			bus.post(new AnimeUpdateEvent(id));
		} else {
			bus.post(new MangaUpdateEvent(id));
		}
	}
	
	private void getList() throws MalformedURLException, SQLException {
		String user = state.getUser();
		if ( user == null ){
			return;
		}
		Dao<BaseRecord, Integer> dao = getRecordDao();
		Dao<BaseJournalEntry, Integer> journalDao = getJournalDao();
		
		Log.v(TAG, String.format("getList: journal size before, %d ",  journalDao.countOf()));
		//push outstanding journal entries to server
		List<BaseJournalEntry> journal = journalDao.queryForAll();
		for( BaseJournalEntry je : journal ){
			switch (je.updateType) {
			case ADD_TO_LIST:
				addRecord(je.recordId);
				break;
			case DELETE_FROM_LIST:
				removeRecord(je.recordId);
				break;
			case UPDATED:
				updateRecord(je.recordId);
				break;
			}
		}
		Log.v(TAG, String.format("getList: journal size after, %d ",  journalDao.countOf()));
		
		URL url;
		String criteria;
		if ( getRecordClass().equals( AnimeRecord.class ) ){
			url = urlBuilder.getAnimeListUrl(user);
			criteria = "watched_status";
		} else {
			url = urlBuilder.getMangaListUrl(user);
			criteria = "read_status";
		}
		
		//get list from server
		RestResult<String> result = restHelper.get(url);
		if (result.code == 200) {

			@SuppressWarnings("unchecked")
			List<BaseRecord> alr = (List<BaseRecord>) processListResponse(result.result);
			if (alr == null || alr.isEmpty()) {
				return;
			}
			
			//get ids that may have been removed, a.k.a. all ids  
			GenericRawResults<String[]> data = dao.queryBuilder().selectColumns("id").where().isNotNull(criteria).queryRaw();
			Set<Integer> deletedIds = new HashSet<Integer>();
			
			for ( String[] s: data.getResults()){
				deletedIds.add(Integer.parseInt(s[0]));
			}
			
			//update local items
			for ( BaseRecord ar : alr ){
				//remove form suspect list 
				deletedIds.remove(ar.id);
				BaseRecord arOriginal = dao.queryForId(ar.id);
				if ( arOriginal != null ){
//					arOriginal.watched_status = ar.watched_status;
//					arOriginal.score = ar.score;
//					arOriginal.watched_episodes = ar.watched_episodes;
					partialUpdate(arOriginal, ar);
					
					dao.createOrUpdate(arOriginal);
				} else {
					dao.createOrUpdate(ar);
				}
				journalDao.deleteById(ar.id);
				sendUpdateEvent(ar.id);
			}
			
			//any items still in deletedIds need to be cleared
			for ( int id : deletedIds){
				BaseRecord arOriginal = dao.queryForId(id);
//				arOriginal.watched_status = null;
//				arOriginal.score = 0;
//				arOriginal.watched_episodes = 0;
				removeFromList(arOriginal);
				
				dao.createOrUpdate(arOriginal);
				journalDao.deleteById(id);
				sendUpdateEvent(id);
			}
		}
	}
	
	private void search( String criteria ) throws MalformedURLException, SQLException, UnsupportedEncodingException {
		URL url;
		if ( getRecordClass().equals( AnimeRecord.class ) ){
			url = urlBuilder.getAnimeSearchUrl( criteria );
		} else {
			url = urlBuilder.getMangaSearchUrl( criteria );
		}
		
		RestResult<String> result = restHelper.get(url);
		if (result.code == 200) {
			Log.v(TAG, result.result);
			
			List<BaseRecord> alr;
			if ( getRecordClass().equals( AnimeRecord.class ) ){
				alr = gson.fromJson(result.result, new TypeToken<List<AnimeRecord>>(){}.getType());
			} else {
				alr = gson.fromJson(result.result, new TypeToken<List<MangaRecord>>(){}.getType());
			}
			
			if (alr == null || alr.isEmpty()) {
				return;
			}
			
			Dao<BaseRecord, Integer> dao = getRecordDao();
			
			List<Integer> ids = new LinkedList<Integer>();
			
			for ( BaseRecord ar : alr ){
				ar.synopsis = null;//silence the partial synopsis so we can fetch the full later
				BaseRecord arOriginal = dao.queryForId(ar.id);
				
				ids.add(ar.id);
				
				if ( arOriginal == null ){
					//limit additions to items not in the database.
					dao.create(ar);
				}
			}
			state.setSearchResults(ids);
			
			if ( getRecordClass().equals( AnimeRecord.class ) ){
				bus.post(new AnimeSearchUpdated());
			} else {
				bus.post(new MangaSearchUpdated());
			}
		}
	}
	
	
	private void verifyCredentials() throws MalformedURLException, SQLException {
		
		RestResult<String> result = restHelper.get(urlBuilder.getVerifyCredentialsUrl());
		bus.post(new CredentialVerificationEvent(result.code));
		if ( result.code == 200 ){
			Dao<NameValuePair, String> dao = getHelper().getDao(NameValuePair.class);
			dao.createOrUpdate(new NameValuePair("USER", state.getUser()));
			dao.createOrUpdate(new NameValuePair("PASS", state.getPass()));
		}
	}
	
	protected static void getList(Context context, Intent serviceIntent) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.GET_LIST);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}

	protected static void getRecord(Context context, int id, Intent serviceIntent) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.GET_RECORD);
		bundle.putInt(ID_KEY, id);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}

	protected static void updateRecord(Context context, int id, Intent serviceIntent) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.UPDATE_RECORD);
		bundle.putInt(ID_KEY, id);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}
	
	protected static void addRecord(Context context, int id, Intent serviceIntent) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.ADD_RECORD);
		bundle.putInt(ID_KEY, id);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}
	
	protected static void removeRecord(Context context, int id, Intent serviceIntent) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.RM_RECORD);
		bundle.putInt(ID_KEY, id);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}
	
	protected static void search(Context context, String criteria, Intent serviceIntent) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.SEARCH);
		bundle.putString(ID_CRITERIA, criteria);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}

	protected static void verifyCredentials(Context context, Intent serviceIntent) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.VERIFY_CREDENTIALS);
		serviceIntent.putExtras(bundle);
		context.startService(serviceIntent);
	}
	
	protected static Intent getSyncIntent(Context context, Intent serviceIntent){
		Bundle bundle = new Bundle();
		bundle.putSerializable(ACTION_KEY, Action.SYNC);
		serviceIntent.putExtras(bundle);
		return serviceIntent;
	}

}