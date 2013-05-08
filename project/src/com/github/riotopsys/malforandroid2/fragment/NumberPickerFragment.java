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

package com.github.riotopsys.malforandroid2.fragment;

import roboguice.fragment.RoboDialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.github.riotopsys.malforandroid2.R;

public class NumberPickerFragment extends RoboDialogFragment {

	private int maximum;
	private int value;
	private OnDismissListener listener = null;
	private NumberPicker view;
	
	public interface OnDismissListener{
		public void onDismiss( NumberPickerFragment numberPickerFragment);
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
       
		view = (NumberPicker) View.inflate(getActivity(), R.layout.number_picker, null);
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
        .setView(view)
        .setTitle("Watched Episodes")
        .setPositiveButton("Ok", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				value = view.getValue();
				
				listener.onDismiss(NumberPickerFragment.this);
				dialog.dismiss();
			}
		})
        .setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
        
        view.setMinValue(0);
        view.setMaxValue(maximum);
        view.setValue(value);
        view.setWrapSelectorWheel(false);
        
        view.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        
        return builder.create();
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return null;
    }

	public void setMaximum(int number) {
		maximum = number;
	}
	
	public void setValue(int number) {
		value = number;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setOnDismissListener(OnDismissListener listener) {
		this.listener = listener;
	}
	
}
