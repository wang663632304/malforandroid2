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

public interface MangaInterconnect {

	@GET("/mangalist/{user}")
	public AnimeListResponse getUsersList(@Path("user") String user);

	@FormUrlEncoded
	@PUT("/mangalist/manga/{id}")
	public Response update(
			@Header("Authorization") String auth,
			@Path("id") int id, 
			@Field("status") String status,
			@Field("chapters") int chapters, 
			@Field("volumes") int volumes, 
			@Field("score") int score);

	@FormUrlEncoded
	@DELETE("/mangalist/manga/{id}")
	public Response delete(
			@Header("Authorization") String auth,
			@Path("id") int id);

	@GET("/manga/{id}?mine=1")
	public AnimeRecord getAnime(
			@Header("Authorization") String auth,
			@Path("id") int id);

	@GET("/manga/search")
	public List<AnimeRecord> search(@Query("q") String query);

	@FormUrlEncoded
	@POST("/mangalist/manga/{id}")
	public Response add(
			@Header("Authorization") String auth,
			@Field("manga_id") int id, 
			@Field("status") String status,
			@Field("chapters") int chapters, 
			@Field("volumes") int volumes, 
			@Field("score") int score);

	@POST("/account/verify_credentials")
	public Response verifyCredentials(@Header("Authorization") String auth);

}
