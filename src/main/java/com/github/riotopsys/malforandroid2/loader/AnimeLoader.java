package com.github.riotopsys.malforandroid2.loader;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.j256.ormlite.dao.Dao;

public class AnimeLoader extends DBLoader<List<AnimeRecord>> {

	private static final String TAG = AnimeLoader.class.getSimpleName();

	public AnimeLoader(Context context) {
		super(context);
	}

	@Override
	public List<AnimeRecord> loadInBackground() {
		try {
			Dao<AnimeRecord, Integer> dao = getHelper().getDao(AnimeRecord.class);
			
			return dao.queryBuilder().where().isNotNull("watched_status").query();
		} catch (SQLException e) {
			Log.e(TAG, "", e);
		}
		return null;
	}

}
