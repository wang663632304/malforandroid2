package com.github.riotopsys.malforandroid2.database;

import java.sql.SQLException;

import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

public class DBUpdateTask<T extends Object> extends AsyncTask<T, Void, Void> {

	private static final String TAG = DBUpdateTask.class.getSimpleName();

	private DatabaseHelper dbHelper;

	public DBUpdateTask(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	@Override
	protected Void doInBackground(T... params) {
		try {
			Dao<T, ?> dao = dbHelper.getDao(params[0].getClass());
			for (T p : params) {
				try {
					dao.createOrUpdate(p);
				} catch (SQLException e) {
					Log.e(TAG, "cannot save item", e);
				}
			}
		} catch (SQLException e) {
			Log.e(TAG, "cannot create dao", e);
		}
		dbHelper = null;
		return null;
	}

}
