package com.github.riotopsys.malforandroid2.model;

import com.j256.ormlite.field.DatabaseField;

public class BaseJournalEntry {

	@DatabaseField(id = true)
	public int recordId;
	
	@DatabaseField( )
	public UpdateType updateType;

}
