package com.github.riotopsys.malforandroid2.server.provider;

import com.github.riotopsys.malforandroid2.server.tasks.AnimeAddTask;

public class AnimeAddTaskProvider extends
		InjectorAssistedProvider<AnimeAddTask> {
	
	@Override
	public AnimeAddTask get() {
		return injectMembers(new AnimeAddTask());
	}

}
