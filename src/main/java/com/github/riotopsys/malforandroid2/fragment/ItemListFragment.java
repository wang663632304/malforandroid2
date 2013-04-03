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
import com.github.riotopsys.malforandroid2.adapter.AnimeAdapter;
import com.github.riotopsys.malforandroid2.event.AnimeUpdateEvent;
import com.github.riotopsys.malforandroid2.event.ChangeDetailViewRequest;
import com.github.riotopsys.malforandroid2.loader.AnimeLoader;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.google.inject.Inject;

import de.greenrobot.event.EventBus;

public class ItemListFragment extends RoboFragment implements
		LoaderManager.LoaderCallbacks<List<AnimeRecord>>, OnItemClickListener {
	
	@InjectView(R.id.item_list_view)
	private AbsListView itemListView;
	
	@Inject
	private AnimeAdapter animeAdapter;
	
	@Inject 
	private EventBus bus;
	
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
		
		itemListView.setOnItemClickListener(this);
		
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
	
	@Override
	public Loader<List<AnimeRecord>> onCreateLoader(int id, Bundle args) {
		AnimeLoader loader = new AnimeLoader(getActivity());
//		loader.setUpdateThrottle(1000/24);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<AnimeRecord>> loader,
			List<AnimeRecord> data) {
		if ( animeAdapter.isEmpty()){
			if ( !data.isEmpty() ){
				ChangeDetailViewRequest cdvr = new ChangeDetailViewRequest(data.get(0));
				cdvr.forceIt = true;
				bus.post(cdvr);
			}
		}
		animeAdapter.addAll(data);
	}

	@Override
	public void onLoaderReset(Loader<List<AnimeRecord>> loader) {
		animeAdapter.addAll(null);
	}

	@Override
	public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
		AnimeRecord ar = (AnimeRecord) view.getItemAtPosition(position);
		bus.post(new ChangeDetailViewRequest(ar));
		
	}
	
}
