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

import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.github.riotopsys.malforandroid2.model.MangaReadStatus;
import com.github.riotopsys.malforandroid2.model.MangaRecord;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;

public class MangaLoader extends DBLoader<List<BaseRecord>> {

	private static final String TAG = MangaLoader.class.getSimpleName();
	private MangaReadStatus filter;

	public MangaLoader(Context context, MangaReadStatus filter) {
		super(context);
		this.filter = filter;
	}

	@Override
	public List<BaseRecord> loadInBackground() {
		try {
			Dao<MangaRecord, Integer> dao = getHelper().getDao(MangaRecord.class);
			Log.i(TAG, String.format("count of manga %d", dao.countOf()));
			Where<MangaRecord, Integer> where = dao.queryBuilder().where().isNotNull("read_status");
			if ( filter != null ){
				where.and().eq("read_status", filter);
			} 
			return new LinkedList<BaseRecord>(where.query());
		} catch (SQLException e) {
			Log.e(TAG, "", e);
		}
		return null;
	}

}
