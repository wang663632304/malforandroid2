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
