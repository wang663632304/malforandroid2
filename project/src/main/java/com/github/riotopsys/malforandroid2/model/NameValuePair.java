package com.github.riotopsys.malforandroid2.model;

import java.io.Serializable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "name_value_pairs")
public class NameValuePair<T extends Serializable> {

	@DatabaseField(id = true)
	public String name;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public T value;
	
	public NameValuePair(String name, T value) {
		this.name = name;
		this.value = value;
	}
	
	public NameValuePair() {}
	
}
