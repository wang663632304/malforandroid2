package com.github.riotopsys.malforandroid2.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "journal")
public class JournalEntry {
	
	public JournalEntry() {
	}

	public JournalEntry(int recordId, UpdateType updateType) {
		this.recordId = recordId;
		this.updateType = updateType;
	}

	@DatabaseField(id = true)
	public int recordId;
	
	@DatabaseField( )
	public UpdateType updateType;
}
