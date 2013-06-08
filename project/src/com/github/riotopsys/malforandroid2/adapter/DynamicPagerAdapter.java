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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.google.inject.Inject;

public class DynamicPagerAdapter extends FragmentPagerAdapter {

	private static final String TAG = DynamicPagerAdapter.class.getSimpleName();
	
	private Map<String, Fragment> fragmentIndex = new HashMap<String, Fragment>();
	private List<String> indexOrder = new ArrayList<String>();
	
	public void addFragment(String title, Fragment fragment){
		fragmentIndex.put(title, fragment);
		indexOrder.add(title);
		notifyDataSetChanged();
	}
	
	public void removeFragment(String title){
		fragmentIndex.remove(title);
		indexOrder.remove(title);
		notifyDataSetChanged();
	}
	
	public int getPosition( String title ){
		return indexOrder.indexOf(title);
	}

	@Inject
	public DynamicPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Log.i(TAG, String.format("get %d", position));
		return fragmentIndex.get(indexOrder.get(position));
	}

	@Override
	public int getCount() {
		return indexOrder.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return indexOrder.get(position);
	}
	
	@Override
	public int getItemPosition(Object object) {
		Fragment f = (Fragment) object;
		for(int i = 0; i < getCount(); i++) {

            Fragment item = (Fragment) getItem(i);
            if(item.equals(f)) {
                return i;
            }
        }
		return POSITION_NONE;
	}

}
