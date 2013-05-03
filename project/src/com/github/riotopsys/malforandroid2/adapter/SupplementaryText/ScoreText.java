package com.github.riotopsys.malforandroid2.adapter.SupplementaryText;

import android.content.Context;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;

public class ScoreText implements SupplementaryTextFactory {

	@Override
	public String getSupplementaryText(Context ctx, AnimeRecord ar) {
		return ctx.getString(R.string.score_format, ar.score );
	}

}
