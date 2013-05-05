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
import com.github.riotopsys.malforandroid2.model.AnimeWatchedStatus;

public class WatchedStatusText implements SupplementaryTextFactory {

	@Override
	public String getSupplementaryText(Context ctx, AnimeRecord ar) {
		if ( ar.watched_status == null ){
			return ctx.getString(R.string.not_in_list);
		}
		if ( ar.watched_status == AnimeWatchedStatus.WATCHING ){
			return ctx.getString(R.string.watched_format, ar.watched_episodes, ar.episodes);			
		}
		return ctx.getString( ar.watched_status.getResource() );
	}

}
