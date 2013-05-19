package com.github.riotopsys.malforandroid2.server;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.github.riotopsys.malforandroid2.model.BaseJournalEntry;
import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.github.riotopsys.malforandroid2.model.MangaJournalEntry;
import com.github.riotopsys.malforandroid2.model.MangaListResponse;
import com.github.riotopsys.malforandroid2.model.MangaRecord;
import com.j256.ormlite.dao.Dao;

public class MangaServerInterface extends AbstractServerInterface {

	@Override
	protected Class<?> getRecordClass() {
		return MangaRecord.class;
	}

	@Override
	protected Dao<BaseRecord, Integer> getRecordDao() throws SQLException {
		return getHelper().getDao(MangaRecord.class);
	}

	@Override
	protected Dao<BaseJournalEntry, Integer> getJournalDao() throws SQLException {
		return getHelper().getDao(MangaJournalEntry.class);
	}

	@Override
	protected List<?> processListResponse(String raw) {
		return gson.fromJson(raw, MangaListResponse.class).manga;
	}

	@Override
	protected void partialUpdate(BaseRecord original, BaseRecord incomming) {
		MangaRecord o = (MangaRecord) original;
		MangaRecord i = (MangaRecord) incomming;
		o.read_status = i.read_status;
		o.score = i.score;
		o.chapters_read = i.chapters_read;
		o.volumes_read = i.volumes_read;
	}

	@Override
	protected void removeFromList(BaseRecord original) {
		MangaRecord o = (MangaRecord) original;
		o.read_status = null;
		o.score = 0;
		o.chapters_read = 0;
		o.volumes_read = 0;
	}
	
	private static Intent getBaseIntent(Context ctx) {
		return new Intent(ctx, MangaServerInterface.class);
	}
	
	public static Intent getSyncIntent(Context ctx) {
		return getSyncIntent(ctx, getBaseIntent(ctx));
	}

	public static void getMangaList(Context ctx) {
		getList(ctx, getBaseIntent(ctx));
	}

	public static void verifyCredentials(Context ctx) {
		verifyCredentials(ctx, getBaseIntent(ctx));
	}

	public static void getAnimeRecord(Context ctx, int id) {
		getRecord(ctx, id, getBaseIntent(ctx) );
	}

	public static void addMangaRecord(Context ctx, int id) {
		addRecord(ctx, id, getBaseIntent(ctx));
	}

	public static void updateMangaRecord(Context ctx, int id) {
		updateRecord(ctx, id, getBaseIntent(ctx));
	}

	public static void removeMangaRecord(Context ctx, int id) {
		removeRecord(ctx, id, getBaseIntent(ctx));
	}

	public static void searchManga(Context ctx, String query) {
		search(ctx, query, getBaseIntent(ctx));
	}

}
