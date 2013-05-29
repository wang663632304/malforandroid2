package com.github.riotopsys.malforandroid2.server.provider;

import com.github.riotopsys.malforandroid2.server.tasks.MangaUpdateTask;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class MangaUpdateTaskProvider implements
		Provider<MangaUpdateTask> {
	
	@Inject
    private Injector injector;

	@Override
	public MangaUpdateTask get() {
		MangaUpdateTask task = new MangaUpdateTask();
		injector.injectMembers(task);
		return task;
	}

}
