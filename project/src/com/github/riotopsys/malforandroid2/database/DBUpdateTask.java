/**
 * Copyright 2013 C. A. Fitzgerald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.github.riotopsys.malforandroid2.database;

import java.sql.SQLException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.server.ServerInterface;
import com.j256.ormlite.dao.Dao;

public class DBUpdateTask extends AsyncTask<AnimeRecord, Void, Void> {

	private static final String TAG = DBUpdateTask.class.getSimpleName();

	private DatabaseHelper dbHelper;

	private Context ctx;

	public DBUpdateTask(DatabaseHelper dbHelper, Context ctx) {
		this.dbHelper = dbHelper;
		this.ctx = ctx;
	}

	@Override
	protected Void doInBackground(AnimeRecord... params) {
		try {
			Dao<AnimeRecord, ?> dao = dbHelper.getDao(params[0].getClass());
			for (AnimeRecord p : params) {
				try {
					dao.createOrUpdate(p);
					ServerInterface.updateAnimeRecord(ctx, p.id);
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
