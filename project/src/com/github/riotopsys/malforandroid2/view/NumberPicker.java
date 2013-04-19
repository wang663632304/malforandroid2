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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.riotopsys.malforandroid2.R;

public class NumberPicker extends FrameLayout implements OnClickListener {

	private TextView positive;
	private TextView negative;
	private TextView readout;
	
	private int currentCount = 0;
	private int maximumCount = 0;

	public NumberPicker(Context context) {
		super(context);
		inflateLayout(context);
	}

	public NumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflateLayout(context);
	}

	public NumberPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inflateLayout(context);
	}
	
	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
		fixOutput();
	}
	
	public void setMaximumCount(int maximumCount) {
		this.maximumCount = maximumCount;
		fixOutput();
	}
	
	public int getCurrentCount() {
		return currentCount;
	}
	
	public int getMaximumCount() {
		return maximumCount;
	}
	
	private void inflateLayout(Context ctx) {
		LayoutInflater layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.number_picker, this);
		positive = (TextView) view.findViewById(R.id.positive_button); 
		negative = (TextView) view.findViewById(R.id.negative_button); 
		readout = (TextView) view.findViewById(R.id.readout);
		
		positive.setOnClickListener(this);
		negative.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch( v.getId() ){
		case R.id.negative_button:
			currentCount -= 1;
			break;
		case R.id.positive_button:
			currentCount += 1;
			break;
		}
		if ( maximumCount != 0  && ( currentCount > maximumCount )) {
			currentCount = maximumCount;
		}
		if (  currentCount < 0  ) {
			currentCount = 0;
		}
		fixOutput();
	}

	private void fixOutput() {
		if ( maximumCount != 0 ) {
			readout.setText(String.format("%d / %d",currentCount, maximumCount));
		} else {
			readout.setText(String.format("%d",currentCount));
		}
	}
}
