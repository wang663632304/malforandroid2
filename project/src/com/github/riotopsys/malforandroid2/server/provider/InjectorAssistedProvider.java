package com.github.riotopsys.malforandroid2.server.provider;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public abstract class InjectorAssistedProvider<T> implements
		Provider<T> {
	
	@Inject
    private Injector injector;

	protected T injectMembers( T t) {
		injector.injectMembers(t);
		return t;
	}

}
