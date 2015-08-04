/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import com.android.volley.*;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * A request for retrieving a T type response body at a given URL that also
 * optionally sends along a JSON body in the request specified.
 *
 * @param <T> JSON type of response expected
 */
public abstract class GsonRequest<T> extends Request<T> {
	/**
	 * Charset for request.
	 */
	private static final String PROTOCOL_CHARSET = "utf-8";

	/**
	 * Content type for request.
	 */
	private static final String PROTOCOL_CONTENT_TYPE =
			String.format("application/json; charset=%s", PROTOCOL_CHARSET);

	private final Listener<T> mListener;
	private final String mRequestBody;
	private final Class<T> mGsonClass;

	public GsonRequest(int method, String url, String requestBody, Class<T> gsonClass, Listener<T> listener,
	                   ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
		mRequestBody = requestBody;
		mGsonClass = gsonClass;
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	/**
	 * @deprecated Use {@link #getBodyContentType()}.
	 */
	@Override
	public String getPostBodyContentType() {
		return getBodyContentType();
	}

	/**
	 * @deprecated Use {@link #getBody()}.
	 */
	@Override
	public byte[] getPostBody() {
		return getBody();
	}

	@Override
	public String getBodyContentType() {
		return PROTOCOL_CONTENT_TYPE;
	}

	@Override
	public byte[] getBody() {
		try {
			return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
		} catch (UnsupportedEncodingException uee) {
			VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
					mRequestBody, PROTOCOL_CHARSET);
			return null;
		}
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {

            System.gc();

            com.google.gson.stream.JsonReader jsonReader = new com.google.gson.stream.JsonReader(new InputStreamReader(new ByteArrayInputStream(response.data)));

//			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			GsonBuilder builder = new GsonBuilder();
			builder.excludeFieldsWithoutExposeAnnotation();
			Gson gson = builder.create();
			T responseGson = gson.fromJson(jsonReader, mGsonClass);

            response.data = null;
            System.gc();

			return Response.success(responseGson, HttpHeaderParser.parseCacheHeaders(response));

		} catch (Exception e) {
			return Response.error(new ParseError(e));
		}
	}
}
