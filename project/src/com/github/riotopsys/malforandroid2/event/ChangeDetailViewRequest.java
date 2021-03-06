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

package com.github.riotopsys.malforandroid2.event;

import java.io.Serializable;


public abstract class ChangeDetailViewRequest implements Serializable {

	private static final long serialVersionUID = 3599241799270950826L;
	
	public int id;

	public ChangeDetailViewRequest(int id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object o) {
		if ( o instanceof ChangeDetailViewRequest ){
			return id == ((ChangeDetailViewRequest)o).id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Integer.valueOf(id).hashCode();
	}

}
