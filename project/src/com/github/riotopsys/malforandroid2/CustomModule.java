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

package com.github.riotopsys.malforandroid2;

import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import com.github.riotopsys.malforandroid2.model.AnimeWatchedStatus;
import com.github.riotopsys.malforandroid2.model.BaseRecord;
import com.github.riotopsys.malforandroid2.model.MangaReadStatus;
import com.github.riotopsys.malforandroid2.server.provider.AnimeAddTaskProvider;
import com.github.riotopsys.malforandroid2.server.provider.AnimeUpdateTaskProvider;
import com.github.riotopsys.malforandroid2.server.provider.MangaAddTaskProvider;
import com.github.riotopsys.malforandroid2.server.provider.MangaUpdateTaskProvider;
import com.github.riotopsys.malforandroid2.server.provider.VerifyCredentialsTaskProvider;
import com.github.riotopsys.malforandroid2.server.retrofit.AnimeInterconnect;
import com.github.riotopsys.malforandroid2.server.retrofit.MangaInterconnect;
import com.github.riotopsys.malforandroid2.server.tasks.AnimeAddTask;
import com.github.riotopsys.malforandroid2.server.tasks.AnimeUpdateTask;
import com.github.riotopsys.malforandroid2.server.tasks.MangaAddTask;
import com.github.riotopsys.malforandroid2.server.tasks.MangaUpdateTask;
import com.github.riotopsys.malforandroid2.server.tasks.VerifyCredentialsTask;
import com.github.riotopsys.malforandroid2.util.AnimeWatchedStatusTypeAdapter;
import com.github.riotopsys.malforandroid2.util.MangaReadStatusTypeAdapter;
import com.github.riotopsys.malforandroid2.util.RecordTitleComparator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

public class CustomModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(new TypeLiteral<Comparator<BaseRecord>>(){}).to(RecordTitleComparator.class);
		bind(VerifyCredentialsTask.class).toProvider(VerifyCredentialsTaskProvider.class);
		bind(AnimeUpdateTask.class).toProvider(AnimeUpdateTaskProvider.class);
		bind(AnimeAddTask.class).toProvider(AnimeAddTaskProvider.class);
		bind(MangaUpdateTask.class).toProvider(MangaUpdateTaskProvider.class);
		bind(MangaAddTask.class).toProvider(MangaAddTaskProvider.class);
	}

	@Provides
	@Singleton
	public EventBus provideBus() {
		return new EventBus();
	}

	@Provides
	@Singleton
	public GlobalState provideGlobalState(){
		return new GlobalState();
	}
	
	@Provides
	public ImageLoader provideImageLoader(){
		return ImageLoader.getInstance();
	}

	@Provides
	public Gson provideGson() {
		return new GsonBuilder()
			.registerTypeAdapter(AnimeWatchedStatus.class,new AnimeWatchedStatusTypeAdapter())
			.registerTypeAdapter(MangaReadStatus.class,new MangaReadStatusTypeAdapter())
			.create();
	}
	
	@Provides
	public AnimeInterconnect provideAnimeInterconnect(){
		RestAdapter restAdapter = provideRestAdapter();
		return restAdapter.create(AnimeInterconnect.class);
	}
	
	@Provides
	public MangaInterconnect provideMangaInterconnect(){
		RestAdapter restAdapter = provideRestAdapter();
		return restAdapter.create(MangaInterconnect.class);
	}
	
	private RestAdapter provideRestAdapter() {
		return new RestAdapter.Builder()
			.setServer("http://mal-api.com")
			.setConverter(new GsonConverter(provideGson()))
			.build();
	}

	@Provides
	@Singleton
	public Executor provideExecutor(){
		return Executors.newCachedThreadPool();
	}
}
