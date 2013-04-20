package com.github.riotopsys.malforandroid2.fragment;

import roboguice.fragment.RoboDialogFragment;
import android.content.DialogInterface;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		view = (NumberPicker) inflater.inflate(R.layout.number_picker, container);
		
		getDialog().setTitle("Watched Episodes");
        
        view.setMinValue(0);
        view.setMaxValue(maximum);
        view.setValue(value);
        view.setWrapSelectorWheel(false);
        
        view.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        return view;
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
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		
		if ( listener != null ){
			value = view.getValue();
			
			listener.onDismiss(this);
		}
	}

	public void setOnDismissListener(OnDismissListener listener) {
		this.listener = listener;
	}

}
