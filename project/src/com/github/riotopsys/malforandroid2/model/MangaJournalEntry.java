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

package com.github.riotopsys.malforandroid2.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "manga_journal")
public class MangaJournalEntry {
	
	public MangaJournalEntry() {
	}

	public MangaJournalEntry(int recordId, UpdateType updateType) {
		this.recordId = recordId;
		this.updateType = updateType;
	}

	@DatabaseField(id = true)
	public int recordId;
	
	@DatabaseField( )
	public UpdateType updateType;
}
