
package com.github.riotopsys.malforandroid2.server;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class UrlBuilder {
	private String baseUrl;

	public UrlBuilder() {
		baseUrl = "http://mal-api.com";
	}

	public URL getAnimeListUrl(String username) throws MalformedURLException {
		return new URL( String.format("%s/animelist/%s?mine=1", baseUrl, username));
	}

	
	public URL getAnimeUpdateUrl(int id) throws MalformedURLException {
		return new URL( String.format("%s/animelist/anime/%d", baseUrl, id));
	}

	public URL getAnimeRecordUrl(int id) throws MalformedURLException {
		return new URL( String.format("%s/anime/%d?mine=1", baseUrl, id));
	}

	public URL getVerifyCredentialsUrl() throws MalformedURLException {
		return new URL( String.format("%s/account/verify_credentials", baseUrl) );
	}

	public String getSearchUrl(String criteria) throws UnsupportedEncodingException {
		return String.format("%s/anime/search?q=%s", baseUrl, URLEncoder.encode(criteria, "utf-8"));
	}

}