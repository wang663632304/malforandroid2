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
import android.widget.ListView;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.adapter.AnimeAdapter;
import com.github.riotopsys.malforandroid2.event.AnimeUpdateEvent;
import com.github.riotopsys.malforandroid2.loader.AnimeLoader;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.google.inject.Inject;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class ItemListFragment extends RoboFragment implements LoaderManager.LoaderCallbacks<List<AnimeRecord>> {
	
	@InjectView(R.id.item_list_view)
	private ListView itemListView;
	
	@Inject
	private AnimeAdapter animeAdapter;
	
	@Inject 
	private Bus bus;
	
	private Loader<List<AnimeRecord>> animeLoader;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.item_list_fragment, null); 
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		itemListView.setAdapter(animeAdapter);
		
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
	
	@Subscribe
	public void updateReady( AnimeUpdateEvent aue ){
		if ( animeLoader != null ){
			animeLoader.onContentChanged();
		}
	}
	
	@Override
	public Loader<List<AnimeRecord>> onCreateLoader(int id, Bundle args) {
		AnimeLoader loader = new AnimeLoader(getActivity());
//		loader.setUpdateThrottle(1000/24);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<AnimeRecord>> loader,
			List<AnimeRecord> data) {
		animeAdapter.addAll(data);
	}

	@Override
	public void onLoaderReset(Loader<List<AnimeRecord>> loader) {
		animeAdapter.addAll(null);
	}
	
}
