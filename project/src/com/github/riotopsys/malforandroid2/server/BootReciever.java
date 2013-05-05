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
		PendingIntent pi = PendingIntent.getService(
				ctx.getApplicationContext(), 0,
				ServerInterface.getSyncIntent(ctx),
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager am = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);

		am.cancel(pi);
		am.setInexactRepeating(AlarmManager.RTC, 
				System.currentTimeMillis(), 
				AlarmManager.INTERVAL_HOUR, 
				pi);
		
		state.setSyncScheduled(true);
	}
	
}
