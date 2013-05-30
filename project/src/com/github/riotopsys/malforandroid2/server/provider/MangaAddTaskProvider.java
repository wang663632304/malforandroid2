package com.github.riotopsys.malforandroid2.server.provider;

import com.github.riotopsys.malforandroid2.server.tasks.MangaAddTask;

public class MangaAddTaskProvider extends
		InjectorAssistedProvider<MangaAddTask> {
	
	@Override
	public MangaAddTask get() {
		return injectMembers(new MangaAddTask());
	}

}
