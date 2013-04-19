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

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.github.riotopsys.malforandroid2.database.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public abstract class DBLoader<D> extends AsyncTaskLoader<D> {

	private DatabaseHelper databaseHelper;

	public DBLoader(Context context) {
		super(context);
	}
	
	@Override
	public void reset() {
	    super.reset();
	    if (databaseHelper != null) {
	        OpenHelperManager.releaseHelper();
	        databaseHelper = null;
	    }
	}

	protected DatabaseHelper getHelper() {
	    if (databaseHelper == null) {
	        databaseHelper =
	            OpenHelperManager.getHelper(this.getContext(), DatabaseHelper.class);
	    }
	    return databaseHelper;
	}
}
