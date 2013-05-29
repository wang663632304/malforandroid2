package com.github.riotopsys.malforandroid2.server.tasks;

import java.sql.SQLException;

import com.github.riotopsys.malforandroid2.model.AnimeJournalEntry;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.BaseJournalEntry;
import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.j256.ormlite.dao.Dao;

public class AnimeUpdateTask extends BaseUpdateTask {

	@Override
	protected Dao<BaseJournalEntry, Integer> getJournalDao() throws SQLException {
		return getHelper().getDao(AnimeJournalEntry.class);
	}

	@Override
	protected Dao<BaseRecord, Integer> getRecordDao() throws SQLException {
		return getHelper().getDao(AnimeRecord.class);
	}
	
	public AnimeUpdateTask setRecord(AnimeRecord record){
		this.record = record;
		return this;
	}

}
