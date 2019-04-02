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

package com.liferay.asset.auto.tagger.google.cloud.natural.language.internal;

import com.liferay.asset.auto.tagger.google.cloud.natural.language.api.GCloudNaturalLanguageDocumentAssetAutoTagger;
import com.liferay.asset.auto.tagger.google.cloud.natural.language.api.configuration.GCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration;
import com.liferay.asset.auto.tagger.google.cloud.natural.language.api.constants.GCloudNaturalLanguageAssetAutoTagProviderConstants;
import com.liferay.asset.auto.tagger.google.cloud.natural.language.internal.util.GCloudNaturalLanguageUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;

import java.net.HttpURLConnection;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = GCloudNaturalLanguageDocumentAssetAutoTagger.class)
public class GCloudNaturalLanguageDocumentAssetAutoTaggerImpl
	implements GCloudNaturalLanguageDocumentAssetAutoTagger {

	@Override
	public Collection<String> getClassificationTagNames(
		GCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration
			gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration,
		String content)
		throws Exception {

		JSONObject responseJSONObject = _post(
			_getServiceURL(
				gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
					apiKey(),
				"classifyText"),
			content);

		return _toTagNames(
			responseJSONObject.getJSONArray("categories"),
			jsonObject -> jsonObject.getDouble("confidence") >
						  gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.confidence());
	}

	@Override
	public Collection<String> getEntityTagNames(
			GCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration
				gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration,
			String content)
		throws Exception {

		JSONObject responseJSONObject = _post(
			_getServiceURL(
				gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
					apiKey(),
				"analyzeEntities"),
			content);

		return _toTagNames(
			responseJSONObject.getJSONArray("entities"),
			jsonObject -> jsonObject.getDouble("salience") >
						  gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.salience());
	}

	private static final int _MINIMUM_PAYLOAD_SIZE;

	private static <T> Predicate<T> _negate(Predicate<T> predicate) {
		return predicate.negate();
	}

	private String _getServiceURL(String apiKey, String endpoint) {
		return StringBundler.concat(
			"https://language.googleapis.com/v1/documents:", endpoint, "?key=",
			apiKey);
	}

	static {
		String payload = GCloudNaturalLanguageUtil.getDocumentPayload(
			StringPool.BLANK, StringPool.BLANK);

		_MINIMUM_PAYLOAD_SIZE = payload.length();
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

	private Set<String> _toTagNames(
		JSONArray jsonArray, Predicate<JSONObject> predicate) {

		if (jsonArray == null) {
			return Collections.emptySet();
		}

		return StreamSupport.stream(
			(Spliterator<JSONObject>)jsonArray.spliterator(), false
		).filter(
			predicate
		).map(
			jsonObject -> StringUtil.removeChars(
				jsonObject.getString("name"), CharPool.APOSTROPHE,
				CharPool.DASH)
		).map(
			tagName -> StringUtil.split(tagName, CharPool.AMPERSAND)
		).flatMap(
			Stream::of
		).map(
			tagNamePart -> StringUtil.split(tagNamePart, CharPool.FORWARD_SLASH)
		).flatMap(
			Stream::of
		).map(
			String::trim
		).filter(
			_negate(String::isEmpty)
		).collect(
			Collectors.toSet()
		);
	}

	@Override
	public String getTruncatedContent(String mimeType, String content) {
		String type = _getType(mimeType);

		int size =
			GCloudNaturalLanguageAssetAutoTagProviderConstants.
				MAX_CHARACTERS_SERVICE - _MINIMUM_PAYLOAD_SIZE - type.length();

		return GCloudNaturalLanguageUtil.truncateToSize(content, size);
	}

	private String _getType(String mimeType) {
		if (ContentTypes.TEXT_HTML.equals(mimeType)) {
			return "HTML";
		}

		return "PLAIN_TEXT";
	}

	@Reference
	private Http _http;

}