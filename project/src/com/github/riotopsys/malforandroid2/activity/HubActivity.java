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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.riotopsys.malforandroid2.GlobalState;
import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.adapter.ListPagerAdapter;
import com.github.riotopsys.malforandroid2.database.ReadNameValuePairs;
import com.github.riotopsys.malforandroid2.database.ReadNameValuePairs.Callback;
import com.github.riotopsys.malforandroid2.event.ChangeDetailViewRequest;
import com.github.riotopsys.malforandroid2.fragment.AnimeDetailFragment;
import com.github.riotopsys.malforandroid2.fragment.LoginFragment;
import com.github.riotopsys.malforandroid2.fragment.PlacardFragment;
import com.github.riotopsys.malforandroid2.model.NameValuePair;
import com.github.riotopsys.malforandroid2.server.ServerInterface;
import com.google.inject.Inject;

import de.greenrobot.event.EventBus;

public class HubActivity extends BaseActivity implements Callback<String> {

	private static String TAG = HubActivity.class.getSimpleName();

	@InjectView(R.id.list_pager)
	private ViewPager listPager;

	@Inject
	private EventBus bus;

	@Inject
	private ListPagerAdapter adapter;
	
	@Inject 
	private GlobalState state;
	
	@Inject
	private LoginFragment login;
	
	private LinkedList<ChangeDetailViewRequest> manualBackStack = new LinkedList<ChangeDetailViewRequest>();

	private ChangeDetailViewRequest currentDetail = null;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.main);

		listPager.setAdapter(adapter);
		
		if ( savedInstanceState != null){
			currentDetail = (ChangeDetailViewRequest) savedInstanceState.get("currentDetail");
			manualBackStack.clear();
			manualBackStack.addAll((Collection<ChangeDetailViewRequest>) savedInstanceState.get("manualBackStack"));
		}
		
		transitionDetail(currentDetail);
		
		new ReadNameValuePairs<String>(getHelper(), this).execute("USER","PASS");
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("currentDetail", currentDetail);
		outState.putSerializable("manualBackStack", manualBackStack);
		
		super.onSaveInstanceState(outState);
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

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.refresh_menu_item) {
			ServerInterface.getAnimeList(this);
			if ( !state.loginSet() ){
				login.show(getSupportFragmentManager(), null);
			}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.base, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		if (manualBackStack.isEmpty()) {
			super.onBackPressed();
		} else {
			transitionDetail(manualBackStack.pop());
		}
	}

	public void onEventMainThread(ChangeDetailViewRequest cdvr) {
		if ( cdvr.equals(currentDetail)){
			return;
		}
		manualBackStack.push(currentDetail);
		transitionDetail(cdvr);
	}

	private void transitionDetail(ChangeDetailViewRequest cdvr) {

		currentDetail = cdvr;

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		Fragment fragment;
		if (cdvr != null) {
			fragment = new AnimeDetailFragment();
			Bundle args = new Bundle();
			args.putInt("id", cdvr.id);
			fragment.setArguments(args);
		} else {
			fragment = new PlacardFragment();
		}
		transaction.replace(R.id.detail_frame, fragment);
		transaction.commit();
	}

	@Override
	public void onNameValuePairsReady(List<NameValuePair<String>> data) {
		for (NameValuePair<String> pair : data) {
			if ("USER".equals(pair.name)) {
				state.setUser(pair.value);
			}
			if ("PASS".equals(pair.name)) {
				state.setPass(pair.value);
			}
		}
		if ( !state.loginSet() ){
			login.show(getSupportFragmentManager(), null);
		}
		
	}

}
