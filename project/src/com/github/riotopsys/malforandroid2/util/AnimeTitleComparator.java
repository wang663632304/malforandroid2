package com.github.riotopsys.malforandroid2.util;

import java.util.Comparator;

import com.github.riotopsys.malforandroid2.model.AnimeRecord;

public class AnimeTitleComparator implements Comparator<AnimeRecord> {

	@Override
	public int compare(AnimeRecord lhs, AnimeRecord rhs) {
		return lhs.title.compareTo(rhs.title);
	}

}
