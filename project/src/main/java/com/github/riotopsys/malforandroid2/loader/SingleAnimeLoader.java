package com.github.riotopsys.malforandroid2.loader;

import java.sql.SQLException;

import android.content.Context;
import android.util.Log;

import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.j256.ormlite.dao.Dao;

public class SingleAnimeLoader extends DBLoader<AnimeRecord> {

	private static final String TAG = SingleAnimeLoader.class.getSimpleName();
	private int id;

	public SingleAnimeLoader(Context context, int id) {
		super(context);
		this.id = id;
	}

	@Override
	public AnimeRecord loadInBackground() {
		try {
			Dao<AnimeRecord, Integer> dao = getHelper().getDao(AnimeRecord.class);
			
			return dao.queryForId(id);
		} catch (SQLException e) {
			Log.e(TAG, "", e);
		}
		return null;
	}

}
