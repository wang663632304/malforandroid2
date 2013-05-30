package com.github.riotopsys.malforandroid2.server.retrofit;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
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
	public Response update(
			@Header("Authorization") String auth,
			@Path("id") int id, 
			@Field("status") String status,
			@Field("episodes") int episodes, 
			@Field("score") int score);

	@FormUrlEncoded
	@DELETE("/animelist/anime/{id}")
	public Response delete(
			@Header("Authorization") String auth,
			@Path("id") int id);

	@GET("/anime/{id}?mine=1")
	public AnimeRecord get(
			@Header("Authorization") String auth,
			@Path("id") int id);

	@GET("/anime/search")
	public List<AnimeRecord> search(@Query("q") String query);

	@FormUrlEncoded
	@POST("/animelist/anime")
	public Response add(
			@Header("Authorization") String auth,
			@Field("anime_id") int id, 
			@Field("status") String status,
			@Field("episodes") int episodes, 
			@Field("score") int score);

	@GET("/account/verify_credentials")
	public Response verifyCredentials(@Header("Authorization") String auth);

}
