package com.github.riotopsys.malforandroid2.model;

import com.j256.ormlite.field.DatabaseField;

public class BaseRecord {
	
	@DatabaseField
	public String title;
	
	@DatabaseField(id = true)
	public int id;
	
	@DatabaseField( )
	public String synopsis;
	
	@DatabaseField
	public String image_url;
	
	@DatabaseField( )
	public int rank;
	
	@DatabaseField()
	public int score;

}
