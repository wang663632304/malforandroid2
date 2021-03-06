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
import java.util.LinkedList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.github.riotopsys.malforandroid2.model.NameValuePair;
import com.j256.ormlite.dao.Dao;

public class ReadNameValuePairs extends AsyncTask<String, Void, List<NameValuePair>> {
	
	private static final String TAG = ReadNameValuePairs.class.getSimpleName();
	
	public interface Callback{
		public void onNameValuePairsReady(List<NameValuePair> data );
	}
	
	private Callback callback = null;
	private DatabaseHelper dbHelper = null;
	
	public ReadNameValuePairs(DatabaseHelper dbHelper,  Callback callback ) {
		this.callback = callback;
		this.dbHelper = dbHelper;
	}

	@Override
	protected List<NameValuePair> doInBackground(String... params) {
		List<NameValuePair> result = new LinkedList<NameValuePair>();
		try {
			Dao<NameValuePair, String> dao = dbHelper.getDao(NameValuePair.class);
			for (String name : params) {
				try {
					NameValuePair nvp = dao.queryForId(name);
					if ( nvp != null ){
						result.add(nvp);
					}
				} catch (SQLException e) {
					Log.e(TAG, "cannot read item", e);
				}
			}
		} catch (SQLException e) {
			Log.e(TAG, "cannot create dao", e);
		}
		dbHelper = null;
		return result;
	}
	
	@Override
	protected void onPostExecute(List<NameValuePair> result) {
		super.onPostExecute(result);
		if ( callback != null ){
			callback.onNameValuePairsReady(result);
		}
		callback = null;
		result = null;
	}

}
