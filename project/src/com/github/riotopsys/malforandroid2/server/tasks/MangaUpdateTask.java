package com.github.riotopsys.malforandroid2.server.tasks;

import java.sql.SQLException;

import com.github.riotopsys.malforandroid2.model.BaseJournalEntry;
import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.github.riotopsys.malforandroid2.model.MangaJournalEntry;
import com.github.riotopsys.malforandroid2.model.MangaRecord;
import com.j256.ormlite.dao.Dao;

public class MangaUpdateTask extends BaseUpdateTask {

	@Override
	protected Dao<BaseJournalEntry, Integer> getJournalDao() throws SQLException {
		return getHelper().getDao(MangaJournalEntry.class);
	}

	@Override
	protected Dao<BaseRecord, Integer> getRecordDao() throws SQLException {
		return getHelper().getDao(MangaRecord.class);
	}

	public MangaUpdateTask setRecord(MangaRecord record){
		this.record = record;
		return this;
	}
}
