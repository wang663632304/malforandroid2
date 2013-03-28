package com.github.riotopsys.malforandroid2;

import com.github.riotopsys.malforandroid2.server.RestHelper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import de.greenrobot.event.EventBus;

public class CustomModule extends AbstractModule {

	@Override
	protected void configure() {
		
	}
	
	@Provides
	@Singleton
	public EventBus provideBus(){
		return new EventBus();
	}
	
	@Provides
	@Singleton
	public RestHelper provideRestHelper(){
		return new RestHelper();
	}
}
