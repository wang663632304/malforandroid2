package com.github.riotopsys.malforandroid2.adapter.SupplementaryText;

import android.content.Context;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.AnimeWatchedStatus;

public class WatchedStatusText implements SupplementaryTextFactory {

	@Override
	public String getSupplementaryText(Context ctx, AnimeRecord ar) {
		if ( ar.watched_status == null ){
			return ctx.getString(R.string.not_in_list);
		}
		if ( ar.watched_status == AnimeWatchedStatus.WATCHING ){
			return ctx.getString(R.string.watched_format, ar.watched_episodes, ar.episodes);			
		}
		return ctx.getString( ar.watched_status.getResource() );
	}

}
