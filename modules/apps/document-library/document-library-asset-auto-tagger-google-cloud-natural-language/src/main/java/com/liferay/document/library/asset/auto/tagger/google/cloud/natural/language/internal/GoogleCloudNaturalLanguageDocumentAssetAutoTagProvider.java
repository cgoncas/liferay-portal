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

package com.liferay.document.library.asset.auto.tagger.google.cloud.natural.language.internal;

import com.liferay.asset.auto.tagger.AssetAutoTagProvider;
import com.liferay.document.library.asset.auto.tagger.google.cloud.natural.language.internal.configuration.GoogleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration;
import com.liferay.document.library.asset.auto.tagger.google.cloud.natural.language.internal.constants.GoogleCloudNaturalLanguageAssetAutoTagProviderConstants;
import com.liferay.document.library.asset.auto.tagger.google.cloud.natural.language.internal.util.GoogleCloudNaturalLanguageUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.repository.capabilities.TemporaryFileEntriesCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.InputStream;

import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = AssetAutoTagProvider.class
)
public class GoogleCloudNaturalLanguageDocumentAssetAutoTagProvider
	implements AssetAutoTagProvider<FileEntry> {

	@Override
	public List<String> getTagNames(FileEntry fileEntry) {
		List<String> tagList = new ArrayList<>();

		try {
			GoogleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration
				googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration =
					_getConfiguration(fileEntry);

			if (!_isTemporary(fileEntry) && _isSupportedFormat(fileEntry)) {
				FileVersion fileVersion = fileEntry.getFileVersion();
				Set<String> tags = new HashSet<>();

				InputStream contentStream = fileVersion.getContentStream(false);

				byte[] bytes = FileUtil.getBytes(contentStream);

				String contentText = new String(bytes);

				if (googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
						classificationEndpointEnabled()) {

					List<String> splitedTexts =
						GoogleCloudNaturalLanguageUtil.splitTextToMaxSizeCall(
							contentText,
							GoogleCloudNaturalLanguageAssetAutoTagProviderConstants.MAX_CHARACTERS_SERVICE);

					for (String payloadFragment : splitedTexts) {
						JSONObject responseJSONObject =
							_queryCloudNaturalLanguageJSONObject(
								googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
									apiKey(),
								payloadFragment, "classifyText");

						JSONArray responsesJSONArray =
							responseJSONObject.getJSONArray("categories");

						if ((responsesJSONArray != null) &&
							(responsesJSONArray.length() > 0)) {

							for (int i = 0; i < responsesJSONArray.length();
								 i++) {

								JSONObject tagCandidate =
									responsesJSONArray.getJSONObject(i);

								String tag = tagCandidate.getString("name");

								_clearDivideTags(tags, tag);
							}

							tagList.addAll(tags);
						}
					}
				}

				if (googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
						entityEndpointEnabled()) {

					List<String> splitedTexts =
						GoogleCloudNaturalLanguageUtil.splitTextToMaxSizeCall(
							contentText,
							GoogleCloudNaturalLanguageAssetAutoTagProviderConstants.MAX_CHARACTERS_SERVICE);

					for (String payloadFragment : splitedTexts) {
						JSONObject responseJSONObject =
							_queryCloudNaturalLanguageJSONObject(
								googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
									apiKey(),
								payloadFragment, "analyzeEntities");

						JSONArray responsesJSONArray =
							responseJSONObject.getJSONArray("entities");

						if ((responsesJSONArray != null) &&
							(responsesJSONArray.length() > 0)) {

							for (int i = 0; i < responsesJSONArray.length();
								 i++) {

								JSONObject tagCandidate =
									responsesJSONArray.getJSONObject(i);

								String salience = tagCandidate.getString(
									"salience");

								double salienceValue = 0D;

								if (!salience.isEmpty()) {
									salienceValue = Double.parseDouble(
										salience);
								}

								if (salienceValue >
										googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
											salience()) {

									String tag = tagCandidate.getString("name");

									_clearDivideTags(tags, tag);
								}
							}
						}

						tagList.addAll(tags);
					}
				}
			}
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		return tagList;
	}

	private void _clearDivideTags(Set<String> tags, String untratedTag) {
		untratedTag.replaceAll(StringPool.DASH, StringPool.BLANK);
		untratedTag.replaceAll(StringPool.APOSTROPHE, StringPool.BLANK);

		if (!untratedTag.isEmpty()) {
			String[] split = untratedTag.split(StringPool.AMPERSAND, -1);

			List<String> candidates = Stream.of(
				split
			).flatMap(
				elem -> Stream.of(elem.split(StringPool.FORWARD_SLASH, 0))
			).filter(
				tagCandidate -> !tagCandidate.isEmpty()
			).map(
				String::trim
			).collect(
				Collectors.toList()
			);

			tags.addAll(candidates);
		}
	}

	private GoogleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration
			_getConfiguration(FileEntry fileEntry)
		throws ConfigurationException {

		return _configurationProvider.getConfiguration(
			GoogleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
				class,
			new CompanyServiceSettingsLocator(
				fileEntry.getCompanyId(),
				GoogleCloudNaturalLanguageAssetAutoTagProviderConstants.
					SERVICE_NAME));
	}

	private boolean _isSupportedFormat(FileEntry fileEntry) {
		String extension = fileEntry.getExtension();

		return _supportedFormats.contains(StringUtil.toUpperCase(extension));
	}

	private boolean _isTemporary(FileEntry fileEntry) {
		return fileEntry.isRepositoryCapabilityProvided(
			TemporaryFileEntriesCapability.class);
	}

	private JSONObject _queryCloudNaturalLanguageJSONObject(
			String apiKey, String payloadJSON, String endpoint)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
		options.setBody(
			payloadJSON, ContentTypes.APPLICATION_JSON, StringPool.UTF8);
		options.setLocation(
			StringBundler.concat(
				"https://language.googleapis.com/v1/documents:", endpoint,
				"?key=", apiKey));
		options.setPost(true);

		String responseJSON = _http.URLtoString(options);

		Http.Response response = options.getResponse();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(responseJSON);

		if (response.getResponseCode() != HttpURLConnection.HTTP_OK) {
			JSONObject errorJSONObject = jsonObject.getJSONObject("error");

			String errorMessage = responseJSON;

			if (errorJSONObject != null) {
				errorMessage = errorJSONObject.getString("message");
			}

			throw new PortalException(
				StringBundler.concat(
					"Response code ", response.getResponseCode(), ": ",
					errorMessage));
		}

		return jsonObject;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GoogleCloudNaturalLanguageDocumentAssetAutoTagProvider.class);

	private static final Set<String> _supportedFormats = new HashSet<>(
		Arrays.asList("TXT"));

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Http _http;

}