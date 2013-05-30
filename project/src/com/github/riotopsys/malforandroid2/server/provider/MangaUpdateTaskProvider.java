package com.github.riotopsys.malforandroid2.server.provider;

import com.github.riotopsys.malforandroid2.server.tasks.MangaUpdateTask;

public class MangaUpdateTaskProvider extends
		InjectorAssistedProvider<MangaUpdateTask> {
	
	@Override
	public MangaUpdateTask get() {
		return injectMembers(new MangaUpdateTask());
	}

}
