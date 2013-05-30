package com.github.riotopsys.malforandroid2.server.provider;

import com.github.riotopsys.malforandroid2.server.tasks.AnimeUpdateTask;

public class AnimeUpdateTaskProvider extends
		InjectorAssistedProvider<AnimeUpdateTask> {
	
	@Override
	public AnimeUpdateTask get() {
		return injectMembers(new AnimeUpdateTask());
	}

}
