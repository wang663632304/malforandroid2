package com.github.riotopsys.malforandroid2;

import com.github.riotopsys.malforandroid2.server.RestHelper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class CustomModule extends AbstractModule {

	@Override
	protected void configure() {
		
	}
	
	
	@Provides
	@Singleton
	public Bus provideBus(){
		return new Bus( ThreadEnforcer.ANY );
	}
	
	@Provides
	@Singleton
	public RestHelper provideRestHelper(){
		return new RestHelper();
	}
}
