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

package com.github.riotopsys.malforandroid2.server;

import com.github.riotopsys.malforandroid2.GlobalState;
import com.google.inject.Inject;

import roboguice.receiver.RoboBroadcastReceiver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class BootReciever extends RoboBroadcastReceiver {
	
	@Inject
	private GlobalState state; 
	
	@Override
	protected void handleReceive(Context ctx, Intent intent) {
		
		scheduleSync(state, ctx);

	}

	//initializes the sync system so the user does not need to enter app on reboot
	public static void scheduleSync(GlobalState state, Context ctx) {
		PendingIntent piAnime = PendingIntent.getService(
				ctx.getApplicationContext(), 0,
				AnimeServerInterface.getSyncIntent(ctx),
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		PendingIntent piManga = PendingIntent.getService(
				ctx.getApplicationContext(), 0,
				AnimeServerInterface.getSyncIntent(ctx),
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager am = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);

		am.cancel(piAnime);
		am.cancel(piManga);
		
		am.setInexactRepeating(AlarmManager.RTC, 
				System.currentTimeMillis(), 
				AlarmManager.INTERVAL_HOUR, 
				piAnime);
		am.setInexactRepeating(AlarmManager.RTC, 
				System.currentTimeMillis(), 
				AlarmManager.INTERVAL_HOUR, 
				piManga);
		
		state.setSyncScheduled(true);
	}
	
}
