/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.asset.auto.tagger.google.cloud.natural.language.internal;

import com.liferay.petra.string.CharPool;

import com.liferay.portal.asset.auto.tagger.google.cloud.natural.language.api.GCloudNaturalLanguageDocumentAssetAutoTagger;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.net.HttpURLConnection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Cristina González
 */
@Component(service = GCloudNaturalLanguageDocumentAssetAutoTagger.class)
public class GCloudNaturalLanguageDocumentAssetAutoTaggerImpl
	implements GCloudNaturalLanguageDocumentAssetAutoTagger {

	@Reference
	private Http _http;

	private static <T> Predicate<T> _negate(Predicate<T> predicate) {
		return predicate.negate();
	}

	@Override
	public Collection<String> getClassificationTagNames(
			String apiKey, float confidence, String content)
		throws Exception {

		Set<String> tagNames = new HashSet<>();

		JSONObject responseJSONObject = _post(
			_getServiceURL(apiKey, "classifyText"), content);

		_processTagNames(
			responseJSONObject.getJSONArray("categories"),
			jsonObject -> jsonObject.getDouble("confidence") > confidence,
			tagNames::add);

		return tagNames;
	}

	private String _getServiceURL(String apiKey, String endpoint) {
		return StringBundler.concat(
			"https://language.googleapis.com/v1/documents:", endpoint, "?key=",
			apiKey);
	}

	private JSONObject _post(String serviceURL, String body) throws Exception {
		Http.Options options = new Http.Options();

		options.setBody(body, ContentTypes.APPLICATION_JSON, StringPool.UTF8);
		options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
		options.setLocation(serviceURL);
		options.setPost(true);

		String responseJSON = _http.URLtoString(options);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(responseJSON);

		Http.Response response = options.getResponse();

		if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return jsonObject;
		}

		JSONObject errorJSONObject = jsonObject.getJSONObject("error");

		String errorMessage = responseJSON;

		if (errorJSONObject != null) {
			errorMessage = errorJSONObject.getString("message");
		}

		throw new PortalException(
			StringBundler.concat(
				"Unable to generate tags with the Google Natural Language ",
				"service. Response code ", response.getResponseCode(), ": ",
				errorMessage));
	}

	private void _processTagNames(
		JSONArray jsonArray, Predicate<JSONObject> predicate,
		Consumer<String> consumer) {

		if (jsonArray == null) {
			return;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (predicate.test(jsonObject)) {
				String tagName = StringUtil.removeChars(
					jsonObject.getString("name"), CharPool.APOSTROPHE,
					CharPool.DASH);

				Stream.of(
					StringUtil.split(tagName, CharPool.AMPERSAND)
				).flatMap(
					tagNamePart -> Stream.of(
						StringUtil.split(tagNamePart, CharPool.FORWARD_SLASH))
				).map(
					String::trim
				).filter(
					_negate(String::isEmpty)
				).forEach(
					consumer
				);
			}
		}
	}


}