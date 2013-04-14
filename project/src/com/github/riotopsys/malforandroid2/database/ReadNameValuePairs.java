package com.github.riotopsys.malforandroid2.database;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.github.riotopsys.malforandroid2.model.NameValuePair;
import com.j256.ormlite.dao.Dao;

public class ReadNameValuePairs<T extends Serializable> extends AsyncTask<String, Void, List<NameValuePair<T>>> {
	
	private static final String TAG = ReadNameValuePairs.class.getSimpleName();
	
	public interface Callback<T extends Serializable>{
		public void onNameValuePairsReady( List<NameValuePair<T>> data );
	}
	
	private Callback<T> callback = null;
	private DatabaseHelper dbHelper = null;
	
	public ReadNameValuePairs(DatabaseHelper dbHelper,  Callback<T> callback ) {
		this.callback = callback;
		this.dbHelper = dbHelper;
	}

	@Override
	protected List<NameValuePair<T>> doInBackground(String... params) {
		List<NameValuePair<T>> result = new LinkedList<NameValuePair<T>>();
		try {
			Dao<NameValuePair<T>, String> dao = dbHelper.getDao(NameValuePair.class);
			for (String name : params) {
				try {
					NameValuePair<T> nvp = dao.queryForId(name);
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
	protected void onPostExecute(List<NameValuePair<T>> result) {
		super.onPostExecute(result);
		if ( callback != null ){
			callback.onNameValuePairsReady(result);
		}
		callback = null;
		result = null;
	}

}
