package com.github.riotopsys.malforandroid2.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

public class PojoUpdater<T extends Object> {

	private static final String TAG = PojoUpdater.class.getSimpleName();
	
	private List<Field> fields = null;

	public void update(T original, T partial) {
		if (fields == null) {
			fields = buildFields(original);
		}
		for (Field f : fields) {
			try {
				if (f.get(partial) != null) {
					f.set(original, partial);
				}
			} catch (Exception e) {
				Log.e(TAG, "", e);
			}
		}
	}

	private List<Field> buildFields(T t) {
		List<Field> fields = new LinkedList<Field>();

		for (Field f : t.getClass().getDeclaredFields()) {
			if (Modifier.isPublic(f.getModifiers())) {
				fields.add(f);
			}
		}
		return fields;
	}
}
