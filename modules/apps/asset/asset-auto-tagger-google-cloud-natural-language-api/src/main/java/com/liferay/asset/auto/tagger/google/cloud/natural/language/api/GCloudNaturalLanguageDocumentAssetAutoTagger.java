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

package com.liferay.asset.auto.tagger.google.cloud.natural.language.api;

import com.liferay.asset.auto.tagger.google.cloud.natural.language.api.configuration.GCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration;

import java.util.Collection;

/**
 * Models a Google Cloud Natural Language Document Asset Auto Tagger.
 *
 * @author Cristina González
 * @review
 */
public interface GCloudNaturalLanguageDocumentAssetAutoTagger {

	/**
	 * Returns a list of tag names from Google Cloud Natural Language
	 * Classification API.
	 *
	 * @param gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration the configuratio.
	 * @param content the text to be tagged.
	 * @return a list of tag names.
	 * @review
	 */
	public Collection<String> getClassificationTagNames(
			GCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration
				gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration,
			String content)
		throws Exception;

	/**
	 * Returns a list of tag names from Google Cloud Natural Language
	 * Entity API.
	 *
	 * @param gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration the configuratio.
	 * @param content the text to be tagged.
	 * @return a list of tag names.
	 * @review
	 * */
	public Collection<String> getEntityTagNames(
			GCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration
				gCloudNaturalLanguageAssetAutoTagProviderCompanyConfiguration,
			String content)
		throws Exception;

	/**
	 * Returns the content truncated to the maximum size of the mimeType.
	 *
	 * @param mimeType the mimeType of the content.
	 * @param content the text to be truncated.
	 * @return a truncated string.
	 * @review
	 */
	public String getTruncatedContent(String mimeType, String content);

}