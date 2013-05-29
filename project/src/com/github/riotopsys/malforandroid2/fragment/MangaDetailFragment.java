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
import com.github.riotopsys.malforandroid2.database.MangaDBDeleteTask;
import com.github.riotopsys.malforandroid2.event.AnimeChangeDetailViewRequest;
import com.github.riotopsys.malforandroid2.event.MangaChangeDetailViewRequest;
import com.github.riotopsys.malforandroid2.event.MangaUpdateEvent;
import com.github.riotopsys.malforandroid2.fragment.NumberPickerFragment.OnDismissListener;
import com.github.riotopsys.malforandroid2.loader.SingleMangaLoader;
import com.github.riotopsys.malforandroid2.model.AnimeCrossReferance;
import com.github.riotopsys.malforandroid2.model.MangaCrossReferance;
import com.github.riotopsys.malforandroid2.model.MangaReadStatus;
import com.github.riotopsys.malforandroid2.model.MangaRecord;
import com.github.riotopsys.malforandroid2.server.MangaServerInterface;
import com.github.riotopsys.malforandroid2.server.tasks.MangaUpdateTask;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import de.greenrobot.event.EventBus;

public class MangaDetailFragment extends RoboFragment implements
		LoaderManager.LoaderCallbacks<MangaRecord>, OnItemSelectedListener, OnClickListener, OnTouchListener, ImageLoadingListener {

	private static final String TAG = MangaDetailFragment.class.getSimpleName();

	@InjectView(R.id.title)
	private TextView title;

	@InjectView(R.id.cover_image)
	private ImageView cover;

	@InjectView(R.id.synopsis)
	private TextView synopsys;

	@InjectView(R.id.synopsis_container)
	private View synopsysContainer;
	
	@InjectView(R.id.manga_read_status)
	private Spinner readStatus;

	@InjectView(R.id.manga_score_status)
	private Spinner scoreStatus;

	@InjectView(R.id.chapter_count)
	private TextView chapterCount;
	
	@InjectView(R.id.volume_count)
	private TextView volumeCount;
	
	@InjectView(R.id.chapter_panel)
	private View chapterPannel;
	
	@InjectView(R.id.volume_panel)
	private View volumePannel;
	
	@InjectView(R.id.plus_one_chapter)
	private Button plusOneChapter;
	
	@InjectView(R.id.plus_one_volume)
	private Button plusOneVolume;
	
	@InjectView(R.id.spin_offs)
	private TextView spinOffs;
	
	@InjectView(R.id.alternative_versions)
	private TextView alternativeVersions;
	
	@InjectView(R.id.anime_adaptations)
	private TextView animeAdaptations;
	
	@InjectView(R.id.type)
	private TextView type;
	
	@InjectView(R.id.status)
	private TextView status;
	
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
	
	@InjectView(R.id.status_panel)
	private View statusPanel;
	
	@InjectView(R.id.score_panel)
	private View scorePanel;
	
	@InjectView(R.id.add_panel)
	private View addPanel;
	
	@InjectView(R.id.manga_watched_status_add)
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
	private Provider<MangaUpdateTask> mangaUpdateTaskProvider;

	private int idToDisplay;

	private Loader<MangaRecord> singleMangaLoader;

	private MangaRecord activeRecord;

	private OnDismissListener chapterDismissListener = new OnDismissListener() {
		@Override
		public void onDismiss(NumberPickerFragment numberPickerFragment) {
			int value = numberPickerFragment.getValue();
			if ( activeRecord.chapters_read != value ){
				activeRecord.chapters_read = value;
				//save
				mangaUpdateTaskProvider.get().setRecord(activeRecord).start();
				checkComplete();
			}
		}
	};

	private OnDismissListener volumeDismissListener = new OnDismissListener() {
		
		@Override
		public void onDismiss(NumberPickerFragment numberPickerFragment) {
			int value = numberPickerFragment.getValue();
			if ( activeRecord.volumes_read != value ){
				activeRecord.volumes_read = value;
				//save
				mangaUpdateTaskProvider.get().setRecord(activeRecord).start();
				checkComplete();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		idToDisplay = getArguments().getInt("id");
		
		Log.v(TAG, String.format("ID: %d", idToDisplay));
		
		setHasOptionsMenu(true);
		
		return inflater.inflate(R.layout.manga_detail_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		readStatus.setAdapter(ArrayAdapter.createFromResource(getActivity(),
				R.array.manga_status_options,
				android.R.layout.simple_spinner_dropdown_item));
		
		readStatus.setOnItemSelectedListener(this);
		
		scoreStatus.setAdapter(ArrayAdapter.createFromResource(getActivity(),
				R.array.anime_score_options,
				android.R.layout.simple_spinner_dropdown_item));
		
		addSpinner.setAdapter(ArrayAdapter.createFromResource(getActivity(),
				R.array.manga_status_options,
				android.R.layout.simple_spinner_dropdown_item));
		
		scoreStatus.setOnItemSelectedListener(this);

		singleMangaLoader = getLoaderManager().initLoader(0, null, this);
		singleMangaLoader.forceLoad();
		
		plusOneChapter.setOnClickListener(this);
		plusOneVolume.setOnClickListener(this);
		chapterPannel.setOnClickListener(this);
		volumePannel.setOnClickListener(this);
		spinOffs.setOnClickListener(this);
		alternativeVersions.setOnClickListener(this);
		animeAdaptations.setOnClickListener(this);
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
			new MangaDBDeleteTask( ba.getHelper(), ba.getApplicationContext(), bus ).execute(activeRecord);
			return true;
		case R.id.open_web:
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("http://myanimelist.net/manga/%d", activeRecord.id)));
			startActivity(myIntent);
			return true;
		}
		return false;
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if ( activeRecord != null ){
			menu.findItem(R.id.remove_anime).setEnabled(activeRecord.read_status != null);
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

	public void onEvent(MangaUpdateEvent aue) {
		if (singleMangaLoader != null) {
			if (aue.id == idToDisplay) {
				singleMangaLoader.onContentChanged();
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
			MangaServerInterface.getAnimeRecord(getActivity(), activeRecord.id);
			informationContainer.setVisibility(View.GONE);
			statisticsContainer.setVisibility(View.GONE);
		} else {
			informationContainer.setVisibility(View.VISIBLE);
			statisticsContainer.setVisibility(View.VISIBLE);
		}

		chapterCount.setText(getString(R.string.chapter_format, activeRecord.chapters_read, activeRecord.chapters ));
		volumeCount.setText(getString(R.string.volume_format, activeRecord.volumes_read, activeRecord.volumes ));
		
		if ( activeRecord.chapters != 0 && activeRecord.chapters == activeRecord.chapters_read ){
			plusOneChapter.setVisibility(View.GONE);
		} else {
			plusOneChapter.setVisibility(View.VISIBLE);
		}
		
		if ( activeRecord.volumes != 0 && activeRecord.volumes == activeRecord.volumes_read ){
			plusOneVolume.setVisibility(View.GONE);
		} else {
			plusOneVolume.setVisibility(View.VISIBLE);
		}
		
		type.setText(getString(R.string.type_format, activeRecord.type ));
		status.setText(getString(R.string.status_format, activeRecord.status ));
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
		
		if (activeRecord.read_status != null ){
			readStatus.setSelection(activeRecord.read_status.ordinal());
			chapterPannel.setVisibility(View.VISIBLE);
			volumePannel.setVisibility(View.VISIBLE);
			statusPanel.setVisibility(View.VISIBLE);
			scorePanel.setVisibility(View.VISIBLE); 
			
			addPanel.setVisibility(View.GONE);
		} else {
			chapterPannel.setVisibility(View.GONE);
			volumePannel.setVisibility(View.GONE);
			statusPanel.setVisibility(View.GONE);
			scorePanel.setVisibility(View.GONE);
			
			addPanel.setVisibility(View.VISIBLE);
		}
		
		if ( activeRecord.related_manga.size() >0 ){
			spinOffs.setText(getString(R.string.related_format, activeRecord.related_manga.size()));
			spinOffs.setVisibility(View.VISIBLE);
		} else {
			spinOffs.setVisibility(View.GONE);
		}

		if ( activeRecord.alternative_versions.size() >0 ){
			alternativeVersions.setText(getString(R.string.alternative_versions_format, activeRecord.alternative_versions.size()));
			alternativeVersions.setVisibility(View.VISIBLE);
		} else {
			alternativeVersions.setVisibility(View.GONE);
		}
		
		if ( activeRecord.anime_adaptations.size() >0 ){
			animeAdaptations.setText(getString(R.string.anime_adaptations_format, activeRecord.anime_adaptations.size()));
			animeAdaptations.setVisibility(View.VISIBLE);
		} else {
			animeAdaptations.setVisibility(View.GONE);
		}
	}

	@Override
	public Loader<MangaRecord> onCreateLoader(int id, Bundle args) {
		return new SingleMangaLoader(getActivity(), idToDisplay);
	}

	@Override
	public void onLoadFinished(Loader<MangaRecord> loader, MangaRecord data) {
		activeRecord = data;
		if ( activeRecord == null ){
			MangaServerInterface.getAnimeRecord(getActivity(), idToDisplay);
		} else {
			updateUI();
		}
	}

	@Override
	public void onLoaderReset(Loader<MangaRecord> loader) {}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
		if ( activeRecord == null){
			return;
		}
		
		switch ( adapter.getId() ){
		case R.id.manga_read_status:
			MangaReadStatus newStatus = MangaReadStatus.values()[position];
			if ( activeRecord.read_status != null && activeRecord.read_status != newStatus ){
				activeRecord.read_status = MangaReadStatus.values()[position];
				mangaUpdateTaskProvider.get().setRecord(activeRecord).start();
			}
			break;
		case R.id.manga_score_status:
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
				mangaUpdateTaskProvider.get().setRecord(activeRecord).start();
			}
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}

	@Override
	public void onClick(View v) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		
		switch (v.getId()) {
		case R.id.plus_one_chapter:
			activeRecord.chapters_read++;
			if (activeRecord.chapters_read > activeRecord.chapters && activeRecord.chapters != 0) {
				activeRecord.chapters_read = activeRecord.chapters;
				// save
			}
			mangaUpdateTaskProvider.get().setRecord(activeRecord).start();
			checkComplete();
			break;
		case R.id.plus_one_volume:
			activeRecord.volumes_read++;
			if (activeRecord.volumes_read > activeRecord.volumes && activeRecord.volumes != 0) {
				activeRecord.volumes_read = activeRecord.volumes;
				// save
			}
			mangaUpdateTaskProvider.get().setRecord(activeRecord).start();
			checkComplete();
			break;
		case R.id.chapter_panel:
			NumberPickerFragment numberPickerFragmentChapter = new NumberPickerFragment();
			if ( activeRecord.chapters != 0 ){
				numberPickerFragmentChapter.setMaximum(activeRecord.chapters);
			} else {
				numberPickerFragmentChapter.setMaximum(Integer.MAX_VALUE);
			}
			numberPickerFragmentChapter.setValue( activeRecord.chapters_read);		
			
			numberPickerFragmentChapter.show(fm,"numberpicker");
			
			numberPickerFragmentChapter.setOnDismissListener(chapterDismissListener);
			break;
		case R.id.volume_panel:
			NumberPickerFragment numberPickerFragmentVolume = new NumberPickerFragment();
			if ( activeRecord.chapters != 0 ){
				numberPickerFragmentVolume.setMaximum(activeRecord.volumes);
			} else {
				numberPickerFragmentVolume.setMaximum(Integer.MAX_VALUE);
			}
			numberPickerFragmentVolume.setValue( activeRecord.volumes_read);		
			
			numberPickerFragmentVolume.show(fm,"numberpicker");
			
			numberPickerFragmentVolume.setOnDismissListener(volumeDismissListener);
			break;
		case R.id.add_button:
			activeRecord.read_status = MangaReadStatus.values()[addSpinner.getSelectedItemPosition()];
			//TODO: add add task
			break;
		case R.id.spin_offs:
			createMangaPickerDialog( activeRecord.related_manga ).show();
			break;
		case R.id.alternative_versions:
			createMangaPickerDialog( activeRecord.alternative_versions ).show();
			break;
		case R.id.anime_adaptations:
			createAnimePickerDialog( activeRecord.anime_adaptations ).show();
			break;
		}
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
	
	private void checkComplete() {
		if ( activeRecord.read_status != MangaReadStatus.COMPLETED && 
			( activeRecord.chapters == activeRecord.chapters_read || activeRecord.volumes == activeRecord.volumes_read ) &&
			( activeRecord.chapters != 0 && activeRecord.volumes != 0 ) ){
			new AlertDialog.Builder(getActivity())
			.setTitle(R.string.complete_question_title)
			.setMessage(R.string.complete_question_msg)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					activeRecord.read_status = MangaReadStatus.COMPLETED;
					//we can have a mismatch between volumes and chapters so well bash it here
					activeRecord.chapters_read = activeRecord.chapters;
					activeRecord.volumes_read = activeRecord.volumes;
					mangaUpdateTaskProvider.get().setRecord(activeRecord).start();
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
