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

import java.util.LinkedList;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "manga")
public class MangaRecord extends BaseRecord{

	@DatabaseField
	public String type;
	
	@DatabaseField
	public String status;
	
	@DatabaseField
	public int volumes;
	
	@DatabaseField
	public int chapters;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<String> genres;
	
	@DatabaseField
	public float members_score;
	
	@DatabaseField
	public int members_count;
	
	@DatabaseField
	public int popularity_rank;
	
	@DatabaseField
	public int favorited_count;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<String> tags;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<AnimeCrossReferance> anime_adaptations;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<MangaCrossReferance> related_manga;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<MangaCrossReferance> alternative_versions;
	
	@DatabaseField()
	public MangaReadStatus read_status;
	
	@DatabaseField()
	public int listed_manga_id;
	
	@DatabaseField()
	public int chapters_read;
	
	@DatabaseField()
	public int volumes_read;
	
}
