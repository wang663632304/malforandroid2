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

package com.github.riotopsys.malforandroid2.util;

import com.github.riotopsys.malforandroid2.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

public class Apprater {
	
	private static final String COUNT = "count";
	private static final String INITIAL = "initial";
	private static final String RESOLVED = "resolved";

	public void onAppOpened(final Context ctx){
		SharedPreferences prefs = ctx.getSharedPreferences("Apprater", Context.MODE_PRIVATE);
		
		long initialStart = prefs.getLong(INITIAL, 0);
		boolean resolved = prefs.getBoolean(RESOLVED, false);
		int launchCount = prefs.getInt(COUNT, 0);
		
		long now = System.currentTimeMillis();
		
		if (initialStart == 0 ){
			Editor editor = prefs.edit();
			initialStart = now;
			editor.putLong(INITIAL, initialStart);
			editor.commit();
		}
		
		if (!resolved){
			
			launchCount++;
			Editor editor = prefs.edit();
			editor.putInt(COUNT, launchCount);
			editor.commit();
			
			if ( (launchCount >= 7) && (now >= (initialStart + 5*24*60*60*1000)) ){
				new AlertDialog.Builder(ctx)
					.setTitle(R.string.app_rate_title)
					.setMessage(R.string.app_rate_message)
					.setPositiveButton(android.R.string.ok, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.github.riotopsys.malforandroid2")));
							
							Editor editor = ctx.getSharedPreferences("Apprater", Context.MODE_PRIVATE).edit();
							editor.putBoolean(RESOLVED, true);
							editor.commit();
							
			                dialog.dismiss();
						}
					})
					.setNeutralButton(R.string.later, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							Editor editor = ctx.getSharedPreferences("Apprater", Context.MODE_PRIVATE).edit();
							editor.putLong(INITIAL, System.currentTimeMillis());
							editor.putInt(COUNT, 0);
							editor.commit();
							
			                dialog.dismiss();
						}
					})
					.setNegativeButton(android.R.string.no, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {

							Editor editor = ctx.getSharedPreferences("Apprater", Context.MODE_PRIVATE).edit();
							editor.putBoolean(RESOLVED, true);
							editor.commit();
							
			                dialog.dismiss();
							
						}
					}).create().show();
			}
		}
	}
}
