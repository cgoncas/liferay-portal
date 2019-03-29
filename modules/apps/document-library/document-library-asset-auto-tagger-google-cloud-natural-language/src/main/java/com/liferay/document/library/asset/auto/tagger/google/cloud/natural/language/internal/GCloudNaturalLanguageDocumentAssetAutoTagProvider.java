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
import com.liferay.asset.auto.tagger.google.cloud.natural.language.api.GCloudNaturalLanguageDocumentAssetAutoTagger;
import com.liferay.document.library.asset.auto.tagger.google.cloud.natural.language.internal.configuration.GCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration;
import com.liferay.document.library.asset.auto.tagger.google.cloud.natural.language.internal.constants.GCloudNaturalLanguageAssetAutoTagProviderConstants;
import com.liferay.portal.kernel.exception.PortalException;
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

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = AssetAutoTagProvider.class
)
public class GCloudNaturalLanguageDocumentAssetAutoTagProvider
	implements AssetAutoTagProvider<FileEntry> {

	@Override
	public Collection<String> getTagNames(FileEntry fileEntry) {
		try {
			return _getTagNames(fileEntry);
		}
		catch (Exception e) {
			_log.error(e, e);

			return Collections.emptyList();
		}
	}

	private GCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration
			_getConfiguration(FileEntry fileEntry)
		throws ConfigurationException {

		return _configurationProvider.getConfiguration(
			GCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.class,
			new CompanyServiceSettingsLocator(
				fileEntry.getCompanyId(),
				GCloudNaturalLanguageAssetAutoTagProviderConstants.
					SERVICE_NAME));
	}

	private String _getFileEntryContent(FileEntry fileEntry)
		throws IOException, PortalException {

		FileVersion fileVersion = fileEntry.getFileVersion();

		String mimeType = fileVersion.getMimeType();

		if (mimeType.equals(ContentTypes.TEXT_PLAIN) ||
			mimeType.equals(ContentTypes.TEXT_HTML)) {

			try (InputStream inputStream = fileVersion.getContentStream(
					false)) {

				return new String(
					FileUtil.getBytes(inputStream), StandardCharsets.UTF_8);
			}
		}

		try (InputStream inputStream = fileVersion.getContentStream(false)) {
			return FileUtil.extractText(inputStream, fileVersion.getFileName());
		}
	}

	private Collection<String> _getTagNames(FileEntry fileEntry)
		throws Exception {

		if (fileEntry.isRepositoryCapabilityProvided(
				TemporaryFileEntriesCapability.class) ||
			!_supportedContentTypes.contains(fileEntry.getMimeType())) {

			return Collections.emptyList();
		}

		GCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration
			gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration =
				_getConfiguration(fileEntry);

		if (!gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
				classificationEndpointEnabled() &&
			!gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
				entityEndpointEnabled()) {

			return Collections.emptyList();
		}

		Set<String> tagNames = new HashSet<>();

		String apiKey =
			gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
				apiKey();

		String documentPayload =
			_gCloudNaturalLanguageDocumentAssetAutoTagger.getTruncatedContent(
				fileEntry.getMimeType(), _getFileEntryContent(fileEntry));

		if (gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
				classificationEndpointEnabled()) {

			float confidence =
				gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
					confidence();

			tagNames.addAll(
				_gCloudNaturalLanguageDocumentAssetAutoTagger.
					getClassificationTagNames(
						apiKey, confidence, documentPayload));
		}

		if (gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
				entityEndpointEnabled()) {

			float salience =
				gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration.
					salience();

			tagNames.addAll(
				_gCloudNaturalLanguageDocumentAssetAutoTagger.getEntityTagNames(
					apiKey, salience, documentPayload));
		}

		return tagNames;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GCloudNaturalLanguageDocumentAssetAutoTagProvider.class);

	private static final Set<String> _supportedContentTypes = new HashSet<>(
		Arrays.asList(
			"application/epub+zip", "application/vnd.apple.pages.13",
			"application/vnd.google-apps.document",
			"application/vnd.openxmlformats-officedocument.wordprocessingml." +
				"document",
			ContentTypes.APPLICATION_MSWORD, ContentTypes.APPLICATION_PDF,
			ContentTypes.APPLICATION_TEXT, ContentTypes.TEXT_HTML,
			ContentTypes.TEXT_PLAIN));

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GCloudNaturalLanguageDocumentAssetAutoTagger
		_gCloudNaturalLanguageDocumentAssetAutoTagger;

}