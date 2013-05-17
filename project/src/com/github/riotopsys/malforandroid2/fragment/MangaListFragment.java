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

package com.github.riotopsys.malforandroid2.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.content.Loader;

import com.github.riotopsys.malforandroid2.adapter.SupplementaryText.ProgressText;
import com.github.riotopsys.malforandroid2.adapter.SupplementaryText.RankText;
import com.github.riotopsys.malforandroid2.adapter.SupplementaryText.ScoreText;
import com.github.riotopsys.malforandroid2.adapter.SupplementaryText.SupplementaryTextFactory;
import com.github.riotopsys.malforandroid2.adapter.SupplementaryText.WatchedStatusText;
import com.github.riotopsys.malforandroid2.loader.MangaLoader;
import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.github.riotopsys.malforandroid2.model.MangaReadStatus;

public class MangaListFragment extends AbstractListFragment  {
	
	@Override
	public Loader<List<BaseRecord>> onCreateLoader(int id, Bundle args) {
		MangaLoader loader = new MangaLoader(getActivity(), (MangaReadStatus)getArguments().getSerializable("filter"));
//		loader.setUpdateThrottle(1000/24);
		return loader;
	}

	@Override
	protected SupplementaryTextFactory getSupplementaryTextFactory() {
		MangaReadStatus filter = (MangaReadStatus)getArguments().getSerializable("filter");
		if ( filter == null ){
			return new WatchedStatusText();
		}
		
		switch( filter ){
		case COMPLETED:
			return new ScoreText();
		case DROPPED:
			return new ProgressText();
		case ONHOLD:
			return new ProgressText();
		case PLAN:
			return new RankText();
		case READING:
			return new WatchedStatusText();
		default:
			return new WatchedStatusText();
		}
		
	}
	
}
