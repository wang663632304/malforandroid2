package com.github.riotopsys.malforandroid2.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.util.LazyLoader;
import com.google.inject.Inject;

public class AnimeAdapter extends BaseAdapter {
	
	@Inject
	private LazyLoader lazyLoader;
	
	private List<AnimeRecord> animeList = new LinkedList<AnimeRecord>();

	@Override
	public int getCount() {
		return animeList.size();
	}

	@Override
	public Object getItem(int position) {
		return animeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if ( convertView == null ){
			convertView = ((LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.anime_item, null);
		}
		AnimeRecord anime = animeList.get(position);
		((TextView)convertView.findViewById(R.id.title)).setText(Html.fromHtml(anime.title));
		lazyLoader.DisplayImage(anime.image_url,
				((ImageView)convertView.findViewById(R.id.thumb_image)),
				R.drawable.icon); 
		
		return convertView;
	}
	
	public void addAll( List<AnimeRecord> anime ) {
		animeList.clear();
		if ( anime != null ){
			animeList.addAll(anime);
		}
		notifyDataSetChanged();
	}

}
