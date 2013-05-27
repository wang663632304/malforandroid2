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

package com.github.riotopsys.malforandroid2.activity;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import roboguice.inject.InjectView;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.github.riotopsys.malforandroid2.GlobalState;
import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.adapter.AnimePagerAdapter;
import com.github.riotopsys.malforandroid2.adapter.MangaPagerAdapter;
import com.github.riotopsys.malforandroid2.database.ReadNameValuePairs;
import com.github.riotopsys.malforandroid2.database.ReadNameValuePairs.Callback;
import com.github.riotopsys.malforandroid2.event.AnimeChangeDetailViewRequest;
import com.github.riotopsys.malforandroid2.event.ListRetreivalStatusEvent;
import com.github.riotopsys.malforandroid2.fragment.AnimeDetailFragment;
import com.github.riotopsys.malforandroid2.fragment.LoginFragment;
import com.github.riotopsys.malforandroid2.fragment.MangaDetailFragment;
import com.github.riotopsys.malforandroid2.fragment.PlacardFragment;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.AnimeWatchedStatus;
import com.github.riotopsys.malforandroid2.model.MangaReadStatus;
import com.github.riotopsys.malforandroid2.model.NameValuePair;
import com.github.riotopsys.malforandroid2.server.AnimeServerInterface;
import com.github.riotopsys.malforandroid2.server.BootReciever;
import com.github.riotopsys.malforandroid2.server.MangaServerInterface;
import com.github.riotopsys.malforandroid2.server.retrofit.AnimeInterconnect;
import com.github.riotopsys.malforandroid2.util.AnimeWatchedStatusTypeAdapter;
import com.github.riotopsys.malforandroid2.util.Apprater;
import com.github.riotopsys.malforandroid2.util.MangaReadStatusTypeAdapter;
import com.github.riotopsys.malforandroid2.view.RefreshProgressActionView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

public class HubActivity extends BaseDetailActivity implements Callback, OnQueryTextListener, OnNavigationListener, OnClickListener {

	@SuppressWarnings("unused")
	private static final int MANGA_POSITION = 1;
	private static final int ANIME_POSITION = 0;

	private static String TAG = HubActivity.class.getSimpleName();

	@InjectView(R.id.list_pager_anime)
	private ViewPager animeListPager;
	
	@InjectView(R.id.list_pager_manga)
	private ViewPager mangaListPager;

	@Inject
	private AnimePagerAdapter animeAdapter;
	
	@Inject
	private MangaPagerAdapter mangaAdapter;
	
	@Inject 
	private GlobalState state;
	
	@Inject
	private LoginFragment login;
	
	@Inject
	private Apprater apprater;
	
	private SearchView searchView;
	private MenuItem searchItem;
	private RefreshProgressActionView refreshView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.main);
		
		ActionBar actionBar = getActionBar();
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

	    actionBar.setListNavigationCallbacks(
	            new ArrayAdapter<String>(
	                    actionBar.getThemedContext(),
	                    android.R.layout.simple_list_item_1,
	                    android.R.id.text1,
	                    getResources().getStringArray(R.array.drop_down_nav_options)),
	            this
	            );

		animeListPager.setAdapter(animeAdapter);
		animeListPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.standard_padding));
		
		mangaListPager.setAdapter(mangaAdapter);
		mangaListPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.standard_padding));
		
		
		new ReadNameValuePairs(getHelper(), this).execute("USER","PASS");
		
		if ( !state.isSyncScheduled()){
			//somehow we hit this point with out starting the sync, so we'll do it now
			BootReciever.scheduleSync(state, this);
		}
		
		if ( detailFrame != null ){
			transitionDetail( false );
		}
		
		if ( savedInstanceState != null){
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt("MODE"));
		}
		
		apprater.onAppOpened(this);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new GsonBuilder()
						.registerTypeAdapter(AnimeWatchedStatus.class,
								new AnimeWatchedStatusTypeAdapter())
						.registerTypeAdapter(MangaReadStatus.class,
								new MangaReadStatusTypeAdapter()).create();

				RestAdapter restAdapter = new RestAdapter.Builder()
						.setServer("http://mal-api.com")
						.setConverter(new GsonConverter(gson)).build();

				// Create an instance of our GitHub API interface.
				AnimeInterconnect ai = restAdapter
						.create(AnimeInterconnect.class);
				
				List<AnimeRecord> out = ai.search("ah my goddess");
				for ( AnimeRecord ar : out ){
					Log.i("nadeshiko", ar.title);
				}

			}
		}).start();
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		outState.putInt("MODE", getActionBar().getSelectedNavigationIndex());
		
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.base, menu);
		
		searchItem =  menu.findItem(R.id.menu_search);
		
		searchView = (SearchView) searchItem.getActionView();
		searchView.setOnQueryTextListener(this);
		searchView.setQueryHint(getString(R.string.search));
		
		MenuItem refreshItem = menu.findItem(R.id.refresh_menu_item);
		refreshView = (RefreshProgressActionView)refreshItem.getActionView();
		refreshView.setOnClickListener(this);
		refreshView.setBusy(state.isBusy());
		
		return super.onCreateOptionsMenu(menu);
	}
	
	public void onEventMainThread( ListRetreivalStatusEvent lsre ){
		if ( refreshView != null ){
			refreshView.setBusy(state.isBusy());
		}
	}
	
	@Override
	public void onNameValuePairsReady(List<NameValuePair> data) {
		for (NameValuePair pair : data) {
			if ("USER".equals(pair.name)) {
				state.setUser((String) pair.value);
			}
			if ("PASS".equals(pair.name)) {
				state.setPass((String) pair.value);
			}
		}
		if ( !state.loginSet() ){
			login.show(getSupportFragmentManager(), null);
		}
		
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		searchItem.collapseActionView();
		if ( getActionBar().getSelectedNavigationIndex() == ANIME_POSITION ){
			AnimeServerInterface.searchAnime(this, query);
			animeListPager.setCurrentItem(animeAdapter.getCount()-1,true);
		} else {
			MangaServerInterface.searchManga(this, query);
			mangaListPager.setCurrentItem(mangaAdapter.getCount()-1,true);
		}
		return true;
	}
	
	protected void transitionDetail( boolean wasBack) {
		if ( detailFrame != null ){
			transitionDetailTofragment( wasBack );
		} else {
			transitionDetailToActivity();
		}
	}
	
	private void transitionDetailToActivity() {
		Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra("ITEM", currentDetail);
		startActivity(intent);
		purgeFakeBackStack();
	}

	private void transitionDetailTofragment(boolean wasBack) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		
		setAnimations(wasBack, transaction);
		
		Fragment fragment;
		if (currentDetail != null) {
			if ( currentDetail instanceof AnimeChangeDetailViewRequest){
				fragment = new AnimeDetailFragment();
			} else {
				fragment = new MangaDetailFragment();
			}
			Bundle args = new Bundle();
			args.putInt("id", currentDetail.id);
			fragment.setArguments(args);
		} else {
			fragment = new PlacardFragment();
		}
		transaction.replace(R.id.detail_frame, fragment);
		transaction.commit();
	}

	@Override
	public boolean onNavigationItemSelected(int position, long arg1) {
		if (position == ANIME_POSITION){
			animeListPager.setVisibility(ViewPager.VISIBLE);
			mangaListPager.setVisibility(ViewPager.GONE);
		} else {
			animeListPager.setVisibility(ViewPager.GONE);
			mangaListPager.setVisibility(ViewPager.VISIBLE);
		}
		return true;
	}

	@Override
	public void onClick(View view) {
		if ( !state.loginSet() ){
			login.show(getSupportFragmentManager(), null);
		} else {
			AnimeServerInterface.getAnimeList(this);
			MangaServerInterface.getMangaList(this);
			
			refreshView.setBusy(true);
		}
		
	}

}
