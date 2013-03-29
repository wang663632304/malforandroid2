package com.github.riotopsys.malforandroid2.fragment;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.event.AnimeUpdateEvent;
import com.github.riotopsys.malforandroid2.loader.SingleAnimeLoader;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.server.ServerInterface;
import com.github.riotopsys.malforandroid2.util.LazyLoader;
import com.google.inject.Inject;

import de.greenrobot.event.EventBus;

public class AnimeDetailFragment extends RoboFragment implements
		LoaderManager.LoaderCallbacks<AnimeRecord> {

	@InjectView(R.id.title)
	private TextView title;

	@InjectView(R.id.cover_image)
	private ImageView cover;

	@InjectView(R.id.synopsis)
	private TextView synopsys;

	@Inject
	private LazyLoader lazyLoader;

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

		singleAnimeLoader = getLoaderManager().initLoader(0, null, this);
		singleAnimeLoader.forceLoad();
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
		lazyLoader.DisplayImage(activeRecord.image_url, cover, R.drawable.icon);
		title.setText(activeRecord.title);
		if ( activeRecord.synopsis != null ){
			synopsys.setText(Html.fromHtml(activeRecord.synopsis));
		}

		if (activeRecord.synopsis == null) {
			ServerInterface.getAnimeRecord(getActivity(), activeRecord.id);
		}
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
	public void onLoaderReset(Loader<AnimeRecord> loader) {
	}

}
