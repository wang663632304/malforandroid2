package com.github.riotopsys.malforandroid2.server.provider;

import com.github.riotopsys.malforandroid2.server.tasks.VerifyCredentialsTask;

public class VerifyCredentialsTaskProvider extends
		InjectorAssistedProvider<VerifyCredentialsTask> {
	
	@Override
	public VerifyCredentialsTask get() {
		return injectMembers(new VerifyCredentialsTask());
	}

}
