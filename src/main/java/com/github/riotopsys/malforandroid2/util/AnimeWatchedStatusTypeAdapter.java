package com.github.riotopsys.malforandroid2.util;

import java.io.IOException;

import com.github.riotopsys.malforandroid2.model.AnimeWatchedStatus;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class AnimeWatchedStatusTypeAdapter extends TypeAdapter<AnimeWatchedStatus> {

	@Override
	public AnimeWatchedStatus read(JsonReader in) throws IOException {
		return AnimeWatchedStatus.getByServerKey( in.nextString());
	}


	@Override
	public void write(JsonWriter out, AnimeWatchedStatus value)
			throws IOException {
		out.value(value.getServerKey());
	}

}
