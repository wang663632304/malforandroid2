package com.github.riotopsys.malforandroid2;

import roboguice.RoboGuice;

public class Application extends android.app.Application {

	@Override
	public void onCreate() {
		RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE, 
				new CustomModule(),
				RoboGuice.newDefaultRoboModule(this));
	}

}
