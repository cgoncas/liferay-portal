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
	 * @param apiKey the API key from Google Cloud Natural Language.
	 * @param confidence the classifier's confidence of the category.
	 * @param content the text to be tagged.
	 * @return a list of tag names.
	 * @review
	 */
	public Collection<String> getClassificationTagNames(
			String apiKey, float confidence, String content)
		throws Exception;

}