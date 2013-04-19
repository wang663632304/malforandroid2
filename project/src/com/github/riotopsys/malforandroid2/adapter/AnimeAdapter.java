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

package com.github.riotopsys.malforandroid2.adapter;

import java.util.Collections;
import java.util.Comparator;
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
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AnimeAdapter extends BaseAdapter {
	
	@Inject
	private ImageLoader lazyLoader;
	
	@Inject
	private Comparator<AnimeRecord> comparator;
	
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
		
		ImageView imageView = (ImageView)convertView.findViewById(R.id.thumb_image);
		imageView.setImageBitmap(null);
		lazyLoader.displayImage(anime.image_url,imageView ); 
		
		return convertView;
	}
	
	public void addAll( List<AnimeRecord> anime ) {
		animeList.clear();
		if ( anime != null ){
			animeList.addAll(anime);
			Collections.sort( animeList, comparator );
		}
		notifyDataSetChanged();
	}

}
