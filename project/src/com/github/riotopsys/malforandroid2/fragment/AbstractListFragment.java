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

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.adapter.BaseRecordAdapter;
import com.github.riotopsys.malforandroid2.adapter.SupplementaryText.SupplementaryTextFactory;
import com.github.riotopsys.malforandroid2.event.AnimeChangeDetailViewRequest;
import com.github.riotopsys.malforandroid2.event.AnimeUpdateEvent;
import com.github.riotopsys.malforandroid2.event.MangaChangeDetailViewRequest;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import de.greenrobot.event.EventBus;

public abstract class AbstractListFragment extends RoboFragment implements
		LoaderManager.LoaderCallbacks<List<BaseRecord>>, OnItemClickListener {
	
	@SuppressWarnings("unused")
	private static final String TAG = AbstractListFragment.class.getSimpleName();

	@InjectView(R.id.item_list_view)
	protected AbsListView itemListView;
	
	@Inject
	private BaseRecordAdapter animeAdapter;
	
	@Inject 
	private EventBus bus;
	
	@Inject
	private ImageLoader lazyLoader;
	
	protected Loader<List<BaseRecord>> animeLoader;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.item_list_fragment, null); 
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		
		animeAdapter.setSupplementaryTextFactory(getSupplementaryTextFactory());

		itemListView.setAdapter(animeAdapter);

		itemListView.setOnItemClickListener(this);

		PauseOnScrollListener listener = new PauseOnScrollListener(lazyLoader, false, true);
		itemListView.setOnScrollListener(listener);

		animeLoader = getLoaderManager().initLoader(0, null, this);
		animeLoader.forceLoad();

	}
	
	@Override
	public void onPause() {
		bus.unregister(this);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		bus.register(this);
		super.onResume();
	}
	
	public void onEvent( AnimeUpdateEvent aue ){
		if ( animeLoader != null ){
			animeLoader.onContentChanged();
		}
	}
	
	protected abstract SupplementaryTextFactory getSupplementaryTextFactory(); 
	public abstract Loader<List<BaseRecord>> onCreateLoader(int id, Bundle args); 
	
	@Override
	public void onLoadFinished(Loader<List<BaseRecord>> loader,
			List<BaseRecord> data) {
		animeAdapter.addAll(data);
	}

	@Override
	public void onLoaderReset(Loader<List<BaseRecord>> loader) {
		animeAdapter.addAll(null);
	}

	@Override
	public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
		BaseRecord item = (BaseRecord) view.getItemAtPosition(position);
		if ( item instanceof AnimeRecord ){
			bus.post(new AnimeChangeDetailViewRequest(item.id));
		} else {
			bus.post(new MangaChangeDetailViewRequest(item.id));
		}
	}
	
}
