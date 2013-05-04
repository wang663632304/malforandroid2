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

import com.github.riotopsys.malforandroid2.GlobalState;
import com.github.riotopsys.malforandroid2.adapter.SupplementaryText.SupplementaryTextFactory;
import com.github.riotopsys.malforandroid2.adapter.SupplementaryText.WatchedStatusText;
import com.github.riotopsys.malforandroid2.event.AnimeSearchUpdated;
import com.github.riotopsys.malforandroid2.loader.SearchLoader;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.google.inject.Inject;

public class SearchListFragment extends AbstractListFragment  {
	
	@Inject GlobalState state;
	
	@Override
	public Loader<List<AnimeRecord>> onCreateLoader(int id, Bundle args) {
		SearchLoader loader = new SearchLoader(getActivity(), state );
//		loader.setUpdateThrottle(1000/24);
		return loader;
	}
	
	public void onEventMainThread( AnimeSearchUpdated asu ){
		if ( animeLoader != null ){
			itemListView.setSelection(0);
			animeLoader.onContentChanged();
		}
	}

	@Override
	protected SupplementaryTextFactory getSupplementaryTextFactory() {
		return new WatchedStatusText();
	}
	
}
