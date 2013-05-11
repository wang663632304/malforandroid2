package com.github.riotopsys.malforandroid2.activity;

import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.Nullable;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.View;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.event.ChangeDetailViewRequest;
import com.google.inject.Inject;

import de.greenrobot.event.EventBus;

public abstract class BaseDetailActivity extends BaseActivity {
	
	@Nullable
	@InjectView(R.id.detail_frame)
	protected View detailFrame;
	
	@Inject
	private EventBus bus;

	private LinkedList<ChangeDetailViewRequest> manualBackStack = new LinkedList<ChangeDetailViewRequest>();
	protected ChangeDetailViewRequest currentDetail = null;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if ( savedInstanceState != null){
			currentDetail = (ChangeDetailViewRequest) savedInstanceState.get("currentDetail");
			manualBackStack.clear();
			manualBackStack.addAll((Collection<ChangeDetailViewRequest>) savedInstanceState.get("manualBackStack"));
		}
		
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
		currentDetail = cdvr;
		transitionDetail(cdvr);
	}

	protected void purgeFakeBackStack() {
		manualBackStack.clear();
		currentDetail = null;
	}
	
	protected abstract void transitionDetail(ChangeDetailViewRequest cdvr);

}
