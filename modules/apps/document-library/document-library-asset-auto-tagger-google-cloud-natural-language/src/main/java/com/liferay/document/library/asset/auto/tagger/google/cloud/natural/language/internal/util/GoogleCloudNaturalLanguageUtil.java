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

package com.liferay.document.library.asset.auto.tagger.google.cloud.natural.language.internal.util;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alicia García
 */
public class GoogleCloudNaturalLanguageUtil {

	public static List<String> splitTextToMaxSizeCall(
		String contentText, int max) {

		List<String> matchList = new ArrayList<>();

		Pattern regex = Pattern.compile(
			".{1," + max + "}(?:\\s|$)", Pattern.DOTALL);

		Matcher regexMatcher = regex.matcher(contentText);

		while (regexMatcher.find()) {
			matchList.add(_getAnnotateDocumentPayload(regexMatcher.group()));
		}

		return matchList;
	}

	private static String _getAnnotateDocumentPayload(String content) {
		JSONObject jsonObject = JSONUtil.put(
			"document",
			JSONUtil.put(
				"type", "PLAIN_TEXT"
			).put(
				"content", content
			));

		return jsonObject.toString();
	}

}