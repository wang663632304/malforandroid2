package com.github.riotopsys.malforandroid2.adapter.SupplementaryText;

import android.content.Context;

import com.github.riotopsys.malforandroid2.model.AnimeRecord;

public interface SupplementaryTextFactory {

	public String getSupplementaryText(Context ctx, AnimeRecord ar);
	
}
