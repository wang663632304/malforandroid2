package com.github.riotopsys.malforandroid2.model;

import com.j256.ormlite.field.DatabaseField;

public class BaseRecord {
	
	@DatabaseField(id = true)
	public int id;
	
	@DatabaseField( )
	public String synopsis;

}
