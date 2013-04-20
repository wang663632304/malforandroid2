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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Base64;
import android.util.Log;

public class RestHelper {

	private static final String TAG = RestHelper.class.getSimpleName();
	private String token = null;
	private String userAgent = null;

	public RestResult<String> get(URL url) {
		HttpURLConnection con = null;
		RestResult<String> result = new RestResult<String>();
		try {
			con = (HttpURLConnection) url.openConnection();
			setupBoilerPlate(con);

			result.result = readToString(con);
			result.code = con.getResponseCode();

		} catch (Exception e) {
			Log.e(TAG, "get", e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}

		return result;
	}

	private void setupBoilerPlate(HttpURLConnection con) {
		if (token != null) {
			con.addRequestProperty("Authorization", token);
		}
		if (userAgent != null) {
			con.addRequestProperty("User-Agent", userAgent);
		}
	}

	public RestResult<String> post(URL url, String data) {
		HttpURLConnection con = null;
		RestResult<String> result = new RestResult<String>();
		try {

			con = (HttpURLConnection) url.openConnection();
			setupBoilerPlate(con);

			con.setRequestMethod("POST");
			con.setDoOutput(true);

			OutputStreamWriter wr = new OutputStreamWriter(
					con.getOutputStream());
			if (data != null) {
				wr.write(data);
			}
			wr.flush();
			wr.close();

			result.result = readToString(con);
			result.code = con.getResponseCode();

		} catch (Exception e) {
			Log.e(TAG, "post", e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return result;
	}

	private String readToString(HttpURLConnection con) throws IOException {
		StringBuffer sb = new StringBuffer();
		InputStreamReader rd;
		if (con.getResponseCode() >= 400) {
			rd = new InputStreamReader(con.getErrorStream());
		} else {
			rd = new InputStreamReader(con.getInputStream());
		}
		char[] buf = new char[128];
		int read;
		while ((read = rd.read(buf)) > 0) {
			sb.append(buf, 0, read);
		}
		
		String result = sb.toString();
		Log.v(TAG, String.format("data %s", result));
		
		return result;
	}

	public RestResult<String> delete(URL url) {
		HttpURLConnection con = null;
		RestResult<String> result = new RestResult<String>();
		try {
			con = (HttpURLConnection) url.openConnection();
			setupBoilerPlate(con);

			con.setRequestMethod("DELETE");

			con.connect();

			result.code = con.getResponseCode();

		} catch (Exception e) {
			Log.e(TAG, "get", e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}

		return result;
	}

	public void setCredentials(String username, String password) {
		if (username == null || password == null) {
			return;
		}
		token = "Basic "
				+ Base64.encodeToString((username + ":" + password).getBytes(),
						Base64.DEFAULT | Base64.NO_WRAP);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void applyUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public RestResult<String> put(URL url, String data) {
		HttpURLConnection con = null;
		RestResult<String> result = new RestResult<String>();
		try {

			con = (HttpURLConnection) url.openConnection();
			setupBoilerPlate(con);

			con.setRequestMethod("PUT");
			con.setDoOutput(true);

			OutputStreamWriter wr = new OutputStreamWriter(
					con.getOutputStream());
			if (data != null) {
				wr.write(data);
			}
			wr.flush();
			wr.close();

			result.result = readToString(con);
			result.code = con.getResponseCode();

		} catch (Exception e) {
			Log.e(TAG, "post", e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return result;
	}

}