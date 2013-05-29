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

import java.util.LinkedList;
import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.activity.BaseActivity;
import com.github.riotopsys.malforandroid2.database.AnimeDBDeleteTask;
import com.github.riotopsys.malforandroid2.event.AnimeChangeDetailViewRequest;
import com.github.riotopsys.malforandroid2.event.AnimeUpdateEvent;
import com.github.riotopsys.malforandroid2.event.MangaChangeDetailViewRequest;
import com.github.riotopsys.malforandroid2.fragment.NumberPickerFragment.OnDismissListener;
import com.github.riotopsys.malforandroid2.loader.SingleAnimeLoader;
import com.github.riotopsys.malforandroid2.model.AnimeCrossReferance;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.AnimeWatchedStatus;
import com.github.riotopsys.malforandroid2.model.MangaCrossReferance;
import com.github.riotopsys.malforandroid2.server.AnimeServerInterface;
import com.github.riotopsys.malforandroid2.server.tasks.AnimeUpdateTask;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import de.greenrobot.event.EventBus;

public class AnimeDetailFragment extends RoboFragment implements
		LoaderManager.LoaderCallbacks<AnimeRecord>, OnItemSelectedListener, OnClickListener, OnDismissListener, OnTouchListener, ImageLoadingListener {

	private static final String TAG = AnimeDetailFragment.class.getSimpleName();

	@InjectView(R.id.title)
	private TextView title;

	@InjectView(R.id.cover_image)
	private ImageView cover;

	@InjectView(R.id.synopsis)
	private TextView synopsys;

	@InjectView(R.id.synopsis_container)
	private View synopsysContainer;
	
	@InjectView(R.id.anime_watched_status)
	private Spinner watchedStatus;

	@InjectView(R.id.anime_score_status)
	private Spinner scoreStatus;

	@InjectView(R.id.watched_count)
	private TextView watchedCount;
	
	@InjectView(R.id.watched_panel)
	private View watchedPannel;
	
	@InjectView(R.id.plus_one)
	private Button plusOne;
	
	@InjectView(R.id.prequel)
	private TextView prequel;
	
	@InjectView(R.id.sequel)
	private TextView sequel;
	
	@InjectView(R.id.side_story)
	private TextView sideStory;
	
	@InjectView(R.id.spin_offs)
	private TextView spinOffs;
	
	@InjectView(R.id.summaries)
	private TextView summaries;
	
	@InjectView(R.id.alternative_versions)
	private TextView alternativeVersions;
	
	@InjectView(R.id.manga_adaptations)
	private TextView mangaAdaptations;
	
	@InjectView(R.id.type)
	private TextView type;
	
	@InjectView(R.id.status)
	private TextView status;
	
	@InjectView(R.id.classification)
	private TextView classification;
	
	@InjectView(R.id.rank)
	private TextView rank;
	
	@InjectView(R.id.popularity)
	private TextView popularity;
	
	@InjectView(R.id.member_score)
	private TextView memberScore;
	
	@InjectView(R.id.member_count)
	private TextView memberCount;
	
	@InjectView(R.id.favorited_count)
	private TextView favoritedCount;
	
	@InjectView(R.id.information_container)
	private View informationContainer;
	
	@InjectView(R.id.statistics_container)
	private View statisticsContainer;
	
	@InjectView(R.id.watched_panel)
	private View watchedPanel;
	
	@InjectView(R.id.status_panel)
	private View statusPanel;
	
	@InjectView(R.id.score_panel)
	private View scorePanel;
	
	@InjectView(R.id.add_panel)
	private View addPanel;
	
	@InjectView(R.id.anime_watched_status_add)
	private Spinner addSpinner;
	
	@InjectView(R.id.add_button)
	private Button addButton;
	
	@InjectView(R.id.cover_progress_bar)
	private View coverProgressbar;
	
	@InjectView(R.id.synopsis_progress_bar)
	private View synopsisProgressbar;
	
	@Inject
	private ImageLoader lazyLoader;

	@Inject
	private EventBus bus;
	
	@Inject
	private Provider<AnimeUpdateTask> animeUpdateTaskProvider;

	private int idToDisplay;

	private Loader<AnimeRecord> singleAnimeLoader;

	private AnimeRecord activeRecord;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		idToDisplay = getArguments().getInt("id");
		
		Log.v(TAG, String.format("ID: %d", idToDisplay));
		
		setHasOptionsMenu(true);
		
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
		
		addSpinner.setAdapter(ArrayAdapter.createFromResource(getActivity(),
				R.array.anime_status_options,
				android.R.layout.simple_spinner_dropdown_item));
		
		scoreStatus.setOnItemSelectedListener(this);

		singleAnimeLoader = getLoaderManager().initLoader(0, null, this);
		singleAnimeLoader.forceLoad();
		
		plusOne.setOnClickListener(this);
		watchedPannel.setOnClickListener(this);
		prequel.setOnClickListener(this);
		sequel.setOnClickListener(this);
		sideStory.setOnClickListener(this);
		spinOffs.setOnClickListener(this);
		summaries.setOnClickListener(this);
		alternativeVersions.setOnClickListener(this);
		mangaAdaptations.setOnClickListener(this);
		addButton.setOnClickListener(this);
		
		synopsysContainer.setOnTouchListener(this);
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.detail_fragment_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.remove_anime:
			BaseActivity ba = (BaseActivity)getActivity();
			new AnimeDBDeleteTask( ba.getHelper(), ba.getApplicationContext(), bus ).execute(activeRecord);
			return true;
		case R.id.open_web:
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("http://myanimelist.net/anime/%d", activeRecord.id)));
			startActivity(myIntent);
			return true;
		}
		return false;
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if ( activeRecord != null ){
			menu.findItem(R.id.remove_anime).setEnabled(activeRecord.watched_status != null);
		}
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
			if (aue.id == idToDisplay) {
				singleAnimeLoader.onContentChanged();
			}
		}
	}

	private void updateUI() {
		
		if ( !isAdded() ){
			return;
		}
		
		lazyLoader.displayImage(activeRecord.image_url, cover, this );
		title.setText(Html.fromHtml(activeRecord.title));
		if (activeRecord.synopsis != null) {
			synopsys.setText(Html.fromHtml(activeRecord.synopsis));
			synopsisProgressbar.setVisibility(View.GONE);
		} else {
			synopsisProgressbar.setVisibility(View.VISIBLE);
		}

		if (activeRecord.synopsis == null) {
			AnimeServerInterface.getAnimeRecord(getActivity(), activeRecord.id);
			informationContainer.setVisibility(View.GONE);
			statisticsContainer.setVisibility(View.GONE);
		} else {
			informationContainer.setVisibility(View.VISIBLE);
			statisticsContainer.setVisibility(View.VISIBLE);
		}

		watchedCount.setText(getString(R.string.watched_format, activeRecord.watched_episodes, activeRecord.episodes ));
		
		if ( activeRecord.episodes != 0 && activeRecord.episodes == activeRecord.watched_episodes ){
			plusOne.setVisibility(View.GONE);
		} else {
			plusOne.setVisibility(View.VISIBLE);
		}
		
		type.setText(getString(R.string.type_format, activeRecord.type ));
		status.setText(getString(R.string.status_format, activeRecord.status ));
		classification.setText(getString(R.string.classification_format, activeRecord.classification ));
		rank.setText(getString(R.string.rank_format, activeRecord.rank ));
		popularity.setText(getString(R.string.popularity_format, activeRecord.popularity_rank ));
		memberScore.setText(getString(R.string.members_score_format, activeRecord.members_score ));
		memberCount.setText(getString(R.string.members_count_format, activeRecord.members_count ));
		favoritedCount.setText(getString(R.string.favorited_count_format, activeRecord.favorited_count ));

		switch (activeRecord.score) {
		case 0:
			scoreStatus.setSelection(0);
			break;
		default:
			scoreStatus.setSelection(11 - activeRecord.score  );
		}
		
		if (activeRecord.watched_status != null ){
			watchedStatus.setSelection(activeRecord.watched_status.ordinal());
			watchedPanel.setVisibility(View.VISIBLE);
			statusPanel.setVisibility(View.VISIBLE);
			scorePanel.setVisibility(View.VISIBLE); 
			
			addPanel.setVisibility(View.GONE);
		} else {
			watchedPanel.setVisibility(View.GONE);
			statusPanel.setVisibility(View.GONE);
			scorePanel.setVisibility(View.GONE);
			
			addPanel.setVisibility(View.VISIBLE);
		}
		
		if ( activeRecord.prequels.size() >0 ){
			prequel.setText(getString(R.string.prequel_format, activeRecord.prequels.size()));
			prequel.setVisibility(View.VISIBLE);
		} else {
			prequel.setVisibility(View.GONE);
		}
		
		if ( activeRecord.sequels.size() >0 ){
			sequel.setText(getString(R.string.sequel_format, activeRecord.sequels.size()));
			sequel.setVisibility(View.VISIBLE);
		} else {
			sequel.setVisibility(View.GONE);
		}
		
		if ( activeRecord.side_stories.size() >0 ){
			sideStory.setText(getString(R.string.side_story_format, activeRecord.side_stories.size()));
			sideStory.setVisibility(View.VISIBLE);
		} else {
			sideStory.setVisibility(View.GONE);
		}
		
		if ( activeRecord.spin_offs.size() >0 ){
			spinOffs.setText(getString(R.string.spin_off_format, activeRecord.spin_offs.size()));
			spinOffs.setVisibility(View.VISIBLE);
		} else {
			spinOffs.setVisibility(View.GONE);
		}

		if ( activeRecord.summaries.size() >0 ){
			summaries.setText(getString(R.string.summaries_format, activeRecord.summaries.size()));
			summaries.setVisibility(View.VISIBLE);
		} else {
			summaries.setVisibility(View.GONE);
		}
		
		if ( activeRecord.alternative_versions.size() >0 ){
			alternativeVersions.setText(getString(R.string.alternative_versions_format, activeRecord.alternative_versions.size()));
			alternativeVersions.setVisibility(View.VISIBLE);
		} else {
			alternativeVersions.setVisibility(View.GONE);
		}
		
		if ( activeRecord.alternative_versions.size() >0 ){
			mangaAdaptations.setText(getString(R.string.manga_adaptations_format, activeRecord.manga_adaptations.size()));
			mangaAdaptations.setVisibility(View.VISIBLE);
		} else {
			mangaAdaptations.setVisibility(View.GONE);
		}
	}

	@Override
	public Loader<AnimeRecord> onCreateLoader(int id, Bundle args) {
		return new SingleAnimeLoader(getActivity(), idToDisplay);
	}

	@Override
	public void onLoadFinished(Loader<AnimeRecord> loader, AnimeRecord data) {
		activeRecord = data;
		if ( activeRecord == null ){
			AnimeServerInterface.getAnimeRecord(getActivity(), idToDisplay);
		} else {
			updateUI();
		}
	}

	@Override
	public void onLoaderReset(Loader<AnimeRecord> loader) {}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
		if ( activeRecord == null){
			return;
		}
		
		switch ( adapter.getId() ){
		case R.id.anime_watched_status:
			AnimeWatchedStatus newStatus = AnimeWatchedStatus.values()[position];
			if ( activeRecord.watched_status != null && activeRecord.watched_status != newStatus ){
				activeRecord.watched_status = AnimeWatchedStatus.values()[position];
				animeUpdateTaskProvider.get().setRecord(activeRecord).start();
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
				animeUpdateTaskProvider.get().setRecord(activeRecord).start();
			}
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.plus_one:
			activeRecord.watched_episodes++;
			if (activeRecord.watched_episodes > activeRecord.episodes && activeRecord.episodes != 0) {
				activeRecord.watched_episodes = activeRecord.episodes;
				// save
			}
			animeUpdateTaskProvider.get().setRecord(activeRecord).start();
			checkComplete();
			break;
		case R.id.watched_panel:
			FragmentManager fm = getActivity().getSupportFragmentManager();
			NumberPickerFragment numberPickerFragment = new NumberPickerFragment();
			if ( activeRecord.episodes != 0 ){
				numberPickerFragment.setMaximum(activeRecord.episodes);
			} else {
				numberPickerFragment.setMaximum(Integer.MAX_VALUE);
			}
			numberPickerFragment.setValue( activeRecord.watched_episodes);		
			
			numberPickerFragment.show(fm,"numberpicker");
			
			numberPickerFragment.setOnDismissListener(this);
			break;
		case R.id.add_button:
			activeRecord.watched_status = AnimeWatchedStatus.values()[addSpinner.getSelectedItemPosition()];
			//TODO: add task needed
			break;
		case R.id.prequel:
			createAnimePickerDialog( activeRecord.prequels ).show();
			break;
		case R.id.sequel:
			createAnimePickerDialog( activeRecord.sequels ).show();
			break;
		case R.id.side_story:
			createAnimePickerDialog( activeRecord.side_stories ).show();
			break;
		case R.id.spin_offs:
			createAnimePickerDialog( activeRecord.spin_offs ).show();
			break;
		case R.id.summaries:
			createAnimePickerDialog( activeRecord.summaries ).show();
			break;
		case R.id.alternative_versions:
			createAnimePickerDialog( activeRecord.alternative_versions ).show();
			break;
		case R.id.manga_adaptations:
			createMangaPickerDialog( activeRecord.manga_adaptations ).show();
			break;
		}
	}

	private Dialog createAnimePickerDialog(final LinkedList<AnimeCrossReferance> crefs) {
		List<CharSequence> titles = new LinkedList<CharSequence>();
		
		for ( AnimeCrossReferance cr : crefs ){
			titles.add(Html.fromHtml(cr.title));
		}
		
		return new AlertDialog.Builder(getActivity()).setItems(titles.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				bus.post(new AnimeChangeDetailViewRequest(crefs.get(which).anime_id));
			}
		}).create();
		
	}
	
	private Dialog createMangaPickerDialog(final LinkedList<MangaCrossReferance> crefs) {
		List<CharSequence> titles = new LinkedList<CharSequence>();
		
		for ( MangaCrossReferance cr : crefs ){
			titles.add(Html.fromHtml(cr.title));
		}
		
		return new AlertDialog.Builder(getActivity()).setItems(titles.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				bus.post(new MangaChangeDetailViewRequest(crefs.get(which).manga_id));
			}
		}).create();
		
	}
	

	@Override
	public void onDismiss(NumberPickerFragment numberPickerFragment) {
		int value = numberPickerFragment.getValue();
		if ( activeRecord.watched_episodes != value ){
			activeRecord.watched_episodes = value;
			//save
			animeUpdateTaskProvider.get().setRecord(activeRecord).start();
			checkComplete();
		}
	}

	private void checkComplete() {
		if ( activeRecord.watched_status != AnimeWatchedStatus.COMPLETED && activeRecord.episodes == activeRecord.watched_episodes ){
			new AlertDialog.Builder(getActivity())
			.setTitle(R.string.complete_question_title)
			.setMessage(R.string.complete_question_msg)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					activeRecord.watched_status = AnimeWatchedStatus.COMPLETED;
					animeUpdateTaskProvider.get().setRecord(activeRecord).start();
				}
			})
			.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).create().show();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ViewGroup vg = (ViewGroup)v;
		if ( vg.getChildAt(0).getHeight() > v.getHeight()) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				requestDisallowParentInterceptTouchEvent(v, true);
			} else if (event.getAction() == MotionEvent.ACTION_UP
					|| event.getAction() == MotionEvent.ACTION_CANCEL) {
				requestDisallowParentInterceptTouchEvent(v, false);
			}
		}
		return false;
	}
	

	private void requestDisallowParentInterceptTouchEvent(View v,
			Boolean disallowIntercept) {
		while (v.getParent() != null && v.getParent() instanceof View) {
			if (v.getParent() instanceof ScrollView) {
				v.getParent().requestDisallowInterceptTouchEvent(
						disallowIntercept);
			}
			v = (View) v.getParent();
		}
	}

	@Override
	public void onLoadingCancelled(String arg0, View arg1) {
		coverProgressbar.setVisibility(View.GONE);
	}

	@Override
	public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		coverProgressbar.setVisibility(View.GONE);
	}

	@Override
	public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		coverProgressbar.setVisibility(View.GONE);
	}

	@Override
	public void onLoadingStarted(String arg0, View arg1) {
		coverProgressbar.setVisibility(View.VISIBLE);
	}

}
