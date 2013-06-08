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
		return new URL( String.format("http://myanimelist.net/malappinfo.php?u=%s&status=all&type=anime", username));
	}
	
	public URL getMangaListUrl(String username) throws MalformedURLException {
		return new URL( String.format("http://myanimelist.net/malappinfo.php?u=%s&status=all&type=manga", username));
	}

	//same structure required for delete
	public URL getAnimeUpdateUrl(int id) throws MalformedURLException {
		return new URL( String.format("%s/animelist/anime/%d", baseUrl, id));
	}

	public URL getAnimeRecordUrl(int id) throws MalformedURLException {
		return new URL( String.format("%s/anime/%d?mine=1", baseUrl, id));
	}

	public URL getVerifyCredentialsUrl() throws MalformedURLException {
		return new URL( String.format("%s/account/verify_credentials", baseUrl) );
	}

	public URL getAnimeSearchUrl(String criteria) throws UnsupportedEncodingException, MalformedURLException {
		return new URL( String.format("%s/anime/search?q=%s", baseUrl, URLEncoder.encode(criteria, "utf-8")));
	}

	public URL getAnimeAddUrl() throws MalformedURLException {
		return new URL( String.format("%s/animelist/anime", baseUrl));
	}

	public URL getMangaAddUrl() throws MalformedURLException {
		return new URL( String.format("%s/mangalist/manga", baseUrl));
	}

	public URL getMangaUpdateUrl(int id) throws MalformedURLException {
		return new URL( String.format("%s/mangalist/manga/%d", baseUrl, id));
	}

	public URL getMangaRecordUrl(int id) throws MalformedURLException {
		return new URL( String.format("%s/manga/%d?mine=1", baseUrl, id));
	}

	public URL getMangaSearchUrl(String criteria) throws MalformedURLException, UnsupportedEncodingException {
		return new URL( String.format("%s/manga/search?q=%s", baseUrl, URLEncoder.encode(criteria, "utf-8")));
	}

}