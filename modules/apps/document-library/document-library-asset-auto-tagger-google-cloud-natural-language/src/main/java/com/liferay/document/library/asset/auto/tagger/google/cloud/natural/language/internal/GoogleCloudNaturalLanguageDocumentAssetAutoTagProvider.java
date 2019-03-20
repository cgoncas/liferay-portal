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
import com.liferay.petra.string.CharPool;
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
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
	public Collection<String> getTagNames(FileEntry fileEntry) {
		try {
			GoogleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration
				googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration =
					_getConfiguration(fileEntry);

			FileVersion fileVersion = fileEntry.getFileVersion();

			String fileName = fileVersion.getFileName();

			if (_isTemporary(fileEntry) ||
				!_isSupportedFormat(
					fileVersion.getContentStream(false), fileName)) {

				return Collections.emptyList();
			}

			if (!googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
					classificationEndpointEnabled() &&
				!googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
					entityEndpointEnabled()) {

				return Collections.emptyList();
			}

			Set<String> tags = new HashSet<>();

			String type = _getFileEntryType(fileEntry);

			int size =
				GoogleCloudNaturalLanguageAssetAutoTagProviderConstants.
					MAX_CHARACTERS_SERVICE - _MINIMUM_PAYLOAD_SIZE -
						type.length();

			String truncatedContent =
				GoogleCloudNaturalLanguageUtil.truncateToSize(
					_getFileEntryContent(fileEntry), size);

			String documentPayload =
				GoogleCloudNaturalLanguageUtil.getDocumentPayload(
					truncatedContent, type);

			float limitSalience =
				googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
					salience();

			float limitConfidence =
				googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
					confidence();

			String apiKey =
				googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
					apiKey();

			if (googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
					classificationEndpointEnabled()) {

				_getContextTags(
					tags, documentPayload, apiKey, "name", "classifyText",
					"categories", "confidence", limitConfidence);
			}

			if (googleCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
					entityEndpointEnabled()) {

				_getContextTags(
					tags, documentPayload, apiKey, "name", "analyzeEntities",
					"entities", "salience", limitSalience);
			}

			return tags;
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		return Collections.emptyList();
	}

	private void _clearDivideTags(Set<String> tags, String tag) {
		tag = StringUtil.removeChars(tag, CharPool.APOSTROPHE, CharPool.DASH);

		if (tag.isEmpty()) {
			return;
		}

		Stream.of(
			tag.split(StringPool.AMPERSAND, -1)
		).flatMap(
			elem -> Stream.of(elem.split(StringPool.FORWARD_SLASH, 0))
		).map(
			String::trim
		).filter(
			tagCandidate -> !tagCandidate.isEmpty()
		).forEach(
			tags::add
		);
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

	private void _getContextTags(
			Set<String> tags, String documentPayload, String apiKey,
			String name, String analyzeEntitiesEndpoint, String entities,
			String salienceField, float endpointAcceptance)
		throws Exception {

		JSONObject responseJSONObject = _queryCloudNaturalLanguageJSONObject(
			apiKey, documentPayload, analyzeEntitiesEndpoint);

		JSONArray responsesJSONArray = responseJSONObject.getJSONArray(
			entities);

		if (responsesJSONArray == null) {
			return;
		}

		for (int i = 0; i < responsesJSONArray.length(); i++) {
			JSONObject tagCandidate = responsesJSONArray.getJSONObject(i);

			double acceptance = GetterUtil.getDouble(
				tagCandidate.getString(salienceField));

			if (acceptance > endpointAcceptance) {
				String tag = tagCandidate.getString(name);

				_clearDivideTags(tags, tag);
			}
		}
	}

	private String _getFileEntryContent(FileEntry fileEntry)
		throws IOException, PortalException {

		FileVersion fileVersion = fileEntry.getFileVersion();

		try (InputStream is = fileVersion.getContentStream(false)) {
			return FileUtil.extractText(is, fileVersion.getFileName());
		}
	}

	private String _getFileEntryType(FileEntry fileEntry) {
		if (_htmlContentTypes.contains(fileEntry.getMimeType())) {
			return "HTML";
		}

		return "PLAIN_TEXT";
	}

	private boolean _isSupportedFormat(
		InputStream contentStream, String fileName) {

		String contentType = MimeTypesUtil.getContentType(
			contentStream, fileName);

		return _supportedContentTypes.contains(contentType);
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
				"Cannot generate tags with Google Natural Language service; ",
				"Response code ", response.getResponseCode(), ": ",
				errorMessage));
	}

	private static final int _MINIMUM_PAYLOAD_SIZE;

	private static final Log _log = LogFactoryUtil.getLog(
		GoogleCloudNaturalLanguageDocumentAssetAutoTagProvider.class);

	private static final Set<String> _htmlContentTypes = new HashSet<>(
		Arrays.asList(ContentTypes.TEXT_HTML, ContentTypes.TEXT_HTML_UTF8));
	private static final Set<String> _supportedContentTypes = new HashSet<>(
		Arrays.asList(
			ContentTypes.TEXT_PLAIN, ContentTypes.APPLICATION_TEXT,
			ContentTypes.APPLICATION_MSWORD, ContentTypes.APPLICATION_PDF,
			ContentTypes.TEXT_HTML, ContentTypes.TEXT_HTML_UTF8,
			"application/epub+zip", "application/vnd.apple.pages.13",
			"application/vnd.google-apps.document",
			"application/vnd.openxmlformats-officedocument.wordprocessingml." +
				"document"));

	static {
		String payload = GoogleCloudNaturalLanguageUtil.getDocumentPayload(
			StringPool.BLANK, StringPool.BLANK);

		_MINIMUM_PAYLOAD_SIZE = payload.length();
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Http _http;

}