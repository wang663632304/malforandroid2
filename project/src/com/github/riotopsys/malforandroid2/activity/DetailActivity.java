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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.event.AnimeChangeDetailViewRequest;
import com.github.riotopsys.malforandroid2.event.ChangeDetailViewRequest;
import com.github.riotopsys.malforandroid2.fragment.AnimeDetailFragment;
import com.github.riotopsys.malforandroid2.fragment.MangaDetailFragment;

public class DetailActivity extends BaseDetailActivity {
	
	private static final String TAG = DetailActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.detail_activity);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if ( savedInstanceState == null ){
			Intent intent = getIntent();
			currentDetail = (ChangeDetailViewRequest) intent.getExtras().getSerializable("ITEM");
			transitionDetail(false);
		} 

	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            Intent parentActivityIntent = new Intent(this, HubActivity.class);
	            parentActivityIntent.addFlags(
	                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                    Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(parentActivityIntent);
	            finish();
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	@Override
	protected void transitionDetail( boolean wasBack ) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		Fragment fragment;
		if (currentDetail != null) {
			
			setAnimations(wasBack, transaction);
			
			if ( currentDetail instanceof AnimeChangeDetailViewRequest){
				fragment = new AnimeDetailFragment();
			} else {
				fragment = new MangaDetailFragment();
			}
			Bundle args = new Bundle();
			args.putInt("id", currentDetail.id);
			fragment.setArguments(args);
			transaction.replace(R.id.detail_frame, fragment);
			transaction.commit();
		} else {
			finish();
		}
		
	}
	
}
