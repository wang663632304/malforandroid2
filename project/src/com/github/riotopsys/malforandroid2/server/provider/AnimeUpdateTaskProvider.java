package com.github.riotopsys.malforandroid2.server.provider;

import com.github.riotopsys.malforandroid2.server.tasks.AnimeUpdateTask;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class AnimeUpdateTaskProvider implements
		Provider<AnimeUpdateTask> {
	
	@Inject
    private Injector injector;

	@Override
	public AnimeUpdateTask get() {
		AnimeUpdateTask task = new AnimeUpdateTask();
		injector.injectMembers(task);
		return task;
	}

}
