package com.github.riotopsys.malforandroid2.server.retrofit;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

import com.github.riotopsys.malforandroid2.model.AnimeListResponse;
import com.github.riotopsys.malforandroid2.model.AnimeRecord;

public interface AnimeInterconnect {

	@GET("/animelist/{user}")
	public AnimeListResponse getUsersList(@Path("user") String user);

	@FormUrlEncoded
	@PUT("/animelist/anime/{id}")
	public Response update(@Path("id") int id, @Field("status") String status,
			@Field("episodes") int episodes, @Field("score") int score);

	@FormUrlEncoded
	@DELETE("/animelist/anime/{id}")
	public Response delete(@Path("id") int id);

	@GET("/anime/{id}?mine=1")
	public AnimeRecord getAnime(@Path("id") int id);

	@GET("/anime/search")
	public List<AnimeRecord> search(@Query("q") String query);

	@FormUrlEncoded
	@POST("/animelist/anime/{id}")
	public Response add(@Field("id") int id, @Field("status") String status,
			@Field("episodes") int episodes, @Field("score") int score);

	@POST("/account/verify_credentials")
	public Response verifyCredentials();

}
