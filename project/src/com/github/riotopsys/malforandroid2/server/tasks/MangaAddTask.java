package com.github.riotopsys.malforandroid2.server.tasks;

import java.sql.SQLException;

import retrofit.client.Response;

import com.github.riotopsys.malforandroid2.event.MangaUpdateEvent;
import com.github.riotopsys.malforandroid2.model.BaseJournalEntry;
import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.github.riotopsys.malforandroid2.model.MangaJournalEntry;
import com.github.riotopsys.malforandroid2.model.MangaRecord;
import com.github.riotopsys.malforandroid2.model.UpdateType;
import com.github.riotopsys.malforandroid2.server.retrofit.MangaInterconnect;
import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

public class MangaAddTask extends AbstractJournalizedTask {
	
	@Inject
	private MangaInterconnect mangaInterconnect;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Dao<BaseJournalEntry, Integer> getJournalDao() throws SQLException {
		return (Dao)getHelper().getDao(MangaJournalEntry.class);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Dao<BaseRecord, Integer> getRecordDao() throws SQLException {
		return (Dao)getHelper().getDao(MangaRecord.class);
	}

	public MangaAddTask setRecord(MangaRecord record){
		this.record = record;
		return this;
	}
	
	@Override
	protected Response doServerSideAction( Dao<BaseJournalEntry, Integer> journalDao) throws SQLException {
		bus.post(new MangaUpdateEvent(record.id));
		journalUpdate(journalDao, new MangaJournalEntry(record.id,
				UpdateType.ADD_TO_LIST));
		MangaRecord manga = (MangaRecord) record;
		return mangaInterconnect.add(generateCredentials(),
				manga.id, manga.read_status.getServerKey(),
				manga.chapters_read, manga.volumes_read, manga.score);
	}
	
}
