package com.github.riotopsys.malforandroid2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.event.ChangeDetailViewRequest;
import com.github.riotopsys.malforandroid2.fragment.AnimeDetailFragment;

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
			transitionDetail();
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
	protected void transitionDetail() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		Fragment fragment;
		if (currentDetail != null) {
			fragment = new AnimeDetailFragment();
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
