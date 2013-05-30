package com.github.riotopsys.malforandroid2.server.tasks;

import java.sql.SQLException;

import retrofit.client.Response;

import com.github.riotopsys.malforandroid2.event.AnimeUpdateEvent;
import com.github.riotopsys.malforandroid2.model.AnimeJournalEntry;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.BaseJournalEntry;
import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.github.riotopsys.malforandroid2.model.UpdateType;
import com.github.riotopsys.malforandroid2.server.retrofit.AnimeInterconnect;
import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

public class AnimeAddTask extends AbstractJournalizedTask {

	@Inject
	private AnimeInterconnect animeInterconnect;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Dao<BaseJournalEntry, Integer> getJournalDao()
			throws SQLException {
		return (Dao)getHelper().getDao(AnimeJournalEntry.class);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Dao<BaseRecord, Integer> getRecordDao() throws SQLException {
		return (Dao)getHelper().getDao(AnimeRecord.class);
	}

	public AnimeAddTask setRecord(AnimeRecord record) {
		this.record = record;
		return this;
	}

	@Override
	protected Response doServerSideAction( Dao<BaseJournalEntry, Integer> journalDao) throws SQLException {
		bus.post(new AnimeUpdateEvent(record.id));
		journalUpdate(journalDao, new AnimeJournalEntry(record.id,
				UpdateType.ADD_TO_LIST));
		AnimeRecord anime = (AnimeRecord) record;
		return animeInterconnect.add(generateCredentials(), anime.id,
				anime.watched_status.getServerKey(), anime.watched_episodes,
				anime.score);
	}

}
