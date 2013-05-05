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

package com.github.riotopsys.malforandroid2;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;

public class GlobalState {
	
	private static final String TAG = GlobalState.class.getSimpleName();
	
	private String user = null;
	private String pass = null;
	
	private boolean syncScheduled = false;
	
	private LinkedList<Integer> searchResults = new LinkedList<Integer>();
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		Log.v(TAG, String.format("User is: '%s'!", user));
		this.user = user;
	}
	
	public String getPass() {
		return pass;
	}
	
	public void setPass(String pass) {
		Log.v(TAG, "password is set!");
		this.pass = pass;
	}

	public boolean loginSet() {
		return ( user != null && pass != null);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getSearchResults() {
		synchronized (searchResults) {
			return (List<Integer>) searchResults.clone();			
		}
	}
	
	public void setSearchResults(List<Integer> ids) {
		if ( ids == null ){
			return;
		}
		synchronized (ids) {
			this.searchResults.clear();
			this.searchResults.addAll(ids);
		}
	}

	public boolean isSyncScheduled() {
		return syncScheduled;
	}

	public void setSyncScheduled(boolean syncScheduled) {
		this.syncScheduled = syncScheduled;
	}
	
}
