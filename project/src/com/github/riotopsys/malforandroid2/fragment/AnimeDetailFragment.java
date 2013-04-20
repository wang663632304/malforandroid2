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

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.activity.BaseActivity;
import com.github.riotopsys.malforandroid2.database.DBUpdateTask;
import com.github.riotopsys.malforandroid2.event.AnimeUpdateEvent;
import com.github.riotopsys.malforandroid2.loader.SingleAnimeLoader;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.AnimeWatchedStatus;
import com.github.riotopsys.malforandroid2.server.ServerInterface;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

public class AnimeDetailFragment extends RoboFragment implements
		LoaderManager.LoaderCallbacks<AnimeRecord>, OnItemSelectedListener, OnClickListener {

	private static final String TAG = null;

	@InjectView(R.id.title)
	private TextView title;

	@InjectView(R.id.cover_image)
	private ImageView cover;

	@InjectView(R.id.synopsis)
	private TextView synopsys;

	@InjectView(R.id.anime_watched_status)
	private Spinner watchedStatus;

	@InjectView(R.id.anime_score_status)
	private Spinner scoreStatus;

	@InjectView(R.id.watched_count)
	private TextView watchedCount;
	
	@InjectView(R.id.plus_one)
	private Button plusOne;

	@Inject
	private ImageLoader lazyLoader;

	@Inject
	private EventBus bus;

	private int idToDisplay;

	private Loader<AnimeRecord> singleAnimeLoader;

	private AnimeRecord activeRecord;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		idToDisplay = getArguments().getInt("id");
		
		return inflater.inflate(R.layout.anime_detail_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		watchedStatus.setAdapter(ArrayAdapter.createFromResource(getActivity(),
				R.array.anime_status_options,
				android.R.layout.simple_spinner_dropdown_item));
		
		watchedStatus.setOnItemSelectedListener(this);
		
		scoreStatus.setAdapter(ArrayAdapter.createFromResource(getActivity(),
				R.array.anime_score_options,
				android.R.layout.simple_spinner_dropdown_item));
		
		scoreStatus.setOnItemSelectedListener(this);

		singleAnimeLoader = getLoaderManager().initLoader(0, null, this);
		singleAnimeLoader.forceLoad();
		
		plusOne.setOnClickListener(this);

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

	public void onEvent(AnimeUpdateEvent aue) {
		if (singleAnimeLoader != null) {
			if (aue.id == activeRecord.id) {
				singleAnimeLoader.onContentChanged();
			}
		}
	}

	private void updateUI() {
		lazyLoader.displayImage(activeRecord.image_url, cover);
		title.setText(Html.fromHtml(activeRecord.title));
		if (activeRecord.synopsis != null) {
			synopsys.setText(Html.fromHtml(activeRecord.synopsis));
		}

		if (activeRecord.synopsis == null) {
			ServerInterface.getAnimeRecord(getActivity(), activeRecord.id);
		}

		watchedCount.setText(getString(R.string.watched_format, activeRecord.watched_episodes, activeRecord.episodes ));

		switch (activeRecord.score) {
		case 0:
			scoreStatus.setSelection(0);
			break;
		default:
			scoreStatus.setSelection(11 - activeRecord.score  );
		}
		
		watchedStatus.setSelection(activeRecord.watched_status.ordinal());

	}

	@Override
	public Loader<AnimeRecord> onCreateLoader(int id, Bundle args) {
		return new SingleAnimeLoader(getActivity(), idToDisplay);
	}

	@Override
	public void onLoadFinished(Loader<AnimeRecord> loader, AnimeRecord data) {
		activeRecord = data;
		updateUI();
	}

	@Override
	public void onLoaderReset(Loader<AnimeRecord> loader) {}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
		if ( activeRecord == null){
			return;
		}
		
		BaseActivity ba = (BaseActivity)getActivity();
		
		switch ( adapter.getId() ){
		case R.id.anime_watched_status:
			AnimeWatchedStatus newStatus = AnimeWatchedStatus.values()[position];
			if ( activeRecord.watched_status != newStatus ){
				activeRecord.watched_status = AnimeWatchedStatus.values()[position];
				//save
				new DBUpdateTask( ba.getHelper(),ba.getApplicationContext() ).execute(activeRecord);
			}
			break;
		case R.id.anime_score_status:
			int newScore;
			switch (position) {
			case 0:
				newScore = position;
				break;
			default:
				newScore = 11 - position;
			}
			if ( activeRecord.score != newScore ){
				activeRecord.score = newScore;
				//save
				new DBUpdateTask( ba.getHelper(),ba.getApplicationContext() ).execute(activeRecord);
			}
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}

	@Override
	public void onClick(View v) {
		BaseActivity ba = (BaseActivity)getActivity();
		switch ( v.getId() ){
		case R.id.plus_one:
			activeRecord.watched_episodes++;
			if ( activeRecord.watched_episodes > activeRecord.episodes ){
				activeRecord.watched_episodes = activeRecord.episodes;
				//save
				new DBUpdateTask( ba.getHelper(),ba.getApplicationContext() ).execute(activeRecord);
			}
			break;
		}
	}

}
