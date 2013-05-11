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

import com.github.riotopsys.malforandroid2.event.AnimeUpdateEvent;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.server.ServerInterface;
import com.j256.ormlite.dao.Dao;

import de.greenrobot.event.EventBus;

public class DBUpdateTask extends AsyncTask<AnimeRecord, Void, Void> {

	private static final String TAG = DBUpdateTask.class.getSimpleName();

	private DatabaseHelper dbHelper;

	private Context ctx;

	private EventBus bus;

	private boolean isAdd = false;

	public DBUpdateTask(DatabaseHelper dbHelper, Context ctx, EventBus bus) {
		this.dbHelper = dbHelper;
		this.ctx = ctx;
		this.bus = bus;
	}
	
	public DBUpdateTask(DatabaseHelper dbHelper, Context ctx, EventBus bus, boolean isAdd) {
		this.dbHelper = dbHelper;
		this.ctx = ctx;
		this.bus = bus;
		this.isAdd = isAdd;
	}

	@Override
	protected Void doInBackground(AnimeRecord... params) {
		try {
			Dao<AnimeRecord, Integer> dao = dbHelper.getDao(AnimeRecord.class);
			for (AnimeRecord p : params) {
				try {
					dao.createOrUpdate(p);
					if ( isAdd  ){
						ServerInterface.addAnimeRecord(ctx, p.id);
					} else {
						ServerInterface.updateAnimeRecord(ctx, p.id);
					}
					bus.post(new AnimeUpdateEvent(p.id));
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
