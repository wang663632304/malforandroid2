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

import java.util.HashMap;
import java.util.LinkedList;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "anime")
public class AnimeRecord extends BaseRecord{

	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<AnimeCrossReferance> sequels;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<String> tags;
	 
	@DatabaseField( )
	public String status;
	
	@DatabaseField( )
	public String type;
	
	@DatabaseField( )
	public int listed_anime_id;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<AnimeCrossReferance> side_stories;
	
	@DatabaseField( )
	public String classification;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<String> genres;
	
	@DatabaseField( )
	public int watched_episodes;
	
	@DatabaseField( )
	public float members_score;
	
	@DatabaseField( dataType = DataType.SERIALIZABLE )
	public HashMap<String, LinkedList<String>> other_titles;
	
	@DatabaseField( )
	public AnimeWatchedStatus watched_status;
	
	@DatabaseField( )
	public int members_count;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<MangaCrossReferance> manga_adaptations;
	
	@DatabaseField( )
	public int favorited_count;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<AnimeCrossReferance> prequels;
	
	@DatabaseField( )
	public int popularity_rank;
	
	@DatabaseField( )
	public int episodes;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<AnimeCrossReferance> spin_offs;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<AnimeCrossReferance> summaries;
    
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	public LinkedList<AnimeCrossReferance> alternative_versions;

}