package com.github.riotopsys.malforandroid2.server.background_tasks.provider;

import com.github.riotopsys.malforandroid2.server.background_tasks.VerifyCredentialsTask;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class VerifyCredentialsTaskProvider implements
		Provider<VerifyCredentialsTask> {
	
	@Inject
    private Injector injector;

	@Override
	public VerifyCredentialsTask get() {
		VerifyCredentialsTask vct = new VerifyCredentialsTask();
		injector.injectMembers(vct);
		return vct;
	}

}
