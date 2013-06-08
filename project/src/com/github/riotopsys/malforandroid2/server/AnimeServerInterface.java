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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.XML;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.riotopsys.malforandroid2.model.AnimeJournalEntry;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.j256.ormlite.dao.Dao;

public class AnimeServerInterface extends AbstractServerInterface {
	
	@Override
	protected Class<?> getRecordClass() {
		return AnimeRecord.class;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Dao getRecordDao() throws SQLException {
		return getHelper().getDao(AnimeRecord.class);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Dao getJournalDao() throws SQLException {
		return getHelper().getDao(AnimeJournalEntry.class);
	}

	@Override
	protected List<?> processListResponse(String raw) {
		try {
			
			String s = XML.toJSONObject(raw).toString();
			
			Log.i("bob", s);
			
			List<AnimeRecord> result = new ArrayList<AnimeRecord>();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void partialUpdate(BaseRecord original, BaseRecord incomming) {
		AnimeRecord o = (AnimeRecord) original;
		AnimeRecord i = (AnimeRecord) incomming;
		o.watched_status = i.watched_status;
		o.score = i.score;
		o.watched_episodes = i.watched_episodes;
	}

	@Override
	protected void removeFromList(BaseRecord original) {
		AnimeRecord o = (AnimeRecord) original;
		o.watched_status = null;
		o.score = 0;
		o.watched_episodes = 0;
	}
	
	private static Intent getBaseIntent(Context ctx) {
		return new Intent(ctx, AnimeServerInterface.class);
	}
	
	public static Intent getSyncIntent(Context ctx) {
		return getSyncIntent(ctx, getBaseIntent(ctx));
	}

	public static void getAnimeList(Context ctx) {
		getList(ctx, getBaseIntent(ctx));
	}

	public static void verifyCredentials(Context ctx) {
		verifyCredentials(ctx, getBaseIntent(ctx));
	}

	public static void getAnimeRecord(Context ctx, int id) {
		getRecord(ctx, id, getBaseIntent(ctx) );
	}

	public static void addAnimeRecord(Context ctx, int id) {
		addRecord(ctx, id, getBaseIntent(ctx));
	}

	public static void updateAnimeRecord(Context ctx, int id) {
		updateRecord(ctx, id, getBaseIntent(ctx));
	}

	public static void removeAnimeRecord(Context ctx, int id) {
		removeRecord(ctx, id, getBaseIntent(ctx));
	}

	public static void searchAnime(Context ctx, String query) {
		search(ctx, query, getBaseIntent(ctx));
	}

}
