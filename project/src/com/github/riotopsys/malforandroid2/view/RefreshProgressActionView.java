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
