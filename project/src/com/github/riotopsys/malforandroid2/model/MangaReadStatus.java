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

package com.github.riotopsys.malforandroid2.model;

import com.github.riotopsys.malforandroid2.R;

public enum MangaReadStatus {

	READING(R.string.read_status_watching,  "reading"),
	COMPLETED( R.string.watched_status_compleated,  "completed" ),
	ONHOLD( R.string.watched_status_onhold , "on-hold"),
	DROPPED( R.string.watched_status_dropped,  "dropped" ),
	PLAN( R.string.read_status_plan, "plan to read");

	private final int resource;
	private final String serverKey;

	private MangaReadStatus(int number, String serverKey) {
		this.resource = number;
		this.serverKey = serverKey;
	}

	public int getResource() {
		return resource;
	}
	
	public static MangaReadStatus getByServerKey(String key){
		for ( MangaReadStatus aws : values() ){
			if ( aws.serverKey.equals(key)){
				return aws;
			}
		}
		throw new IllegalArgumentException();
	}

	public String getServerKey() {
		return serverKey;
	}
}
