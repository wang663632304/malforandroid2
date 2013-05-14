package com.github.riotopsys.malforandroid2.server;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.github.riotopsys.malforandroid2.model.AnimeJournalEntry;
import com.github.riotopsys.malforandroid2.model.AnimeListResponse;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.BaseJournalEntry;
import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.j256.ormlite.dao.Dao;

public class AnimeServerInterface extends AbstractServerInterface {

	@Override
	protected Class<?> getRecordClass() {
		return AnimeRecord.class;
	}

	@Override
	protected Dao<BaseRecord, Integer> getRecordDao() throws SQLException {
		return getHelper().getDao(AnimeRecord.class);
	}

	@Override
	protected Dao<BaseJournalEntry, Integer> getJournalDao() throws SQLException {
		return getHelper().getDao(AnimeJournalEntry.class);
	}

	@Override
	protected List<?> processListResponse(String raw) {
		return gson.fromJson(raw, AnimeListResponse.class).anime;
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
