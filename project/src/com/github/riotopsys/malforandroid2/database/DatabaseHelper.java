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
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.JournalEntry;
import com.github.riotopsys.malforandroid2.model.NameValuePair;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String TAG = DatabaseHelper.class.getSimpleName();
	private static final String DB_NAME = "mal2.db";
	private static final int DB_VERSION = 1;
	
	@SuppressWarnings("rawtypes")
	private static final List<Class> STORED_MODLES = Arrays.asList( (Class)AnimeRecord.class, (Class)NameValuePair.class, (Class)JournalEntry.class );

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			for ( Class<?> clazz : STORED_MODLES){
				TableUtils.createTable(connectionSource, clazz);
			}
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int from,
			int to) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			for ( Class<?> clazz : STORED_MODLES){
				TableUtils.dropTable(connectionSource, clazz, true);
			}
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz)
			throws SQLException {
		if ( !STORED_MODLES.contains(clazz) ){
			throw new SQLException(String.format("Class '%s' has not been enabled for storage in %s", clazz.getName(), TAG));
		}
		return super.getDao(clazz);
	}
	
}
