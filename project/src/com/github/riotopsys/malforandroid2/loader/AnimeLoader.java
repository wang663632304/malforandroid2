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

package com.github.riotopsys.malforandroid2.loader;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.AnimeWatchedStatus;
import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;

public class AnimeLoader extends DBLoader<List<BaseRecord>> {

	private static final String TAG = AnimeLoader.class.getSimpleName();
	private AnimeWatchedStatus filter;

	public AnimeLoader(Context context, AnimeWatchedStatus filter) {
		super(context);
		this.filter = filter;
	}

	@Override
	public List<BaseRecord> loadInBackground() {
		try {
			Dao<AnimeRecord, Integer> dao = getHelper().getDao(AnimeRecord.class);
			Where<AnimeRecord, Integer> where = dao.queryBuilder().where().isNotNull("watched_status");
			if ( filter != null ){
				where.and().eq("watched_status", filter);
			} 
			List<AnimeRecord> items = where.query();
			Log.i(TAG, String.format("item count %d: filter: %s", items.size(), (filter != null) ? filter.getServerKey() : "NULL" ));
			return new LinkedList<BaseRecord>(items);
		} catch (SQLException e) {
			Log.e(TAG, "", e);
		}
		return null;
	}

}
