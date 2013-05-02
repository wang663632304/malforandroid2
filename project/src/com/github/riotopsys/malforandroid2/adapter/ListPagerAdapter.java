package com.github.riotopsys.malforandroid2.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.fragment.ItemListFragment;
import com.github.riotopsys.malforandroid2.fragment.SearchListFragment;
import com.github.riotopsys.malforandroid2.model.AnimeWatchedStatus;
import com.google.inject.Inject;

public class ListPagerAdapter extends FragmentPagerAdapter {

	private Context ctx;

	@Inject
	public ListPagerAdapter(FragmentManager fm, Context ctx) {
		super(fm);
		this.ctx = ctx;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment f = null;
		
		Bundle args = new Bundle();
		if ( AnimeWatchedStatus.values().length > position ){
			args.putSerializable("filter", AnimeWatchedStatus.values()[position]);
			f = new ItemListFragment();
		} else if ( AnimeWatchedStatus.values().length == position )  {
			//handle "all" condition
			args.putSerializable("filter", null);
			f = new ItemListFragment();
		} else {
			//handle "search" condition
			f = new SearchListFragment();
		}
		
		f.setArguments(args);
		return f;
	}

	@Override
	public int getCount() {
		return AnimeWatchedStatus.values().length+2;//account for all and search
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		if ( AnimeWatchedStatus.values().length > position ){
			return ctx.getString(AnimeWatchedStatus.values()[position].getResource());
		} else if ( AnimeWatchedStatus.values().length == position ) {
			//handle "all" condition
			return ctx.getString(R.string.all);
		} else {
			//handle "search" condition
			return ctx.getString(R.string.search);			
		}
	}

}
