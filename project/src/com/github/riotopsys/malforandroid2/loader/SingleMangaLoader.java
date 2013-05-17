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

import android.content.Context;
import android.util.Log;

import com.github.riotopsys.malforandroid2.model.MangaRecord;
import com.j256.ormlite.dao.Dao;

public class SingleMangaLoader extends DBLoader<MangaRecord> {

	private static final String TAG = SingleMangaLoader.class.getSimpleName();
	private int id;

	public SingleMangaLoader(Context context, int id) {
		super(context);
		this.id = id;
	}

	@Override
	public MangaRecord loadInBackground() {
		try {
			Dao<MangaRecord, Integer> dao = getHelper().getDao(MangaRecord.class);
			
			return dao.queryForId(id);
		} catch (SQLException e) {
			Log.e(TAG, "", e);
		}
		return null;
	}

}
