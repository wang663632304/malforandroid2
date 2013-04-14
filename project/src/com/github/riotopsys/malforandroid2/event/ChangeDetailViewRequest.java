package com.github.riotopsys.malforandroid2.event;

import com.github.riotopsys.malforandroid2.model.AnimeRecord;

public class ChangeDetailViewRequest {

	public AnimeRecord record;
	public boolean forceIt = false;

	public ChangeDetailViewRequest(AnimeRecord ar) {
		this.record = ar;
	}

}
