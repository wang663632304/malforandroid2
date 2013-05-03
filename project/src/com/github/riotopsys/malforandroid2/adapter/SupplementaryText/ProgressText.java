package com.github.riotopsys.malforandroid2.adapter.SupplementaryText;

import android.content.Context;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;

public class ProgressText implements SupplementaryTextFactory {

	@Override
	public String getSupplementaryText(Context ctx, AnimeRecord ar) {
		return ctx.getString(R.string.watched_format, ar.watched_episodes, ar.episodes);
	}

}
