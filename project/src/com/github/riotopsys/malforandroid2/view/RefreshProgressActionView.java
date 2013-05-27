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

package com.github.riotopsys.malforandroid2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.riotopsys.malforandroid2.R;

public class RefreshProgressActionView extends FrameLayout {

	private ImageView icon;
	private ProgressBar progress;
	private boolean busy;

	private OnClickListener internalOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (externalOnClickListener != null) {
				externalOnClickListener.onClick(RefreshProgressActionView.this);
			}
		}

	};

	private OnClickListener externalOnClickListener = null;

	public RefreshProgressActionView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RefreshProgressActionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RefreshProgressActionView(Context context) {
		super(context);
		init();
	}

	private void init() {
		icon = new ImageView(getContext());
		icon.setImageResource(R.drawable.ic_menu_refresh);
		icon.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		icon.setOnClickListener(internalOnClickListener);
		addView(icon);

		progress = new ProgressBar(getContext(), null,
				android.R.attr.progressBarStyle);
		progress.setLayoutParams(new LayoutParams(dp(32), dp(32)));
		progress.setVisibility(View.INVISIBLE);
		addView(progress);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		externalOnClickListener = l;
	}

	// Durr Hurr Hurr!
	public boolean getBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
		if (this.busy) {
			progress.setVisibility(View.VISIBLE);
			icon.setVisibility(View.INVISIBLE);
		} else {
			progress.setVisibility(View.INVISIBLE);
			icon.setVisibility(View.VISIBLE);
		}
	}

	private int dp(int dpValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpValue, getResources().getDisplayMetrics());
	}

}
