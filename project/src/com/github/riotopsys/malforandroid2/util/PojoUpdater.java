/**
 * Copyright 2013 C. A. Fitzgerald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

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
