package com.github.riotopsys.malforandroid2;

import com.github.riotopsys.malforandroid2.model.AnimeWatchedStatus;
import com.github.riotopsys.malforandroid2.server.RestHelper;
import com.github.riotopsys.malforandroid2.util.AnimeWatchedStatusTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

public class CustomModule extends AbstractModule {

	@Override
	protected void configure() {

	}

	@Provides
	@Singleton
	public EventBus provideBus() {
		return new EventBus();
	}

	@Provides
	@Singleton
	public RestHelper provideRestHelper() {
		return new RestHelper();
	}
	
	@Provides
	public ImageLoader provideImageLoader(){
		return ImageLoader.getInstance();
	}

	@Provides
	public Gson provideGson() {
		return new GsonBuilder().registerTypeAdapter(AnimeWatchedStatus.class,
				new AnimeWatchedStatusTypeAdapter()).create();
	}
}
