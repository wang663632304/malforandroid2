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

package com.github.riotopsys.malforandroid2.adapter.SupplementaryText;

import android.content.Context;

import com.github.riotopsys.malforandroid2.R;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;
import com.github.riotopsys.malforandroid2.model.BaseRecord;

public class ProgressText implements SupplementaryTextFactory {

	@Override
	public String getSupplementaryText(Context ctx, BaseRecord br) {
		if ( br instanceof AnimeRecord){
			AnimeRecord ar = (AnimeRecord) br;
			return ctx.getString(R.string.watched_format, ar .watched_episodes, ar.episodes);
		} else {
			//TODO: add manga version
			return "";
		}
		
	}

}
