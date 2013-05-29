package com.github.riotopsys.malforandroid2.server.tasks;

import java.net.HttpURLConnection;
import java.sql.SQLException;

import retrofit.client.Response;
import android.content.Context;
import android.util.Log;

import com.github.riotopsys.malforandroid2.GlobalState;
import com.github.riotopsys.malforandroid2.event.AnimeUpdateEvent;
import com.github.riotopsys.malforandroid2.event.MangaUpdateEvent;
import com.github.riotopsys.malforandroid2.model.AnimeJournalEntry;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.BaseJournalEntry;
import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.github.riotopsys.malforandroid2.model.MangaJournalEntry;
import com.github.riotopsys.malforandroid2.model.MangaRecord;
import com.github.riotopsys.malforandroid2.model.UpdateType;
import com.github.riotopsys.malforandroid2.server.retrofit.AnimeInterconnect;
import com.github.riotopsys.malforandroid2.server.retrofit.MangaInterconnect;
import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

import de.greenrobot.event.EventBus;

public abstract class BaseUpdateTask extends AbstractBackgroundTask {

	private static final String TAG = BaseUpdateTask.class.getSimpleName();
	
	@Inject
	private AnimeInterconnect animeInterconnect;
	
	@Inject 
	private MangaInterconnect mangaInterconnect;
	
	protected BaseRecord record;
	
	protected abstract Dao<BaseJournalEntry, Integer> getJournalDao() throws SQLException;
	protected abstract Dao<BaseRecord, Integer> getRecordDao() throws SQLException;
	
	@Override
	protected void runInBackground(Context ctx, EventBus bus, GlobalState state) {
		try {
			Dao<BaseRecord, Integer> dao = getRecordDao();
			Dao<BaseJournalEntry, Integer> journalDao = getJournalDao();

			dao.update(record);

			Response responce;
			if (record instanceof AnimeRecord) {
				bus.post(new AnimeUpdateEvent(record.id));
				journalUpdate(journalDao, new AnimeJournalEntry(record.id,
						UpdateType.UPDATED));
				AnimeRecord anime = (AnimeRecord) record;
				responce = animeInterconnect.update(generateCredentials(),
						anime.id, anime.watched_status.getServerKey(),
						anime.watched_episodes, anime.score);
			} else {
				bus.post(new MangaUpdateEvent(record.id));
				journalUpdate(journalDao, new MangaJournalEntry(record.id,
						UpdateType.UPDATED));
				MangaRecord manga = (MangaRecord) record;
				responce = mangaInterconnect.update(generateCredentials(),
						manga.id, manga.read_status.getServerKey(),
						manga.chapters_read, manga.volumes_read, manga.score);
			}

			if (responce.getStatus() == HttpURLConnection.HTTP_OK) {
				journalDao.deleteById(record.id);
			}
		} catch (SQLException e) {
			Log.e(TAG, "", e);
		}

	}
	
	protected void journalUpdate(Dao<BaseJournalEntry, Integer> journalDao, BaseJournalEntry journalEntry) throws SQLException {
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

}
