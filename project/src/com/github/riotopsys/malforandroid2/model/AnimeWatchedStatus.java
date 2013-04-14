package com.github.riotopsys.malforandroid2.model;

import com.github.riotopsys.malforandroid2.R;

public enum AnimeWatchedStatus {

	WATCHING(R.string.watched_status_watching,  "watching"),
	COMPLETED( R.string.watched_status_compleated,  "completed" ),
	ONHOLD( R.string.watched_status_onhold , "on-hold"),
	DROPPED( R.string.watched_status_dropped,  "dropped" ),
	PLAN( R.string.watched_status_plan, "plan to watch");

	private final int resource;
	private final String serverKey;

	private AnimeWatchedStatus(int number, String serverKey) {
		this.resource = number;
		this.serverKey = serverKey;
	}

	public int getResource() {
		return resource;
	}
	
	public static AnimeWatchedStatus getByServerKey(String key){
		for ( AnimeWatchedStatus aws : values() ){
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
