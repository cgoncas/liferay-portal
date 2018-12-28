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

package com.liferay.portal.apio.test.util;

import java.io.File;

/**
 * @author Víctor Galán
 */
public class MediaObjectTestUtil {

	public static String createDocumentInFolder(
		String folderDocumentsHref, File file) {

		return ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).multipart(
			"binaryFile", file
		).when(
		).post(
			folderDocumentsHref
		).then(
		).extract(
		).path(
			"_links.self.href"
		);
	}

	public static String createDocumentInRootFolder(
		String contentSpaceHref, File file) {

		String documentsHref = ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).when(
		).get(
			contentSpaceHref
		).follow(
			"_links.documentsRepository.href"
		).then(
		).extract(
		).path(
			"_links.documents.href"
		);

		return createDocumentInFolder(documentsHref, file);
	}

}